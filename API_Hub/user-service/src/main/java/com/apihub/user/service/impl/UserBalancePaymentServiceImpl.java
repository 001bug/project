package com.apihub.user.service.impl;

import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.BeanUtils;
import com.apihub.common.utils.UserHolder;
import com.apihub.user.mapper.UserBalancePaymentMapper;
import com.apihub.user.model.entity.UserBalancePayment;
import com.apihub.user.model.vo.UserBalanceVO;
import com.apihub.user.service.UserBalancePaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static com.apihub.common.utils.RedisConstants.USER_BALANCE_KEY;

/**
 * @author IKUN
 * @description 针对表【user_balance_payment(用户余额表)】的数据库操作Service实现
 * @createDate 2023-11-02 10:29:07
 */
@Slf4j
@Service
public class UserBalancePaymentServiceImpl extends ServiceImpl<UserBalancePaymentMapper, UserBalancePayment>
        implements UserBalancePaymentService {

    @Resource
    private UserBalancePaymentMapper userBalancePaymentMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 预加载脚本
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static {
        SECKILL_SCRIPT = new DefaultRedisScript();
        ClassPathResource resource = new ClassPathResource("deductAmount.lua");
        SECKILL_SCRIPT.setLocation(resource);
        SECKILL_SCRIPT.setResultType(Long.class);
    }


    @Override
    public Boolean deductBalance(Integer amount) {
        log.info("开始扣款");
        UserBalancePayment userBalancePayment = getUserBalancePayment();

//        会有线程安全问题
//        if (userBalancePayment.getBalance() < amount) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "余额不足");
//        }
//        //扣减余额
//        userBalancePayment.setBalance(userBalancePayment.getBalance() - amount);

        // 测试
//        Long userId = 1L;
        Long userId = userBalancePayment.getUserId();

        // 调用lua脚本, 直接在Redis里面更新
        Long res = stringRedisTemplate.execute(SECKILL_SCRIPT,
                Collections.emptyList(),
                String.valueOf(userId), String.valueOf(amount));

//        return this.updateById(userBalancePayment);

        return res == 0 ? Boolean.TRUE : Boolean.FALSE ;
    }

    @Override
    public Boolean chargeBalance(Integer amount) {
        log.info("开始充值,充值用户Id：" + UserHolder.getUser() + "充值金额：" + amount);
        if (UserHolder.getUser() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        QueryWrapper<UserBalancePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", UserHolder.getUser());
        UserBalancePayment userBalancePayment = userBalancePaymentMapper.selectOne(queryWrapper);
        if (userBalancePayment.getUserId() == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未查询到用户余额信息");
        }
        //扣减余额
        userBalancePayment.setBalance(userBalancePayment.getBalance() + amount);
        return this.updateById(userBalancePayment);
    }

    @Override
    public UserBalanceVO getBalance(HttpServletRequest request) {
        UserBalancePayment userBalancePayment = getUserBalancePayment();
        return BeanUtils.toBean(userBalancePayment, UserBalanceVO.class);
    }

    /**
     * 查询用户余额
     * 如果Redis里面没有，到MySQL里面查询，然后放到Redis
     * 如果MySQL里面没有，才抛出异常
     * @return
     */
    private UserBalancePayment getUserBalancePayment() {

        Long userId = UserHolder.getUser();
        String balanceKey = USER_BALANCE_KEY + userId;
        String amount = stringRedisTemplate.opsForValue().get(balanceKey);
        UserBalancePayment userBalancePayment = new UserBalancePayment();
        // 缓存里面不存在
        if (StringUtils.isBlank(amount)) {
            QueryWrapper<UserBalancePayment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId);
            userBalancePayment = userBalancePaymentMapper.selectOne(queryWrapper);
            if (userBalancePayment == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未查询到用户余额信息");
            }
            // 数据库里面存在，放到缓存里面
            stringRedisTemplate.opsForValue().set(balanceKey, String.valueOf(userBalancePayment.getBalance()));
        } else {
            userBalancePayment.setBalance(Long.valueOf(amount));
        }
        userBalancePayment.setUserId(userId);

        return userBalancePayment;
    }

}




