package com.app.tools;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.app.service.NewsService;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.ui.VideoCallActivity;
import com.app.ui.VideoPlay;
import com.app.video.RtpVideo;
import com.app.video.SendActivePacket;
import com.app.video.VideoInfo;
import com.punuo.sys.app.activity.BaseActivity;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;

import java.net.SocketException;

/**
 * 作者：EchoJ on 2018/7/30 10:14 <br>
 * 邮箱：echojiangyq@gmail.com <br>
 * 描述：
 */
public class SipCallManager {
    private static final String TAG = "SipCallManager";
    static volatile SipCallManager instance;

    SipCallManager() {

    }

    public static final SipCallManager getInstance() {
        if (instance == null) {
            synchronized (SipCallManager.class) {
                if (instance == null) {
                    instance = new SipCallManager();
                }
            }
        }
        return instance;
    }


    public void callVideoChat(final Context mContext, final boolean isIntiative) {
        call(mContext, isIntiative, VideoCallActivity.class);
    }

    public void callVideoPlay(final Context mContext) {
        call(mContext, false, VideoPlay.class);
    }

    private void call(final Context mContext, final boolean isIntiative, final Class<?> activityClass) {
        if (!NewsService.isServiceRunning(mContext)) {
            mContext.startService(new Intent(mContext, NewsService.class));
        }

        Log.d("echo_tag", "2 - SipcallManager - 收到视频请求");
        if (mContext == null || activityClass == null) return;

        SipInfo.isWaitingFeedback = isIntiative;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Log.d("echo_tag", "3 - SipcallManager - 收到视频请求 - 发起视频请求");
                String devId = SipInfo.paddevId;
                devId = devId.substring(0, devId.length() - 4).concat("0160");//设备id后4位替换成0160
                String devName = "pad";
                final String devType = "2";
                SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                SipInfo.toDev = new NameAddress(devName, sipURL);
                SipInfo.queryResponse = false;
                SipInfo.inviteResponse = false;

                if (mContext instanceof BaseActivity) {
                    ((BaseActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((BaseActivity) mContext).dismissLoadingDialog();
                        }
                    });
                }

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Looper.prepare();
                            Log.d("echo_tag", "4 - SipcallManager - 发起视频请求");
                            Message query = SipMessageFactory.createOptionsRequest(SipInfo.sipUser, SipInfo.toDev,
                                    SipInfo.user_from, BodyFactory.createQueryBody(devType));
                            outer:
                            for (int i = 0; i < 3; i++) {
                                SipInfo.sipUser.sendMessage(query);
                                for (int j = 0; j < 20; j++) {
                                    sleep(100);
                                    if (SipInfo.queryResponse) {
                                        break outer;
                                    }
                                }
                                if (SipInfo.queryResponse) {
                                    break;
                                }
                            }

                            Log.d("echo_tag", "5 - SipcallManager - 发起视频请求");
                            if (SipInfo.queryResponse) {
                                Message invite = SipMessageFactory.createInviteRequest(SipInfo.sipUser,
                                        SipInfo.toDev, SipInfo.user_from, BodyFactory.createMediaBody(VideoInfo.resultion, "H.264", "G.711", devType));
                                outer2:
                                for (int i = 0; i < 3; i++) {
                                    SipInfo.sipUser.sendMessage(invite);
                                    for (int j = 0; j < 20; j++) {
                                        sleep(100);
                                        if (SipInfo.inviteResponse) {
                                            break outer2;
                                        }
                                    }
                                    if (SipInfo.inviteResponse) {
                                        break;
                                    }
                                }
                            }
                            Log.d("echo_tag", "6 - SipcallManager - SipInfo.inviteResponse:" + SipInfo.inviteResponse);
                            Log.d("echo_tag", "6 - SipcallManager - isIntiative:" + isIntiative);
                            if (SipInfo.inviteResponse && isIntiative) {
                                Log.i("echo_tag", "等待对方的视频请求");
                                for (int j = 0; j < 200; j++) {
                                    sleep(100);
                                    if (!SipInfo.isWaitingFeedback) {
                                        break;
                                    }
                                }
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            if (mContext instanceof BaseActivity) {
                                ((BaseActivity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((BaseActivity) mContext).dismissLoadingDialog();
                                    }
                                });
                            }
                            if (SipInfo.queryResponse && SipInfo.inviteResponse && (!isIntiative || (isIntiative && !SipInfo.isWaitingFeedback))) {
                                Log.i("echo_tag", "视频请求成功");
                                SipInfo.decoding = true;
                                try {
                                    VideoInfo.rtpVideo = new RtpVideo(VideoInfo.rtpIp, VideoInfo.rtpPort);
                                    VideoInfo.sendActivePacket = new SendActivePacket();
                                    VideoInfo.sendActivePacket.startThread();

                                    Intent intent = new Intent(mContext, activityClass);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(intent);
                                } catch (SocketException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.i("echo_tag", "视频请求失败");
                                Toast.makeText(mContext, "视频请求失败", Toast.LENGTH_SHORT).show();
                            }
                            SipInfo.isWaitingFeedback = false;
                        }
                        Looper.loop();
                    }
                }.start();
                Looper.loop();
            }
        }).start();

    }

}
