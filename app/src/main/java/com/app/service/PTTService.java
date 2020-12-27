package com.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.app.receiver.MyReceiver;


/**
 * Created by acer on 2016/7/1.
 */
public class PTTService extends Service {
    private static final String TAG = "PTTService";
    private String CAMERA = "android.intent.action.CAMERA_BUTTON";
    private String EXTRA_KEY_EVENT = "android.intent.extra.KEY_EVENT";
    private String GLOBAL_BUTTON = "android.intent.action.GLOBAL_BUTTON";
    private MyReceiver myReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerPTTReceiver(this);
        return START_STICKY;
    }

    private void registerPTTReceiver(Context context) {
        Log.d(TAG, "registerPTTReceiver");
        myReceiver = new MyReceiver();
        IntentFilter P91 = new IntentFilter(CAMERA);
        P91.addAction(EXTRA_KEY_EVENT);
        P91.addAction(GLOBAL_BUTTON);
        context.registerReceiver(myReceiver, P91);
    }

    private void unregisterPTTReceiver(Context context) {
        Log.d(TAG, "unregisterPTTReceiver");
        if (myReceiver != null) {
            context.unregisterReceiver(myReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterPTTReceiver(this);
    }
}
