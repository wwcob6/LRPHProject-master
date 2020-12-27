package com.app.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.R;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.tools.NewsVideo;
import com.app.tools.SipCallManager;
import com.app.ui.VideoCallActivity;

import java.util.List;

/**
 * Created by ch on 2016/11/14.
 */

public class NewsService extends Service {
    private String TAG = "NewsService";
    private NotificationManager notificationManager;
    private int TASK_NOTIFICATION_ID = 0x1123;
    private int MAIL_NOTIFICATION_ID = 0x1124;

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "News Service 开启");
        SipInfo.notifymedia = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x1111) {
                   /* System.out.println("收到了 视频邀请");
                    VideoInfo.rtpIp = VideoInfo.media_info_ip;
                    VideoInfo.rtpPort = VideoInfo.media_info_port;
                    VideoInfo.magic = VideoInfo.media_info_magic;
                    
                    Intent intent = new Intent(NewsService.this, VideoCallActivity.class);
                    Log.d("echo_tag", "1 - NewsService - 收到视频请求 - SipInfo.isWaitingFeedback： " + SipInfo.isWaitingFeedback);
    
                    SipInfo.inviteResponse = true;
                    SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(SipInfo.msg, 200, "OK", BodyFactory.createMediaResponseBody("MOBILE_S9")));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
               */

                    Log.d("echo_tag", "1 - NewsService - 收到视频请求 - SipInfo.isWaitingFeedback： " + SipInfo.isWaitingFeedback);

                    SipInfo.inviteResponse = true;
                    SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(SipInfo.msg, 200, "OK", BodyFactory.createMediaResponseBody("MOBILE_S9")));

                    if (SipInfo.isWaitingFeedback) {  // 主动呼叫不用再呼叫一次，直接去视频界面
                        SipInfo.isWaitingFeedback = false; // 改变这个标志，sipCallManager中会跳转到videoCall界面
                    } else {
                        SipCallManager.getInstance().callVideoChat(NewsService.this, false);
                    }
                    super.handleMessage(msg);
                } else if (msg.what == 0x2222) {
                    LocalBroadcastManager.getInstance(NewsService.this).sendBroadcast(new Intent(VideoCallActivity.BROADCAST_ACTION));
                }
//                else if(msg.what==0x3333){
//                    System.out.println("收到了 视频邀请");
//                    Intent intent = new Intent(NewsService.this, H264Sending.class);
//                    SipInfo.inviteResponse = true;
//                    SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(SipInfo.msg, 200, "OK", BodyFactory.createMediaResponseBody("MOBILE_S9")));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    super.handleMessage(msg);
//                }
            }
        };
            
        SipInfo.newTask = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                System.out.println("收到了新的工单");
                NewsVideo newsvideo = new NewsVideo(NewsService.this, false);
                newsvideo.VideoAlerm();

                Notification notify = new Notification.Builder(getApplicationContext())
                        .setAutoCancel(true)
                        .setTicker("有新工单")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("收到一条新工单")
                        .setContentText("点击查看")
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setWhen(System.currentTimeMillis())
//                        .setContentIntent(pi)
                        .build();
                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //发送通知(id相同，只显示最新的notification)
                notificationManager.notify(TASK_NOTIFICATION_ID, notify);
                super.handleMessage(msg);
            }
        };
        
        SipInfo.newMail = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                System.out.println("收到了新的邮件");
                NewsVideo newsvideo = new NewsVideo(NewsService.this, false);
                newsvideo.VideoAlerm();

                Notification notify = new Notification.Builder(getApplicationContext())
                        .setAutoCancel(true)
                        .setTicker("有新邮件")
                        .setSmallIcon(R.drawable.icon_mail)
                        .setContentTitle("收到一条新邮件")
                        .setContentText("点击查看")
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setWhen(System.currentTimeMillis())
//                        .setContentIntent(pi)
                        .build();
                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //发送通知(id相同，只显示最新的notification)
                notificationManager.notify(MAIL_NOTIFICATION_ID, notify);
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * 判断服务是否启动,context上下文对象 ，className服务的name
     */
    public static boolean isServiceRunning(Context mContext) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(NewsService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
