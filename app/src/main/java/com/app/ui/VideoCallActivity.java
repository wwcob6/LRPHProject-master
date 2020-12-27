package com.app.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.R;
import com.app.audio.AudioRecordManager;
import com.app.model.MessageEvent;
import com.app.service.SipService;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.sip.SipUser;
import com.app.tools.H264decoder;
import com.app.video.H264SendingManager;
import com.app.video.VideoInfo;
import com.punuo.sys.app.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author chzjy
 * Date 2016/12/19.
 */
public class VideoCallActivity extends BaseActivity implements SipUser.StopMonitor {
    public static final String TAG = "VideoCallActivity";
    private BufferedOutputStream outputStream;
    @Bind(R.id.mute)
    ImageView mute;
    @Bind(R.id.hangup)
    ImageView hangup;
    @Bind(R.id.HF)
    ImageView HF;
    private int currVolume = 0;
    private SurfaceHolder shBack;
    private int getNum = 0;
    Timer timer = new Timer();
    private H264decoder h264decoder;
    AlertDialog dialog;
    @Bind(R.id.sv_back)
    SurfaceView svBack;
    @Bind(R.id.sv_front)
    SurfaceView svFront;
    int time = 0;
    H264SendingManager sendingManager;

    public static final String BROADCAST_ACTION = "BROADCAST_ACTION";
    IntentFilter imIntentFilter;
    LocalBroadcastManager mManager;
    BroadcastReceiver mReceiver;
    private boolean isSpeakerMode = false;
    private boolean ismute=false;
    File f = new File(Environment.getExternalStorageDirectory(), "DCIM/video_encoded1.264");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        ButterKnife.bind(this);
        //防止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SipInfo.sipUser.setMonitor(this);

        shBack = svBack.getHolder();

        //shFront.setFormat(PixelFormat.TRANSPARENT);
        svFront.setZOrderOnTop(true);
        svFront.setZOrderMediaOverlay(true);

        sendingManager = new H264SendingManager(svFront);
        sendingManager.init();
        h264decoder = new H264decoder();

        playVideo();
        timer.schedule(task, 0, 10000);

        imIntentFilter = new IntentFilter();
        imIntentFilter.addAction(BROADCAST_ACTION);
        mManager = LocalBroadcastManager.getInstance(VideoCallActivity.this);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                VideoCallActivity.this.finish();
            }
        };

// Check if we have write PermissionUtils
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have PermissionUtils so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

    }
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };




    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (VideoInfo.isrec == 0) {
                if (time == 6) {
                    closeVideo();
                    time = 0;
                } else {
                    new Handler(VideoCallActivity.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(VideoCallActivity.this, "未收到消息!", Toast.LENGTH_SHORT).show();

                        }
                    });
                    time++;
                }
            } else if (VideoInfo.isrec == 2) {
                VideoInfo.isrec = 0;
                time = 0;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        mManager.registerReceiver(mReceiver, imIntentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mManager.unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();

        //重置免提
//        serviceBinder.setSpeakerMode(false);
        if (dialog != null) {
            dialog.dismiss();
        }
        VideoInfo.isrec = 1;
        SipInfo.decoding = false;
        VideoInfo.rtpVideo.removeParticipant();
        VideoInfo.sendActivePacket.stopThread();
        sendingManager.deInit();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        VideoInfo.rtpVideo.endSession();
        AudioRecordManager.getInstance().stop();
        ButterKnife.unbind(this);
        System.gc();//系统垃圾回收
    }

    private void playVideo() {
        new Thread(Video).start();
        EventBus.getDefault().post(new MessageEvent("开始视频"));
    }

    Runnable Video = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Surface surface = shBack.getSurface();
            System.out.println(surface);

            if (surface != null) {
                h264decoder.initDecoder(surface);
                while (SipInfo.decoding) {
                    if (SipInfo.isNetworkConnected) {
                        byte[] nal = VideoInfo.nalBuffers[getNum].getReadableNalBuf();
                        try {
                            outputStream = new BufferedOutputStream(new FileOutputStream(f));
                            Log.i("AvcEncoder", "outputStream initialized");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            outputStream.write(nal,0,nal.length);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (nal != null) {
                            Log.i(TAG, "nalLen:" + nal.length);

                            try {
                                //硬解码
                                h264decoder.onFrame(nal, 0, nal.length);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        VideoInfo.nalBuffers[getNum].readLock();
                        VideoInfo.nalBuffers[getNum].cleanNalBuf();
                        getNum++;
                        if (getNum == 200) {
                            getNum = 0;
                        }
                    }
                }
            }
        }
    };

    private SipService.MyBinder serviceBinder;
    @OnClick({R.id.mute,R.id.hangup,R.id.HF})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.hangup:
                closeVideo();
                break;
            case R.id.HF:

//                openSpeaker();
//                setSpeakerphoneOn(true);
                if (isSpeakerMode) {
                    HF.setImageResource(R.drawable.ic_hf);
                    openSpeaker();
//                SipAudioCall.setSpeakerMode(false);
                    isSpeakerMode = false;
            } else {
                    HF.setImageResource(R.drawable.ysq);
//                serviceBinder.setSpeakerMode(true);
                    closeSpeaker();
                isSpeakerMode = true;
            }
                break;
            case R.id.mute:
                if(ismute){
                    mute.setImageResource(R.drawable.jingyin);
                    ismute=false;
                }else{
                    mute.setImageResource(R.drawable.ic_mute);
                    ismute=true;
                }
                break;
            case R.id.sv_front:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        dialog = new AlertDialog.Builder(this)
                .setTitle("是否结束聊天?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeVideo();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    private void closeVideo() {
        Message bye = SipMessageFactory.createByeRequest(SipInfo.sipUser, SipInfo.toDev, SipInfo.user_from);
        //创建结束视频请求
        SipInfo.sipUser.sendMessage(bye);
        finish();
    }

    @Override
    public void stopVideo() {
        closeVideo();
    }

//    private AudioManager audioManager =
//            (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
    /**
     * 扬声器与听筒切换
     * @param isSpeakerphoneOn
     */
//    public void setSpeakerphoneOn(boolean isSpeakerphoneOn){
//        audioManager.setSpeakerphoneOn(isSpeakerphoneOn);
//        if(!isSpeakerphoneOn){
//            audioManager.setMode(AudioManager.MODE_NORMAL);
//        }
//    }


    /**
     * 打开扬声器
     */
    private void openSpeaker() {
        try{
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if(!audioManager.isSpeakerphoneOn()) {
                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 关闭扬声器
     */
    public void closeSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if(audioManager != null) {
                if(audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
