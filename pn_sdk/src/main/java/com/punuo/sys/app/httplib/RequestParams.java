package com.punuo.sys.app.httplib;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public class RequestParams {

    public static final String UTF_8 = "UTF-8";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String NAME_VALUE_SEPARATOR = "=";
    private Map<String, String> urlParams = new ConcurrentHashMap<String, String>();

    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    public String get(String key) {
        return urlParams.get(key);
    }

    public void remove(String key) {
        urlParams.remove(key);
    }

    private String getParamStringWithoutEqual() {
        StringBuilder sb = new StringBuilder();
        Object[] keys = urlParams.keySet().toArray();
        Arrays.sort(keys);
        for (Object key : keys) {
            sb.append(key);
            sb.append(urlParams.get(key));
        }
        return sb.toString();
    }

    private String getParamString() throws UnsupportedEncodingException {
        final StringBuilder result = new StringBuilder();
        for (String key : urlParams.keySet()) {
            final String encodedName = URLEncoder.encode(key, UTF_8);
            final String value = urlParams.get(key);
            final String encodedValue = URLEncoder.encode(value, UTF_8);
            if (result.length() > 0)
                result.append(PARAMETER_SEPARATOR);
            result.append(encodedName);
            result.append(NAME_VALUE_SEPARATOR);
            result.append(encodedValue);
        }
        return result.toString();
    }

    public String getParamString(boolean withoutEqual) {
        if (withoutEqual) {
            return getParamStringWithoutEqual();
        }
        try {
            return getParamString();
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public String getAbrParamString(){

        final StringBuilder result = new StringBuilder();
        String[] keys = urlParams.keySet().toArray(new String[]{});
        Arrays.sort(keys);
        for (String key : keys) {
            final String value = urlParams.get(key);
            if (result.length() > 0){
                result.append(PARAMETER_SEPARATOR);
            } else {
                result.append("?");
            }
            result.append(key);
            result.append(NAME_VALUE_SEPARATOR);
            result.append(value);
        }
        return result.toString();

    }
}
