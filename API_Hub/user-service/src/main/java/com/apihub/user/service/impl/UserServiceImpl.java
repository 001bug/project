package com.apihub.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.user.config.JwtProperties;
import com.apihub.user.mapper.UserMapper;
import com.apihub.user.model.dto.*;
import com.apihub.user.model.entity.User;
import com.apihub.user.model.entity.UserBalancePayment;
import com.apihub.user.model.vo.UserKeyPairVO;
import com.apihub.user.model.vo.UserLoginVO;
import com.apihub.user.model.vo.UserVO;
import com.apihub.user.service.UserService;
import com.apihub.user.utils.JwtTool;
import com.apihub.user.utils.MailUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.apihub.common.utils.RedisConstants.*;
import static com.apihub.user.utils.UserConstant.*;

/**
 * @author IKUN
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2023-10-22 23:40:52
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserBalancePaymentServiceImpl userBalancePaymentService;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        User user = userRegisterCommonMethod(userAccount, userPassword, null, null);

        return user.getId();

    }

    private User userRegisterCommonMethod(String userAccount, String userPassword, String avatarUrl, String unionId) {
        // 使用分布式锁防止并发注册
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(REGISTER_LOCK_KEY + userAccount, "locked");

        // 如果加锁失败，说明正在注册中，直接返回，不走try里面的逻辑
        if (Boolean.FALSE.equals(flag)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统繁忙中，请稍后重试");
        }
        try {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((MD5_SALT + userPassword).getBytes());
            // 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(MD5_SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(MD5_SALT + userAccount + RandomUtil.randomNumbers(8));

            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            if (StrUtil.isNotBlank(avatarUrl)) {
                user.setUserAvatar(avatarUrl);
            }
            if (StrUtil.isNotBlank(unionId)) {
                user.setUnionId(unionId);
            }
            user.setUserRole(DEFAULT_ROLE);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }

            //5. 同时注册用户的余额信息
            UserBalancePayment userBalance = new UserBalancePayment();
            userBalance.setId(user.getId());
            userBalance.setUserId(user.getId());
            userBalance.setBalance(0L);
            userBalance.setScore(0L);
            userBalance.setExpenseAmount(0L);
            userBalance.setExpenseScore(0L);
            userBalance.setFrozenAmount(0L);
            userBalancePaymentService.save(userBalance);

            return user;
        } finally {
            //释放锁
            stringRedisTemplate.delete(REGISTER_LOCK_KEY + userAccount);
        }
    }

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;

    @Override
    public UserLoginVO login(LoginFormDTO loginFormDTO) {

        String userAccount = loginFormDTO.getUserAccount();
        String userPassword = loginFormDTO.getUserPassword();
        //可以添加密码要求

        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((MD5_SALT + userPassword).getBytes());//md5加salt加密

        // 2. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);//这里可以加一个判断优化,如果没有该用户直接返回
        User user = userMapper.selectOne(queryWrapper);//约等于sql: select * from user where .. and..

        return loginCommonMethod(user);
    }

    private UserLoginVO loginCommonMethod(User user) {
        // 用户不存在
        if (user == null) {
            log.info("用户不存在或密码错误");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        if (user.getUserRole().equals(BAN_ROLE)) {//接口中的变量属性默认是static final修饰的
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "被封禁");
        }

        // 5.生成TOKEN
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());//传入userid,tokenTTL

        // 6.封装VO
        UserLoginVO userLoginVo;
        //新,复制用户信息,将user转换为UserLoginVo,屏蔽敏感信息
        userLoginVo = BeanUtil.copyProperties(user, UserLoginVO.class);

        //在redis中，以accessKey为Key，存储sign(签名)
        String accessKey = user.getAccessKey();
        //秘钥加密
//        Digester md5 = new Digester(DigestAlgorithm.SHA256);
//        String sign = "hekmatyar" + "." + user.getSecretKey();
//        sign = md5.digestHex(sign);
//        user.setSecretKey(sign);

        //7.保存所有用户信息到 redis中
        Map<String, Object> userMap = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue != null ? fieldValue.toString() : ""));

        String key = LOGIN_USER_KEY + user.getId();
        //以userId为key存一份user的信息
        stringRedisTemplate.opsForHash().putAll(key, userMap);
        // 7.4.设置token有效期   用户信息存入 Redis 后，LOGIN_USER_TTL 分钟后自动删除（表示 Token 过期时间）。
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);

        //以accessKey作为key存一份(id和secreKey)(两个作用，存在的话就是刷新有效期，不存在的话就是补充)
        HashMap<String, Object> userAkInfo = new HashMap<>();
        userAkInfo.put("id", String.valueOf(user.getId()));
        userAkInfo.put("secretKey", user.getSecretKey());
        //创建api访问密钥,在访问api时,使用accessKey身份验证
        stringRedisTemplate.opsForHash().putAll(API_ACCESS_KEY + accessKey, userAkInfo);


        // 保存用户余额
        String blcKey = USER_BALANCE_KEY + user.getId();

        // 查询balance数据库(UserBalancePaymentServiceImpl有getBalance函数，但是是通过UserHolder获取用户id的)
        // 网关没有拦截，也就没有传递user-id，此时UserHolder里面就没有user-id
        QueryWrapper<UserBalancePayment> balanceQuery = new QueryWrapper<>();
        balanceQuery.eq("userId", user.getId());
        UserBalancePayment balanceInfo = userBalancePaymentService.getOne(balanceQuery);


        stringRedisTemplate.opsForValue().set(blcKey, String.valueOf(balanceInfo.getBalance()));


        //Todo 可将此时间设置长一些
        stringRedisTemplate.expire(API_ACCESS_KEY + accessKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

        // 用户余额的TTL设置成26h
        stringRedisTemplate.expire(blcKey, USER_BALANCE_TTL, TimeUnit.HOURS);

        //以token为key存一份，为了跨域
        //如果跨域问题解决了，此处可以删除
        stringRedisTemplate.opsForValue().set(USER_TOKEN_KEY + token, user.getId().toString(), LOGIN_USER_TTL, TimeUnit.MINUTES);

        // 返回token+user信息
        userLoginVo.setToken(token);

        return userLoginVo;
    }

    @Override
    public UserVO getLoginUser() {
        Long userId = UserHolder.getUser();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }

        String tokenKey = LOGIN_USER_KEY + userId;

        //如果不想用redis,将下列代码改造即可
        //尝试使用redis查询用户信息，如果没有查询到，则通过MySQL查询到后，重新生成到redis中
        Map<Object, Object> userMap;
        try {
            userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        } catch (Exception e) {
            //throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
            User user = this.getById(userId);
            if (user == null) {
                log.info("用户不存在");
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
            }
            if (user.getUserRole().equals(BAN_ROLE)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "被封禁");
            }
            UserVO userVo;
            userVo = BeanUtil.copyProperties(user, UserVO.class);

            //不想使用redis，把这个注释掉
            Map<String, Object> userVoMap = BeanUtil.beanToMap(userVo, new HashMap<>(),
                    CopyOptions.create()
                            .setIgnoreNullValue(true)
                            .setFieldValueEditor((fieldName, fieldValue) -> fieldValue != null ? fieldValue.toString() : ""));

            String key = LOGIN_USER_KEY + user.getId();
            stringRedisTemplate.opsForHash().putAll(key, userVoMap);
            // 设置token有效期
            stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);

            return userVo;
        }
        if (userMap.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未查询到登录信息，请重新登录");
        }

//

        if (userMap.get("userRole").equals(BAN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "被封禁");
        }
        //刷新redis存储时间
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

        return BeanUtil.fillBeanWithMap(userMap, new UserVO(), false);
    }


    @Override
    public Boolean updatePassword(Long userId, UserUpdatePasswordRequest userUpdatePasswordRequest) {
        if (userUpdatePasswordRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String oldPassword = userUpdatePasswordRequest.getOldPassword();
        String newPassword = userUpdatePasswordRequest.getNewPassword();
        // 查询用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String encryptOldPassword = user.getUserPassword();
        String encryptOldPassword2 = DigestUtils.md5DigestAsHex((MD5_SALT + oldPassword).getBytes());

        if (!encryptOldPassword.equals(encryptOldPassword2)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原密码错误");
        }
        String encryptNewPassword = DigestUtils.md5DigestAsHex((MD5_SALT + newPassword).getBytes());
        user.setUserPassword(encryptNewPassword);

        return this.updateById(user);
    }

    @Override
    public Boolean checkUserAK(String accessKey, String sign) {
        //获取用户，并检查是否封禁
        Long userId = UserHolder.getUser();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }

        String tokenKey = LOGIN_USER_KEY + userId;

        Map<Object, Object> userMap;
        userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        if (userMap.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户数据为空");
        }
        if (userMap.get("userRole").equals(BAN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "被封禁");
        }


        return Objects.equals(sign, stringRedisTemplate.opsForValue().get(API_ACCESS_KEY + accessKey));
    }

    @Override
    public UserKeyPairVO changeKeyPair(LoginFormDTO loginFormDTO) {
        if (loginFormDTO == null || StringUtils.isBlank(loginFormDTO.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数有误");
        }

        // 获取当前登录用户信息(拿到userAccount)
        UserVO loginUser = getLoginUser();

        String userAccount = loginUser.getUserAccount();

        // 方便测试使用
        //String userAccount = "root";
        String userPassword = loginFormDTO.getUserPassword();
        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((MD5_SALT + userPassword).getBytes());

        // 生成一份新的ak/sk
        String accessKey = DigestUtil.md5Hex(MD5_SALT + userAccount + RandomUtil.randomNumbers(5));
        String secretKey = DigestUtil.md5Hex(MD5_SALT + userAccount + RandomUtil.randomNumbers(8));


        //  update user
        //  set accessKey = ?  and secretKey = ?
        //  where  userAccount = ?  and userPassword = ?
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("userAccount", userAccount);
        wrapper.eq("userPassword", encryptPassword);
        wrapper.set("accessKey", accessKey);
        wrapper.set("secretKey", secretKey);

        boolean update = this.update(wrapper);
        if (!update) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        UserKeyPairVO keyPairVO = new UserKeyPairVO();
        keyPairVO.setSecreteKey(secretKey);
        keyPairVO.setAccessKey(accessKey);

        return keyPairVO;
    }

    @Autowired
    private MailUtil mailUtil;

    /**
     * 查询当前用户ak/sk
     * todo 可以加入redis优化查询
     *
     * @return
     */
    @Override
    public UserKeyPairVO getKeyPair() {
        UserVO loginUser = getLoginUser();
        Long id = loginUser.getId();

        User user = getOne(new QueryWrapper<User>().eq("id", id));

        UserKeyPairVO keyPairVO = new UserKeyPairVO();
        keyPairVO.setAccessKey(user.getAccessKey());
        keyPairVO.setSecreteKey(user.getSecretKey());

        return keyPairVO;
    }

    @Override
    public Boolean getCodeForBindEmail(GetCodeForBindEmailRequest getCodeForBindEmailRequest) {
        if (getCodeForBindEmailRequest.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户id不存在");
        }

        //检查email是否符合邮件格式
        String email = getCodeForBindEmailRequest.getEmail();
        if (!mailUtil.isValidEmail(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮件格式错误");
        }

        //产生六位数验证码
        int code = new Random().nextInt(900000) + 100000;

        log.info("发送验证码，邮箱地址" + email + "验证码为：" + code);
        boolean b = mailUtil.sendMailMessage(email, "绑定邮箱验证码", "您的验证码为：" + code + "，请在5分钟内完成验证");

        if (b) {
            //发送成功，将验证码存入redis中，设置5分钟过期时间
            //key为：USER_EMAIL_CODE_KEY+email+userid
            stringRedisTemplate.opsForValue().set(USER_EMAIL_BIND_CODE_KEY + email + ":" + getCodeForBindEmailRequest.getUserId()
                    , String.valueOf(code), 5, TimeUnit.MINUTES);
        }
        return b;
    }

    @Override
    public Boolean verifyCodeForBindEmail(VerifyCodeForBindEmailRequest newVerifyCodeForBindEmailRequest) {
        if (newVerifyCodeForBindEmailRequest.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户id不存在");
        }
        if (newVerifyCodeForBindEmailRequest.getCode() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不存在");
        }
        //检查email是否符合邮件格式
        String email = newVerifyCodeForBindEmailRequest.getEmail();
        if (!mailUtil.isValidEmail(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮件格式错误");
        }

        String key = USER_EMAIL_BIND_CODE_KEY
                + email + ":" + newVerifyCodeForBindEmailRequest.getUserId();

        //检查验证码
        String code = stringRedisTemplate.opsForValue().get(key);
        if (code == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "杨正吗未发送或验证码已过期");
        } else {
            if (!code.equals(newVerifyCodeForBindEmailRequest.getCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
            }
        }

        //验证码验证通过后，删除验证码
        stringRedisTemplate.delete(key);

        //将邮件绑定到用户信息中
        User user = this.getById(newVerifyCodeForBindEmailRequest.getUserId());
        user.setMpOpenId(email);
        boolean b = this.updateById(user);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "绑定邮箱失败");
        }

        //将邮箱信息更新到redis中
        String redisKey = LOGIN_USER_KEY
                + newVerifyCodeForBindEmailRequest.getUserId();
        Map<String, Object> userMap = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue != null ? fieldValue.toString() : ""));
        stringRedisTemplate.opsForHash().putAll(redisKey, userMap);
        //无需刷新时间

        return true;
    }

    @Override
    public Boolean sendEmailCodeForResetPassword(EmailCodeForResetPasswordRequest newEmailCodeForResetPasswordRequest) {
        String email = newEmailCodeForResetPasswordRequest.getEmail();
        Long userId = newEmailCodeForResetPasswordRequest.getUserId();

        // 验证email与userId是否匹配
        User user = this.getById(userId);
        if (!user.getMpOpenId().equals(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "email与userId不匹配");
        }

        String redisKey = USER_RESET_PASSWORD_EMAILCODE_KEY + email;

        sendEmailCode(email, redisKey, "通过邮箱验证码重置密码");

        return true;
    }

    private void sendEmailCode(String email, String redisKey, String textSubject) {
        if (!mailUtil.isValidEmail(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮件格式错误");
        }

        // 查询是否已经生成了验证码
        String checkCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (checkCode != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码生成频率太高，请稍后再试");
        }

        //产生六位数验证码
        int code = new Random().nextInt(900000) + 100000;

        log.info("发送验证码，邮箱地址" + email + "验证码为：" + code);
        boolean b = mailUtil.sendMailMessage(
                email, textSubject, "您正在" + textSubject + ",您的验证码为：" + code + "，请在5分钟内完成验证");

        if (b) {
            //发送成功，将验证码存入redis中，设置5分钟过期时间
            //key为：USER_EMAIL_CODE_KEY+email+userid
            stringRedisTemplate.opsForValue().set(
                    redisKey, String.valueOf(code),
                    5, TimeUnit.MINUTES);
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮件发送失败");
        }
    }

    @Override
    public Boolean verifyEmailCodeForResetPassword(VerifyCodeForResetPasswordRequest verifyCodeForResetPasswordRequest) {
        if (verifyCodeForResetPasswordRequest == null || StringUtils.isAnyBlank(
                verifyCodeForResetPasswordRequest.getEmail(), verifyCodeForResetPasswordRequest.getVerifyCode(),
                verifyCodeForResetPasswordRequest.getNewPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        String email = verifyCodeForResetPasswordRequest.getEmail();
        String verifyCode = verifyCodeForResetPasswordRequest.getVerifyCode();
        String newPassword = verifyCodeForResetPasswordRequest.getNewPassword();

        //检查新密码是否符合规范
        if (newPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码长度不能小于8位");
        }

        //检查userId与绑定邮箱是否一致
        Long userId = verifyCodeForResetPasswordRequest.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        User user = this.getById(userId);
        if (!user.getMpOpenId().equals(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "email与userId不匹配");
        }

        String redisKey = USER_RESET_PASSWORD_EMAILCODE_KEY + email;

        //检查验证码
        String code = stringRedisTemplate.opsForValue().get(redisKey);
        if (code == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "验证码未发送或验证码已过期");
        } else {
            if (!code.equals(verifyCode)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
            }
        }

        //验证码验证通过后，删除验证码
        stringRedisTemplate.delete(redisKey);

        //重置密码
        String encryptPassword = DigestUtils.md5DigestAsHex((MD5_SALT + newPassword).getBytes());
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.updateById(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "重置密码失败，数据库错误");
        }

        //用户退出登录,让用户重新登录
        logout(userId);

        return true;
    }

    @Override
    public UserLoginVO userEmailLogin(String email, String password) {
        String userEncryptPassword = DigestUtils.md5DigestAsHex((MD5_SALT + password).getBytes());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mpOpenId", email);
        queryWrapper.eq("userPassword", userEncryptPassword);
        User user = userMapper.selectOne(queryWrapper);

        return loginCommonMethod(user);
    }

    @Override
    public void logout(Long currentUserId) {
        User user;
        String tokenKey = LOGIN_USER_KEY + currentUserId;

        //获取用户信息
        Map<Object, Object> userMap = null;
        try {
            userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
            user = BeanUtil.fillBeanWithMap(userMap, new User(), false);
        } catch (Exception e) {
            //throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
            user = this.getById(currentUserId);
        }
        if (user == null) {
            log.error("用户不存在，查询id为：" + currentUserId);
            return;
        }
        //删除redis中的用户信息
        stringRedisTemplate.delete(tokenKey);
        stringRedisTemplate.delete(USER_BALANCE_KEY + currentUserId);
        if (user.getAccessKey() != null) {
            stringRedisTemplate.delete(API_ACCESS_KEY + user.getAccessKey());
        }
        //还有个以token为key的hash表
        //以token为key的hash表，是为了跨域而存在的，现在无需处理
    }

    @Override
    public UserLoginVO giteeLoginCallback(AuthResponse response) {
        log.info("【response】= {}", JSONUtil.toJsonStr(response));

        Object data = response.getData();
        String dataStr = JSONUtil.toJsonStr(data);
        Gson gson = new Gson();
        Map<String, Object> responseMapFromJson = gson.fromJson(dataStr, new TypeToken<Map<String, Object>>() {
        }.getType());
        //获取giteeId
        String id = (String) responseMapFromJson.get("uuid");
        if (StrUtil.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未查询到giteeId");
        }

        //通过unionId查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("unionId", id);
        User user = userMapper.selectOne(queryWrapper);

        //没有查询到用户，自动注册，并登录
        //默认密码为666666aa
        if (user == null) {
            String name = (String) responseMapFromJson.get("username");
            String avatarUrl = (String) responseMapFromJson.get("avatar");
            user = userRegisterCommonMethod(name, DEFAULT_PASSWORD, avatarUrl, id);
        }

        //进行登录操作

        return loginCommonMethod(user);
    }

}
