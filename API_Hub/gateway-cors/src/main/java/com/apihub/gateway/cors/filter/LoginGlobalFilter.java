package com.apihub.gateway.cors.filter;

import com.apihub.common.utils.CollUtils;
import com.apihub.gateway.cors.config.AuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

import static com.apihub.common.utils.RedisConstants.USER_TOKEN_KEY;


@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
@Slf4j
public class LoginGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取Request
        ServerHttpRequest request = exchange.getRequest();
        // 2.判断是否不需要拦截
        if (isExclude(request.getPath().toString())) {
            // 无需拦截，直接放行
            return chain.filter(exchange);
        }
        // 3.获取请求头中的token
        String token;
        List<String> headers = request.getHeaders().get("Authorization");
        if (!CollUtils.isEmpty(headers)) {
            token = headers.get(0);
        } else {//没有token直接驳回
            token = null;
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }
        // 4.以token为key,从redis中获取用户id(USER_TOKEN_KEY+token)可直接获得id
        String userIdInfo;
        try {
            userIdInfo = stringRedisTemplate.opsForValue().get(USER_TOKEN_KEY + token);
            if (userIdInfo == null) {
                throw new RuntimeException("token无效");
            }
        } catch (Exception e) {
            // 如果无效，拦截
            log.warn(e.getMessage());
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }

        // token有效，传递用户信息

        ServerWebExchange ex = exchange.mutate()
                .request(b -> b.header("userId-info", userIdInfo))
                .build();//在表头添加userId-info字段

        // 放行
        return chain.filter(exchange);
    }

    private boolean isExclude(String antPath) {//哪写请求不拦截
        for (String pathPattern : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pathPattern, antPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
