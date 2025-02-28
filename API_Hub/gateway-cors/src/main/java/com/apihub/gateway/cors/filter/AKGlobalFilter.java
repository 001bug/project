package com.apihub.gateway.cors.filter;

import cn.hutool.core.util.StrUtil;
import com.apihub.common.utils.CollUtils;
import com.apihub.gateway.cors.config.AuthProperties;
import com.apihub.gateway.cors.utils.ApiSignUtils;
import lombok.RequiredArgsConstructor;
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
import java.util.Map;
import java.util.Objects;

import static com.apihub.common.utils.RedisConstants.API_ACCESS_KEY;


@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class AKGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取Request
        ServerHttpRequest request = exchange.getRequest();
        // 2.判断是否不需要拦截
        if (!isInclude(request.getPath().toString())) {
            // 无需拦截，直接放行
            return chain.filter(exchange);
        }
        List<String> headers;
        String accessKey;
        String secretSign;
        headers = request.getHeaders().get("accessKey");
        if (!CollUtils.isEmpty(headers)) {
            accessKey = headers.get(0);
        } else {
            accessKey = null;
        }
        headers = request.getHeaders().get("secretSign");
        if (!CollUtils.isEmpty(headers)) {
            secretSign = headers.get(0);
        } else {
            secretSign = null;
        }
        //防止同一请求反复发送
        //时间戳
        String timestamp = request.getHeaders().getFirst("timestamp");

//        // 随机数
//        String nonce = request.getHeaders().getFirst("nonce");
        if (!StrUtil.isAllNotEmpty(accessKey, secretSign, timestamp)) {
            // 如果数据缺失，拦截
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(403);
            return response.setComplete();
        }

        // 时间和当前时间不能超过 5 分钟
        long currentTime = System.currentTimeMillis() / 1000;
        final long FIVE_MINUTES = 60 * 5L;
        if (timestamp != null && (currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(403);
            return response.setComplete();
        }

        String userId;
        try {
            Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(API_ACCESS_KEY + accessKey);
            //secretKey校验
            String UserSecretKey = (String) userMap.get("secretKey");
            String checkSign = ApiSignUtils.genSign(timestamp, UserSecretKey);//哈希生成签名
            if (!Objects.equals(checkSign, secretSign)) throw new RuntimeException();

            //传递userId
            userId = (String) userMap.get("id");
            String finalUserId = userId;
            ServerWebExchange ex = exchange.mutate()
                    .request(b -> b.header("userId-info", String.valueOf(finalUserId)))
                    .build();
        } catch (Exception e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(403);
            return response.setComplete();
        }


        /*ServerWebExchange ex = exchange.mutate()
                .request(b -> b.header("accessKey", accessKey))
                .request(b -> b.header("sign", sign))
                .build();*/

        // 放行
        return chain.filter(exchange);
    }

    private boolean isInclude(String antPath) {
        for (String pathPattern : authProperties.getIncludePaths()) {
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
