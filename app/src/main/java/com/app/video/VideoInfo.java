package com.app.video;

import android.hardware.Camera;
import android.media.AudioTrack;
import android.os.Handler;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class VideoInfo {
    //用户部分
    //分辨率 with*height
    public static int width;
    public static int height;
    public static int videoType;

    public static String rtpIp;
    public static int rtpPort;

    public static byte[] magic;

    public static NalBuffer[] nalBuffers = new NalBuffer[200];

    public static RtpVideo rtpVideo;
    public static SendActivePacket sendActivePacket;

    public static int isrec=1;

    public static AudioTrack track;

    public static Handler handler;

    public static String resultion;

    public static String vidieoBegin;

    public static String videoEnd;

    //设备部分
    /**
     * 每一个新的NAL设置首包打包状态为false，即没有打包首包
     */
    public static boolean firstPktReceived = false;
    /**
     * 记录打包分片的索引
     */
    public static int pktflag = 0;
    /**
     * 若未打包到末包，则此状态一直为true
     */
    public static boolean status = true;
    /**
     * 打包分片长度
     */
    public static int divide_length = 1000;
    /**
     * 分片标志位
     */
    public static boolean dividingFrame = false;

    public static boolean endView = false;
    public static boolean query_response = false;

    // 发送的几个常数
    public static byte[] NalBuf = new byte[50000];
    public static int nalfirst = 0; // 0表示未收到首包，1表示收到
    public static int index = 0;
    public static int isDestroy = 0;

    public static int pktNumber = 0; // 记录发送的RTP包的个数
    public static String media_info_ip ;
    public static int media_info_port;
    public static byte[] media_info_magic = new byte[16];


    public static boolean isMonitor=false;

    public static Camera mCamera;

    public static int mAudioSessionId = -1;


}
