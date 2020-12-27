package com.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.app.groupvoice.GroupInfo;
import com.app.groupvoice.GroupSignaling;
import com.app.sip.SipInfo;
import com.app.tools.MyToast;
import com.app.ui.MovieRecord;
import com.app.ui.MyCamera;
import com.app.video.H264Sending;
import com.app.video.H264SendingManager;
import com.app.video.VideoInfo;


public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        int keyCode = keyEvent.getKeyCode();
        System.out.println(keyCode);
        int state = keyEvent.getAction();
        switch (keyCode) {
            case 260:
                if (state == 0) {
                    if (SipInfo.flag) {
                        MyToast.show(context, "SOS键按下", Toast.LENGTH_SHORT);
                        Intent movieRecord = new Intent(context, MovieRecord.class);
//                        if (!SipInfo.Recording) {
                    movieRecord.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(movieRecord);
//                            context.startService(movieRecord);
//                            SipInfo.Recording = true;
//                        } else {
//                            context.stopService(movieRecord);
//                            SipInfo.Recording = false;
//                        }
                    }
                }
                break;
            case 261:
                MyToast.show(context, "PTT键按下", Toast.LENGTH_SHORT);
                System.out.println("state = " + state);
                if (state == 0) {
                    MyToast.show(context, "正在说话...", Toast.LENGTH_LONG);
                    if (GroupInfo.rtpAudio != null) {
                        System.out.println(111);
                        GroupInfo.rtpAudio.pttChanged(true);
                        if (VideoInfo.track != null) {
                            VideoInfo.track.stop();
                        }
                        H264SendingManager.G711Running = false;
                        waitFor();
                        GroupSignaling groupSignaling = new GroupSignaling();
                        groupSignaling.setStart(SipInfo.devId);
                        groupSignaling.setLevel(GroupInfo.level);
                        String start = JSON.toJSONString(groupSignaling);
                        GroupInfo.groupUdpThread.sendMsg(start.getBytes());
                    }
                } else {
                    MyToast.show(context, "结束说话...", Toast.LENGTH_LONG);
                    if (GroupInfo.rtpAudio != null) {
                        System.out.println(222);
                        GroupInfo.rtpAudio.pttChanged(false);
                        if (GroupInfo.isSpeak) {
                            GroupSignaling groupSignaling = new GroupSignaling();
                            groupSignaling.setEnd(SipInfo.userId);
                            String end = JSON.toJSONString(groupSignaling);
                            GroupInfo.groupUdpThread.sendMsg(end.getBytes());
                            waitFor();
                            if (VideoInfo.track != null) {
                                VideoInfo.track.play();
                            }
                            //发送消息通知H264Sending重新开启G711_encode线程
                            if (VideoInfo.handler != null)
                                VideoInfo.handler.sendEmptyMessage(0x1111);
                        }
                    }
                }
                break;
            case 27:
                if (SipInfo.flag) {
                    MyToast.show(context, "照相机键按下", Toast.LENGTH_SHORT);
                    Intent camera = new Intent(context, MyCamera.class);
                    camera.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(camera);
                }
                break;
            case 131://P91上的录像键
                if (state == 0) {
                    if (SipInfo.flag) {
                        Intent Record = new Intent(context, MovieRecord.class);
//                        if (!SipInfo.Recording) {
                    Record.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(Record);
//                            context.startService(Record);
//                            SipInfo.Recording = true;
//                        } else {
//                            context.stopService(Record);
//                            SipInfo.Recording = false;
//                        }
                    }
                }
                break;

        }
    }

    private void waitFor() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
