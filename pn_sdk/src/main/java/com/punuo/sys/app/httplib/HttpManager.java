package com.punuo.sys.app.httplib;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public class HttpManager {
    //timeout 30s
    public static final long DEFAULT_TIME_OUT = 30L;
    private static volatile OkHttpClient sOkHttpClient;
    private static OkHttpClient.Builder sBuilder = new OkHttpClient.Builder();
    private static Context sContext;

    public static OkHttpClient getsOkHttpClient() {
        init();
        return sOkHttpClient;
    }

    public static void init() {
        if (sContext == null) {
            throw new RuntimeException("context is null, please set context");
        }
        if (sOkHttpClient == null) {
            synchronized (HttpManager.class) {
                if (sOkHttpClient == null) {
                    sBuilder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                            .readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                            .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                            .followRedirects(true)
                            .retryOnConnectionFailure(true)
                            .cache(new Cache(new File(sContext.getExternalCacheDir(), "okhttp"),
                                    500 * 1024 * 1024));
                    sOkHttpClient = sBuilder.build();
                }
            }
        }
    }

    /**
     * 设置context
     *
     * @param context
     */
    public static void setContext(Context context) {
        sContext = context.getApplicationContext();
    }

    private static ExecutorDelivery sDelivery = new ExecutorDelivery(new Handler(Looper
            .getMainLooper()));

    public static void addRequest(final NetRequest netRequest) {
        final Request request = netRequest.build();
        HttpManager.getsOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sDelivery.postError(netRequest, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    handlerResponse(netRequest, response, true);
                } catch (Exception e) {
                    sDelivery.postError(netRequest, e);
                }
            }
        });
    }

    /**
     * 执行网络请求
     * 同步执行
     * 注意：使用完一定要注意最后是否已经close
     *
     * @param request NetRequest obj
     * @return Response obj
     */
    public static Response execute(NetRequest request) {
        Request requestTemp = request.build();

        try {
            Response okHttpResponse = HttpManager.getsOkHttpClient().newCall(requestTemp).execute();
            return handlerResponse(request, okHttpResponse, false);
        } catch (Exception e) {
            sDelivery.postCacheResponse(request, null, e);
            return null;
        }
    }

    private static Response handlerResponse(NetRequest request,
                                            Response response, boolean async) throws IOException {
        if (response.isSuccessful()) {
            if (async) {
                sDelivery.postResponse(request, response);
            }
            return response;
        } else {
            ErrorTipException error;
            if (response.isRedirect()) {
                error = new ErrorTipException("当前请求被劫持");
            } else {
                switch (response.code()) {
                    case 400:
                    case 403:
                    case 404:
                    case 405:
                    case 406:
                        error = new ErrorTipException("请求资源失效");
                        break;
                    case 408:
                    case 504:
                        error = new ErrorTipException("网络超时");
                        break;
                    case 500:
                    case 501:
                    case 502:
                    case 503:
                        error = new ErrorTipException("服务器异常");
                        break;
                    default:
                        error = new ErrorTipException("网络服务繁忙");
                        break;
                }
            }
            if (response.body() != null) {
                response.close();
            }
            error.mResposeCode = response.code();
            if (async) {
                sDelivery.postError(request, error);
            }
            return response;
        }
    }
}
