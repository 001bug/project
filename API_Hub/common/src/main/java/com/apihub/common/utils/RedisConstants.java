package com.apihub.common.utils;

public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_USER_KEY = "login:token:hekmatyar:";
    public static final Long LOGIN_USER_TTL = 36000L;

    // 以用户账号为粒度的分布式锁
    public static final String REGISTER_LOCK_KEY = "register:lock:";

    public static final String API_ACCESS_KEY = "ak:hekmatyar:";

    public static final String USER_BALANCE_KEY = "ak:balance:";

    public static final String API_INVOKE_KEY = "ak:apiInvoke:";

    public static final String USER_TOKEN_KEY = "user:token:";

    public static final String USER_EMAIL_CODE_KEY = "user:code:email:";
    public static final String USER_EMAIL_BIND_CODE_KEY = "user:code:bind:email:";

    //使用email验证码重置密码
    public static final String USER_RESET_PASSWORD_EMAILCODE_KEY = "user:code:reset:";

    //接口详细信息
    public static final String INTERFACEINFO_DETAIL_KEY = "interfaceInfo:detail:";
    //接口请求方法
    public static final String INTERFACEINFO_METHOD_KEY = "interfaceInfo:method:";

    public static final Long USER_BALANCE_TTL = 26L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_SHOP_TTL = 30L;
    public static final String CACHE_SHOP_KEY = "cache:shop:";

    public static final String LOCK_SHOP_KEY = "lock:shop:";
    public static final Long LOCK_SHOP_TTL = 10L;

    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String BLOG_LIKED_KEY = "blog:liked:";
    public static final String FEED_KEY = "feed:";
    public static final String SHOP_GEO_KEY = "shop:geo:";
    public static final String USER_SIGN_KEY = "sign:";
    public static final String CACHE_SHOP_TYPE_KEY = "cache:shopType:";


}
