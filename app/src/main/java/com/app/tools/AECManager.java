package com.app.tools;

import android.media.audiofx.AcousticEchoCanceler;
import android.util.Log;

/**
 * 作者：EchoJ on 2018/6/13 23:48 <br>
 * 邮箱：echojiangyq@gmail.com <br>
 * 描述：音频回音消除
 */
public class AECManager {
    private AcousticEchoCanceler canceler;

    private static final String TAG = "AECManager";

    private static AECManager single;

    private AECManager() {}

    public static AECManager getInstance() {
        if (single == null) {
            synchronized (AECManager.class) {
                if (single == null) {
                    single = new AECManager();
                }
            }
        } return single;
    }

    public static boolean isDeviceSupport() {
        Log.d(TAG, "isDeviceSupport: " + AcousticEchoCanceler.isAvailable());
        return AcousticEchoCanceler.isAvailable();
    }

    public boolean initAEC(int audioSession) {
        boolean result = false;
        canceler = AcousticEchoCanceler.create(audioSession);
        if (canceler != null) {
            canceler.setEnabled(true);
            result = canceler.getEnabled();
        }
        return result;
    }

    /**
     * 使能AEC
     *
     * @param enable
     * @return
     */
    public boolean setAECEnabled(boolean enable) {
        if (null == canceler) {
            return false;
        } 
        canceler.setEnabled(enable); 
        return canceler.getEnabled();
    }


    /**
     * 释放AEC
     *
     * @return
     */
    public boolean release() {
        if (null == canceler) {
            return false;
        } 
        canceler.setEnabled(false); 
        canceler.release();
        canceler = null;
        return true;
    }
}
