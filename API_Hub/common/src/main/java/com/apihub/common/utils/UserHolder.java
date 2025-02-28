package com.apihub.common.utils;
//用户信息存储工具类,同一线程内共享资源,登录用户的上下文管理
//避免在方法间传递参数,便于在拦截器,aop或服务层获取当前id
public class UserHolder {
    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    /**
     * 获取当前登录用户信息
     * @return 用户id
     */
    public static Long getUser() {
        return tl.get();
    }

    /**
     * 保存当前登录用户信息到ThreadLocal
     * @param userId 用户id
     */
    public static void setUser(Long userId) {
        tl.set(userId);
    }

    /**
     * 移除当前登录用户信息
     */
    public static void removeUser(){
        tl.remove();
    }
}
