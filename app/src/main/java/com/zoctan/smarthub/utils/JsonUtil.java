package com.zoctan.smarthub.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.okhttplib.HttpInfo;

import java.lang.reflect.Type;

/**
 * 使用Gson取数据
 * Json转换工具类
 */
public class JsonUtil {

    private final static Gson mGson = new Gson();
    private final static JsonParser mJsonParser = new JsonParser();

    /**
     * 将对象准换为Json字符串
     */
    public static <T> String serialize(final T object) {
        return mGson.toJson(object);
    }

    /**
     * 将Json字符串转换为实体对象
     */
    public static <T> T deserialize(final String json, final Class<T> clz) throws JsonSyntaxException {
        return mGson.fromJson(json, clz);
    }

    /**
     * 将Json对象转换为实体对象
     */
    public static <T> T deserialize(final JsonObject json, final Class<T> clz) throws JsonSyntaxException {
        return mGson.fromJson(json, clz);
    }

    /**
     * 将Json字符串转换为实体对象
     */
    public static <T> T deserialize(final String json, final Type type) throws JsonSyntaxException {
        return mGson.fromJson(json, type);
    }

    /**
     * 将Json字符串转换为实体对象
     */
    public static <T> T getObjectFromHttpInfo(final HttpInfo info, final Class<T> clz) {
        final String response = info.getRetDetail();
        // 将res转换成Json对象
        final JsonObject jsonObj = mJsonParser.parse(response).getAsJsonObject();
        return deserialize(jsonObj, clz);
    }
}
