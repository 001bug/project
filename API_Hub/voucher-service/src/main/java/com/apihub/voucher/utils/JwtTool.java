package com.apihub.voucher.utils;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.apihub.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.Date;

import static com.apihub.common.common.ErrorCode.NOT_LOGIN_ERROR;

@Component
public class JwtTool {
    private final JWTSigner jwtSigner;

    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }

    /**
     * 创建 access-token
     */
    public String createToken(Long userId, Date endTime) {
        // 1.生成jws
        return JWT.create()
                .setPayload("userId", userId)
                .setExpiresAt(endTime)
                .setSigner(jwtSigner)
                .sign();
    }

    /**
     * 解析token
     *
     * @param token token
     * @return 解析刷新token得到的用户信息
     */
    public Long parseToken(String token) {
        // 1.校验token是否为空
        if (token == null) {
            throw new BusinessException(NOT_LOGIN_ERROR, "token为空");
        }
        // 2.校验并解析jwt
        JWT jwt;
        try {
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            throw new BusinessException(NOT_LOGIN_ERROR, "无效的token");
        }
        // 2.校验jwt是否有效
        if (!jwt.verify()) {
            // 验证失败
            throw new BusinessException(NOT_LOGIN_ERROR, "无效的token");
        }
        // 3.校验是否过期
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            throw new BusinessException(NOT_LOGIN_ERROR, "token已经过期");
        }
        // 4.数据格式校验
        Object userPayload = jwt.getPayload("userId");
        if (userPayload == null) {
            // 数据为空
            throw new BusinessException(NOT_LOGIN_ERROR, "无效的token");
        }

        // 5.数据解析
        try {
            return Long.valueOf(userPayload.toString());
        } catch (RuntimeException e) {
            // 数据格式有误
            throw new BusinessException(NOT_LOGIN_ERROR, "无效的token");
        }
    }
}