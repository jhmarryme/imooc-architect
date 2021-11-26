package com.jhmarryme.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * description: 基于Gson的json工具类
 * @author JiaHao Wang
 * @date 2021/2/27 21:59
 */
@Slf4j
public class JsonUtil {

    private static final Gson GSON;

    static {
        GSON = new GsonBuilder().create();
    }

    public static String objectToJson(Object data){
        String json = null;
        try {
            json = GSON.toJson(data);
        } catch (Exception e) {
            log.error("序列化对象失败");
        }
        return json;
    }

    public static <T> String pojoToJson(T t, Class<T> cls) {
        String json = null;
        try {
            json = GSON.toJson(t, cls);
        } catch (Exception e) {
            log.error("序列化对象失败");
        }
        return json;
    }

    public static <T> T jsonToPojo(String json, Class<T> cls) {
        T t = null;
        try {
            t = GSON.fromJson(json, cls);
        } catch (Exception e) {
            log.error("反序列化对象失败");
        }
        return t;
    }

    public static <T> List<T> jsonToList(String json) {
        List<T> list = null;
        try {
            // Gson的TypeToken来确定要反序列化的正确类型
            list = GSON.fromJson(json, new TypeToken<List<T>>() {}.getType());
        } catch (Exception e) {
            log.error("反序列化对象失败");
        }
        return list;
    }

}
