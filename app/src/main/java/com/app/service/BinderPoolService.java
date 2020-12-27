package com.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.app.aidl.BinderPool;


public class BinderPoolService extends Service {
    private static final String TAG = "DEBUG-WCL: " + BinderPoolService.class.getSimpleName();
    private Binder mBinderPool = new BinderPool.BinderPoolImpl(); // 动态选择Binder
    public BinderPoolService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.e(TAG, "onBind");
        return mBinderPool;
    }

}

