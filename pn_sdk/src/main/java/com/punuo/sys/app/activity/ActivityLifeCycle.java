package com.punuo.sys.app.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Created by han.chen.
 * Date on 2019/4/2.
 **/
public class ActivityLifeCycle implements Application.ActivityLifecycleCallbacks {

    private static ActivityLifeCycle sInstance;
    //当前Activity的弱引用
    private WeakReference<Activity> mActivityReference;

    public static synchronized ActivityLifeCycle getInstance() {
        if (sInstance == null) {
            sInstance = new ActivityLifeCycle();
        }
        return sInstance;
    }

    public Activity getCurrentActivity() {
        if (mActivityReference != null) {
            return mActivityReference.get();
        }
        return null;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        mActivityReference = new WeakReference<>(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mActivityReference = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
