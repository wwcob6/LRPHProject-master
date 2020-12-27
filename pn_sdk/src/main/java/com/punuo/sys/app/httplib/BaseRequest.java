package com.punuo.sys.app.httplib;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public class BaseRequest<T> extends NetRequest implements IRequest<T> {
    private HashMap<String, Object> mUrlParams = new HashMap<>();
    private HashMap<String, Object> mEntityParams = new HashMap<>();
    private String mRequestPath = "";
    private RequestType mRequestType = RequestType.GET;
    private String mUrl;
    public RequestListener<T> mRequestListener;

    public BaseRequest() {

    }

    public final void setRequestType(RequestType type) {
        mRequestType = type;
    }

    /**
     * 请求路径
     *
     * @param requestPath the path of request
     * @return this
     */
    public BaseRequest setRequestPath(String requestPath) {
        mRequestPath = requestPath;
        return this;
    }

    /**
     * 添加url参数/GET
     *
     * @param key   key
     * @param value value
     * @return this
     */
    public void addUrlParam(String key, Object value) {
        mUrlParams.put(key, value);
    }

    /**
     * 添加url参数/POST
     *
     * @param key   key
     * @param value value
     * @return this
     */
    public void addEntityParam(String key, Object value) {
        mEntityParams.put(key, value);
    }

    public String getUrl() {
        if (TextUtils.isEmpty(mUrl)) {
            mUrl = generateRestUrl();
        }
        return mUrl;
    }

    private String generateRestUrl() {
        RequestParams requestParams = new RequestParams();
        if (mUrlParams != null && !mUrlParams.isEmpty()) {
            Iterator iterator = mUrlParams.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                requestParams.put(String.valueOf(key), String.valueOf(value));
            }
        }
        return urlWithQueryString(getPathUrl(), requestParams);
    }

    private String getPathUrl() {
        StringBuilder url = new StringBuilder();
        url.append(HttpConfig.isUseHttps() ? "https://" : "http://");
        url.append(HttpConfig.getHost());
        url.append(HttpConfig.getPort() <= 0 ? "" : ":" + HttpConfig.getPort());
        url.append(HttpConfig.getPrefixPath());
        url.append(mRequestPath);
        return url.toString();
    }

    private String getPostURL() {
        return "";
    }

    @Override
    public Request build() {
        addHeader("user-agent", HttpConfig.getUserAgent());
        addHeader("connection", "Keep-Alive");
        url(getUrl());
        switch (mRequestType) {
            case POST:
                type(RequestType.POST);
                buildPostBody();
                break;
            case PUT:
                type(RequestType.PUT);
                buildPostBody();
                break;
            case DELETE:
                type(RequestType.DELETE);
                buildPostBody();
                break;
            case UPLOAD:
                type(RequestType.UPLOAD);
                buildPostBody();
                break;
        }
        return super.build();
    }

    protected void buildPostBody() {
        body(mEntityParams);
    }

    private String urlWithQueryString(String url, RequestParams params) {
        if (params != null) {
            String paramString = params.getParamString(false);
            if (!url.contains("?")) {
                url += "?" + paramString;
            } else {
                url += "&" + paramString;
            }
        }
        return url;
    }

    @Override
    public IRequest setRequestListener(RequestListener<T> listener) {
        mRequestListener = listener;
        return this;
    }

    @Override
    public RequestListener<T> getRequestListener() {
        return mRequestListener;
    }

    @Override
    public void finish() {
        mRequestListener = null;
        super.finish();
    }

    @Override
    public void deliverResponse(Response response, String parse) {
        if (!TextUtils.isEmpty(parse)) {
            Log.d("response", parse);
            JsonElement data = null;
            try {
                data = new JsonParser().parse(parse);
                deliverResponse(jsonParse(data, parse));
            } catch (Exception e) {
                e.printStackTrace();
                deliverError(e);
            }
        }
    }

    @Override
    public void deliverError(Exception error) {
        try {
            if (mRequestListener != null) {
                mRequestListener.onError(error);
                mRequestListener.onComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deliverResponse(T response) {
        try {
            if (mRequestListener != null) {
                mRequestListener.onSuccess(response);
                mRequestListener.onComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected T jsonParse(JsonElement result, String response) {
        if (result == null) {
            return jsonParse(response);
        }
        Type type = getType();
        if (type == String.class) {
            return (T) response;
        }
        return JsonUtil.fromJsonNoCatch(result, getType());
    }

    protected T jsonParse(String result) {
        Type type = getType();
        if (type == String.class) {
            return (T) result;
        }
        return JsonUtil.fromJson(result, getType());
    }

    public Type getType() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments()[0];
    }

    public T execute() {
        Response response = HttpManager.execute(this);
        if (response != null && response.isSuccessful()) {
            try {
                return jsonParse(response.body().string());
            } catch (IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
