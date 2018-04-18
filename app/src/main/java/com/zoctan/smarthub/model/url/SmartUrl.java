package com.zoctan.smarthub.model.url;

public class SmartUrl {
    public static final String API = "http://smart.txdna.cn/api/";

    public final static String HEADER_AUTH_KEY = "Authorization";

    public static final String FEEDBACK = "mail";

    public static final String TOKEN = "token";

    public static final String USERS = "users";
    public static final String USERS_AVATAR = USERS + "/avatar";
    public static final String USERS_PASSWORD = USERS + "/password";

    public static final String HUBS = "hubs";
    public static final String ORDERS = HUBS + "/order";
    public static final String SPARES = HUBS + "/spares";
    public static final String TIMERS = HUBS + "/timers";
    public static final String DEVICE = HUBS + "/devices";
    public static final String DEVICE_IMG = HUBS + "/devices/img";

    public static final String QiNiu = "qiniu";
}
