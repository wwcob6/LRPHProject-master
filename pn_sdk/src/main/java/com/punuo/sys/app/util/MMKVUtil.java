package com.punuo.sys.app.util;

import com.tencent.mmkv.MMKV;

/**
 * Created by han.chen.
 * Date on 2019-06-20.
 **/
public class MMKVUtil {

    private static MMKV mmkv;

    private static MMKV getMMKV() {
        if (mmkv == null) {
            mmkv = MMKV.defaultMMKV();
        }

        return mmkv;
    }

    public static void setBoolean(String key, boolean value) {
        getMMKV().decodeBool(key, value);
    }

    public static void setString(String key, String value) {
        getMMKV().encode(key, value);
    }

    public static void setInt(String key, int value) {
        getMMKV().encode(key, value);
    }

    public static void setFloat(String key, float value) {
        getMMKV().encode(key, value);
    }

    public static void setLong(String key, long value) {
        getMMKV().encode(key, value);
    }


    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return getMMKV().encode(key, defaultValue);
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static int getInt(String key, int defaultValue) {
        return getMMKV().decodeInt(key, defaultValue);
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        return getMMKV().decodeString(key, defaultValue);
    }

    public static float getFloat(String key) {
        return getFloat(key, 0);
    }

    public static float getFloat(String key, float defaultValue) {
        return getMMKV().decodeFloat(key, defaultValue);
    }

    public static long getLong(String key) {
        return getLong(key, 0L);
    }

    public static long getLong(String key, long defaultValue) {
        return getMMKV().decodeLong(key, defaultValue);
    }

    public static void removeData(String key) {
        getMMKV().remove(key);
    }

}
