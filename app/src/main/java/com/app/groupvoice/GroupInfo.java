package com.app.groupvoice;

import android.os.PowerManager;

/**
 * Created by chenblue23 on 2016/7/5.
 */
public class GroupInfo {
    public static String groupNum;
    public static String ip;
    public static int port;
    public static String level;
    public static boolean isSpeak;
    public static RtpAudio rtpAudio;
    public static GroupUdpThread groupUdpThread;
    public static GroupKeepAlive groupKeepAlive;
    public static PowerManager.WakeLock wakeLock;
}
