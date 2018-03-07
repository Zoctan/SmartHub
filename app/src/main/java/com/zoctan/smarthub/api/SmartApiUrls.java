package com.zoctan.smarthub.api;

import java.util.Locale;

public class SmartApiUrls {
    private static final String API = "http://smart.txdna.cn/api/";
    public static final String HUBS = API + "hubs";

    public static final String QiNiu = API + "qiniu/";
    public static final String QiNiuBucket = "http://smarthub.txdna.cn/";

    public static final String SPARES = HUBS + "/spares";
    public static final String TIMERS = HUBS + "/timers";

    public static final String DEVICE = HUBS + "/devices";
    public static final String DEVICE_IMG = DEVICE + "/img/";

    public static final String USERS = API + "users";
    public static final String USERS_AVATAR = USERS + "/avatar";
    public static final String USERS_PASSWORD = USERS + "/password";
    public static final String USERS_TOKENS = API + "tokens";

    public final static String HEADER_KEY = "Authorization";
    public final static String HEADER_VALUE = "Smart ";

    public static String setOrderUrl(final String oneNetId, final String order, final Integer status) {
        return String.format(Locale.CHINA, "%s/%s/order?order=%s&status=%d", HUBS, oneNetId, order, status);
    }
}
