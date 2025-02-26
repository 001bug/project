package com.apihub.user.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.user.model.dto.aouth.BindGiteeRequest;
import com.apihub.user.model.dto.aouth.GiteeTokenRequest;
import com.apihub.user.model.dto.aouth.GiteeUserInfoResponse;
import com.apihub.user.model.entity.User;
import com.apihub.user.model.vo.UserLoginVO;
import com.apihub.user.model.vo.UserVO;
import com.apihub.user.service.UserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xkcoding.justauth.AuthRequestFactory;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.apihub.common.utils.RedisConstants.LOGIN_USER_KEY;

@RestController
@Slf4j
@RequestMapping("/user/oauth")
public class AuthController {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    @Autowired
    private AuthRequestFactory factory;

    @GetMapping
    public List<String> list() {
        return factory.oauthList();
    }

    @Value("${justauth.type.GITEE.client-id}")
    private String clientId;

    @Value("${justauth.type.GITEE.client-secret}")
    private String clientSecret;

    //用户通过gitee登录
    @GetMapping("/login/gitee")
    public void giteeLogin(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = factory.get("gitee");
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    //gitee回调后，返回token和用户信息，并执行登录操作
    @GetMapping("/gitee/callback")
    public BaseResponse<UserLoginVO> login(AuthCallback callback) {
        AuthRequest authRequest = factory.get("gitee");
        AuthResponse response = authRequest.login(callback);
        if (response.getCode() != AuthResponseStatus.SUCCESS.getCode()) {
            log.error(response.getCode() + ":" + response.getMsg());
            throw new BusinessException(response.getCode(), response.getMsg());
        }
        return ResultUtils.success(userService.giteeLoginCallback(response));
    }

    //已登录用户绑定gitee
    //通过输入gitee邮箱和密码
    //将token和refreshToken返回
    @GetMapping("/bind/gitee")
    public BaseResponse<UserVO> bindGitee(@RequestBody BindGiteeRequest bindGiteeRequest) {

        Long userId = UserHolder.getUser();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (bindGiteeRequest == null || bindGiteeRequest.getEmail() == null || bindGiteeRequest.getPassword() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        GiteeTokenRequest giteeTokenRequest = new GiteeTokenRequest();
        giteeTokenRequest.setClient_id(clientId);
        giteeTokenRequest.setClient_secret(clientSecret);
        giteeTokenRequest.setUsername(bindGiteeRequest.getEmail());
        giteeTokenRequest.setPassword(bindGiteeRequest.getPassword());
        giteeTokenRequest.setScope("user_info");
        giteeTokenRequest.setGrant_type("password");

        Gson gson = new Gson();
        String giteeTokenRequestJson = gson.toJson(giteeTokenRequest);

        String giteeTokenResponse = HttpRequest.post("https://gitee.com/oauth/token")
                .body(giteeTokenRequestJson)
                .execute().body();

        Map<String, String>
                giteeTokenResponseMap = gson.fromJson(giteeTokenResponse, new TypeToken<Map<String, String>>() {
        }.getType());
        String access_token = giteeTokenResponseMap.get("access_token");
        if (access_token == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱或密码错误，或服务器错误");
        }

        String userInfoResponse = HttpRequest.get("https://gitee.com/api/v5/user?access_token=" + access_token)
                .timeout(10000)
                .execute().body();
        log.info(userInfoResponse);

        GiteeUserInfoResponse giteeUserInfo = gson.fromJson(userInfoResponse, GiteeUserInfoResponse.class);

        String tokenKey = LOGIN_USER_KEY + userId;
        Map<Object, Object> userMap;
        User user;
        try {
            userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
            user = BeanUtil.fillBeanWithMap(userMap, new User(), false);
        } catch (Exception e) {
            //throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
            user = userService.getById(userId);
        }

        user.setUnionId(String.valueOf(giteeUserInfo.getId()));

        //将gitee的个人信息迁移到apihub中
        if (bindGiteeRequest.getMode() == 1) {
            if (StrUtil.isBlank(user.getUserAvatar())) user.setUserAvatar(giteeUserInfo.getAvatar_url());
            if (StrUtil.isBlank(user.getUserName())) user.setUserName(giteeUserInfo.getName());
        }

        boolean b = userService.updateById(user);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "MySQL绑定Gitee失败");
        }


        //redis中的用户信息更新
        Map<String, Object> newUserMap = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue != null ? fieldValue.toString() : ""));

        String key = LOGIN_USER_KEY + user.getId();
        stringRedisTemplate.opsForHash().putAll(key, newUserMap);

        //封装VO
        UserVO userVo;
        userVo = BeanUtil.copyProperties(user, UserVO.class);

        return ResultUtils.success(userVo);
    }

    /*@GetMapping("/login/{type}")
    public void login(@PathVariable String type, HttpServletResponse response) throws IOException {
        AuthRequest authRequest = factory.get(type);
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }*/

}
