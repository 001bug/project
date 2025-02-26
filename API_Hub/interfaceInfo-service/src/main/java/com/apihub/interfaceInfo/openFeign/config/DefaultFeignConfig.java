package com.apihub.interfaceInfo.openFeign.config;

import com.apihub.interfaceInfo.openFeign.interceptor.UserInfoInterceptor;
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
}
