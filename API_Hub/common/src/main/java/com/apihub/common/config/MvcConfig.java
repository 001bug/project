package com.apihub.common.config;

import com.apihub.common.interceptor.UserInfoInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(DispatcherServlet.class)
public class MvcConfig implements WebMvcConfigurer {

    /*
    * @ConditionalOnClass(DispatcherServlet.class)
    * 因为Gateway没有使用springMvc，所以网关引入会报错
    * 所以，添加此注解，如果有DispatcherServlet.class，此MvcConfig才会生效，否则不生效
    * */

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInfoInterceptor());
    }
}
