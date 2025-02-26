package com.apihub.api.openFeign.config;

import com.apihub.api.openFeign.fallback.InterfaceInfoClientFallback;
import com.apihub.api.openFeign.interceptor.UserInfoInterceptor;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userInfoInterceptor(){
        return new UserInfoInterceptor();
    }

    @Bean
    public InterfaceInfoClientFallback interfaceInfoClientFallback(){
        return new InterfaceInfoClientFallback();
    }
}
