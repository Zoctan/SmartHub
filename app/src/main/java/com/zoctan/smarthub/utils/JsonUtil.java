package com.zoctan.smarthub.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * 使用Gson取数据
 * Json转换工具类
 */
public class JsonUtil {

    private final static Gson mGson = new Gson();

    /**
     * 将对象准换为Json字符串
     */
    public static <T> String serialize(T object) {
        return mGson.toJson(object);
    }

    /**
     * 将Json字符串转换为实体对象
     */
    public static <T> T deserialize(String json, Class<T> clz) throws JsonSyntaxException {
        return mGson.fromJson(json, clz);
    }

    /**
     * 将Json对象转换为实体对象
     */
    public static <T> T deserialize(JsonObject json, Class<T> clz) throws JsonSyntaxException {
        return mGson.fromJson(json, clz);
    }

    /**
     * 将Json字符串转换为实体对象
     */
    public static <T> T deserialize(String json, Type type) throws JsonSyntaxException {
        return mGson.fromJson(json, type);
    }

}
