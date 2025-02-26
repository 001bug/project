package com.apihub.voucher.openFeign.interceptor;

import com.apihub.common.utils.UserHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class UserInfoInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        Long userId = UserHolder.getUser();
        if (userId != null) {
            template.header("userId-info", userId.toString());
        }
    }
}
