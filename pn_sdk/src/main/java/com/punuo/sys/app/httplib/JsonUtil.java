package com.punuo.sys.app.httplib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import java.lang.reflect.Type;


public final class JsonUtil {

    private static Gson mGson;

    public static Gson getGson() {
        if (null == mGson) {
            mGson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
        }
        return mGson;
    }

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    public static String toJson(Object object, Type typeOfT) {
        return new Gson().toJson(object, typeOfT);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        T t = null;
        try {
            t = getGson().fromJson(json, clazz);
        } catch (IncompatibleClassChangeError error) {
            error.printStackTrace();
        } catch (JsonParseException exception) {
            exception.printStackTrace();
        } finally {
            return t;
        }
    }

    public static <T> T fromJsonNoCatch(String json, Class<T> clazz) {
        return getGson().fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return getGson().fromJson(json, typeOfT);
    }

    public static <T> T fromJson(JsonReader reader, Type typeOfT) {
        return getGson().fromJson(reader, typeOfT);
    }

    public static <T> T fromJson(JsonElement element, Type typeOfT) {
        T t = null;
        try {
            t = getGson().fromJson(element, typeOfT);
        } catch (Exception error) {
            error.printStackTrace();
        } finally {
            return t;
        }
    }

    public static <T> T fromJsonNoCatch(JsonElement element, Type typeOfT) {
        return getGson().fromJson(element, typeOfT);
    }
}