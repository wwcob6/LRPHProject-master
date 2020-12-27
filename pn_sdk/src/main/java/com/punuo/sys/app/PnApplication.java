package com.punuo.sys.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tencent.mmkv.MMKV;

/**
 * Created by han.chen.
 * Date on 2019/4/4.
 **/
public class PnApplication extends Application {
    private static PnApplication instance;

    public static PnApplication getInstance() {
        if (instance == null) {
            instance = new PnApplication();
        }
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        MMKV.initialize(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initImageLoader();
    }

    private void initImageLoader() {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }
}
