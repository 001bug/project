package com.apihub.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.apihub.common.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求头中的用户
        String userId = request.getHeader("userId-info");
        //判断是否为空，不为空则存入UserHolder
        if (StrUtil.isNotBlank(userId)) {
            UserHolder.setUser(Long.valueOf(userId));

        }
        //放行
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //在请求回发后清楚ThreadLocal中的用户信息, 防止内存泄漏
        UserHolder.removeUser();
    }
}
