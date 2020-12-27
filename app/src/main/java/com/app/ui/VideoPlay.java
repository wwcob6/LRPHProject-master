package com.app.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.R;
import com.app.audio.AudioRecordManager;
import com.app.model.MessageEvent;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.sip.SipUser;
import com.app.tools.H264decoder;
import com.app.video.VideoInfo;
import com.punuo.sys.app.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zoolu.sip.message.Message;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author chzjy
 * Date 2016/12/19.
 */
public class VideoPlay extends BaseActivity implements SurfaceHolder.Callback,SipUser.StopMonitor {
    public static final String TAG = "VideoPlay";
    private final byte[] SPS_DM365_CIF = {0x00, 0x00, 0x00, 0x01, 0x27, 0x64, 0x00, 0x1f, (byte) 0xad, (byte) 0x84, 0x09, 0x26, 0x6e, 0x23, 0x34, (byte) 0x90, (byte) 0x81, 0x24, (byte) 0xcd, (byte) 0xc4, 0x66, (byte) 0x92, 0x10, 0x24, (byte) 0x99, (byte) 0xb8, (byte) 0x8c, (byte) 0xd2, 0x42, 0x04, (byte) 0x93, 0x37, 0x11, (byte) 0x9a, 0x48, 0x40, (byte) 0x92, 0x66, (byte) 0xe2, 0x33, 0x49, 0x08, 0x12, 0x4c, (byte) 0xdc, 0x46, 0x69, 0x21, 0x05, 0x5a, (byte) 0xeb, (byte) 0xd7, (byte) 0xd7, (byte) 0xe4, (byte) 0xfe, (byte) 0xbf, 0x27, (byte) 0xd7, (byte) 0xae, (byte) 0xb5, 0x50, (byte) 0x82, (byte) 0xad, 0x75, (byte) 0xeb, (byte) 0xeb, (byte) 0xf2, 0x7f, 0x5f, (byte) 0x93, (byte) 0xeb, (byte) 0xd7, 0x5a, (byte) 0xab, 0x40, (byte) 0xb0, 0x4b, 0x20};
    private final byte[] PPS_DM365_CIF = {0x00, 0x00, 0x00, 0x01, 0x28, (byte) 0xee, 0x3c, (byte) 0xb0};
    private final byte[] SPS_MOBILE_QCIF = {0x00, 0x00, 0x00, 0x01, 0x27, 0x42, 0x10, 0x09, (byte) 0x96, 0x35, 0x05, (byte) 0x89, (byte) 0xc8};
    private final byte[] PPS_MOBILE_QCIF = {0x00, 0x00, 0x00, 0x01, 0x28, (byte) 0xce, 0x02, (byte) 0xfc, (byte) 0x80};
    //    private final byte[] SPS_MOBILE_CIF = {0x00, 0x00, 0x00, 0x01, 0x67, 0x42, 0x00, 0x29, (byte) 0x8d, (byte) 0x8d, 0x40, (byte) 0xb0, 0x4b, 0x40, 0x3c, 0x22, 0x11, 0x4e};
//    private final byte[] PPS_MOBILE_CIF = {0x00, 0x00, 0x00, 0x01, 0x68, (byte) 0xca, 0x43, (byte) 0xc8};
    private final byte[] SPS_MOBILE_S6 = {0x00, 0x00, 0x00, 0x01, 0x27, 0x42, 0x00, 0x33, (byte) 0xe3, 0x50, 0x28, (byte) 0x3f, (byte) 0x20};
    private final byte[] PPS_MOBILE_S6 = {0x00, 0x00, 0x00, 0x01, 0x28, (byte) 0xce, 0x02, (byte) 0xfc, (byte) 0x80};
    private final byte[] SPS_MOBILE_S9 = {0x00, 0x00, 0x00, 0x01, 0x27, 0x42, 0x10, 0x09, (byte) 0x96, 0x35, 0x02, (byte) 0x83, (byte) 0xf2};
    private final byte[] PPS_MOBILE_S9 = {0x00, 0x00, 0x00, 0x01, 0x28, (byte) 0xce, 0x02, (byte) 0xfc, (byte) 0x80};
    private SurfaceHolder surfaceHolder;
    //    private byte[] mPixel = new byte[VideoInfo.width * VideoInfo.height * 2];
//    private ByteBuffer buffer = ByteBuffer.wrap(mPixel);
//    private Bitmap videoBit = Bitmap.createBitmap(VideoInfo.width, VideoInfo.height, Bitmap.Config.RGB_565);
    private int getNum = 0;
    Timer timer = new Timer();
    private H264decoder h264decoder;
    AlertDialog dialog;
    @Bind(R.id.surfaceView)
    SurfaceView surfaceView;
    int time=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);
        SipInfo.sipUser.setMonitor(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        h264decoder=new H264decoder();
        ImageView left=(ImageView)findViewById(R.id.left);
        ImageView right=(ImageView)findViewById(R.id.right);
        ImageView stop=(ImageView)findViewById(R.id.stop);
        EventBus.getDefault().register(this);
//        left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
//                        SipInfo.user_from, BodyFactory.createVideoControlResBody("left"));
//                SipInfo.sipUser.sendMessage(query);
//            }
//        });
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                            SipInfo.user_from, BodyFactory.createVideoControlResBody("right"));
                    SipInfo.sipUser.sendMessage(query);
                }
//                    handler.postDelayed(mLongPressed,1000);

                if(event.getAction()==MotionEvent.ACTION_UP){
//                    handler.removeCallbacks(mLongPressed);
                    Message query1 = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                            SipInfo.user_from, BodyFactory.createVideoControlResBody("stop"));
                    SipInfo.sipUser.sendMessage(query1);
                }
                return true;
            }
        });
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                            SipInfo.user_from, BodyFactory.createVideoControlResBody("left"));
                    SipInfo.sipUser.sendMessage(query);
                }
//                    handler.postDelayed(mLongPressed,1000);

                if(event.getAction()==MotionEvent.ACTION_UP){
//                    handler.removeCallbacks(mLongPressed);
                    Message query1 = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                            SipInfo.user_from, BodyFactory.createVideoControlResBody("stop"));
                    SipInfo.sipUser.sendMessage(query1);
                }
                return true;
            }
        });

//        right.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
//                        SipInfo.user_from, BodyFactory.createVideoControlResBody("right"));
//                SipInfo.sipUser.sendMessage(query);
//            }h
//        });
//        stop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
//                        SipInfo.user_from, BodyFactory.createVideoControlResBody("stop"));
//                SipInfo.sipUser.sendMessage(query);
//            }
//        });
        ImageView back1=(ImageView)findViewById(R.id.back1);
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeVideo();
            }
        });

        playVideo();
        timer.schedule(task, 0, 10000);
    }
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (VideoInfo.isrec == 0) {
                if (time==6){
                    closeVideo();
                    time=0;
                }
                else {
                    new Handler(VideoPlay.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(VideoPlay.this, "未收到消息!", Toast.LENGTH_SHORT).show();

                        }
                    });
                    time++;
                }
            } else if (VideoInfo.isrec == 2) {
                VideoInfo.isrec = 0;
                time=0;
            }
        }
    };
//    TimerTask task = new TimerTask() {
//
//        @Override
//        public void run() {
//            if (VideoInfo.isrec == 0) {
//                closeVideo();
//            } else if (VideoInfo.isrec == 2) {
//                VideoInfo.isrec = 0;
//            }
//        }
//    };

    final Handler handler = new Handler();
    final Runnable mLongPressed = new Runnable() {
        public void run() {
            // 长按处理
            Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                    SipInfo.user_from, BodyFactory.createVideoControlResBody("right"));
            SipInfo.sipUser.sendMessage(query);
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        if(dialog!=null){
        dialog.dismiss();}
        VideoInfo.isrec = 1;
        SipInfo.decoding = false;
        VideoInfo.rtpVideo.removeParticipant();
        VideoInfo.sendActivePacket.stopThread();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
//                    mFFmpeg.Destroy();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        VideoInfo.rtpVideo.endSession();
        AudioRecordManager.getInstance().stop();
//        buffer.clear();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
        System.gc();//系统垃圾回收
    }

    private void playVideo() {
        new Thread(Video).start();
    }
    long a;
    Runnable Video = new Runnable() {
        @Override
        public void run() {
            //软解码设置
//            mFFmpeg.init(VideoInfo.width, VideoInfo.height);
//            switch (VideoInfo.videoType) {
//                case 2:
//                    mFFmpeg.DecoderNal(SPS_DM365_CIF, 78, mPixel);
//                    mFFmpeg.DecoderNal(PPS_DM365_CIF, 8, mPixel);
//                    break;
//                case 3:
//                    mFFmpeg.DecoderNal(SPS_MOBILE_QCIF, 13, mPixel);
//                    mFFmpeg.DecoderNal(PPS_MOBILE_QCIF, 9, mPixel);
//                    break;
//                case 4:
//                    mFFmpeg.DecoderNal(SPS_MOBILE_S6, 13, mPixel);
//                    mFFmpeg.DecoderNal(PPS_MOBILE_S6, 9, mPixel);
//                    break;
//                case 5:
//                    mFFmpeg.DecoderNal(SPS_MOBILE_S9, 13, mPixel);
//                    mFFmpeg.DecoderNal(PPS_MOBILE_S9, 9, mPixel);
//                    break;
//                default:
//                    break;
//            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Surface surface=surfaceHolder.getSurface();
            System.out.println(surface);
            if (surface!=null) {
                h264decoder.initDecoder(surface);
                while (SipInfo.decoding) {
                    if (SipInfo.isNetworkConnected) {
                        byte[] nal = VideoInfo.nalBuffers[getNum].getReadableNalBuf();
                        if (nal != null) {
                            Log.i(TAG, "nalLen:" + nal.length);
                            try {
                                //软解码
//                            int iTemp = mFFmpeg.DecoderNal(nal, nal.length, mPixel);
//
//                            if (iTemp > 0) {
//                                doSurfaceDraw();
//                            }

                                //硬解码
//                                saveH264DataToFile(nal);
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
    private BufferedOutputStream mH264DataFile;
    private void saveH264DataToFile(byte[] dataToWrite) {
        File f = new File(Environment.getExternalStorageDirectory(), "DCM/video_encoded.264");

        try {
            mH264DataFile = new BufferedOutputStream(new FileOutputStream(f));
            Log.i("AvcEncoder", "outputStream initialized");
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            mH264DataFile.write(dataToWrite, 0, dataToWrite.length);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }




//      软解码呈现画面
//    private void doSurfaceDraw() {
//        videoBit.copyPixelsFromBuffer(buffer);
//        buffer.position(0);
//        Log.i(TAG, "doSurfaceDraw");
//
//        int surfaceViewWidth = surfaceView.getWidth();
//        int surfaceViewHeight = surfaceView.getHeight();
//        int bmpWidth = videoBit.getWidth();
//        int bmpHeight = videoBit.getHeight();
//        System.out.println("bmpHeight = " + bmpHeight);
//        System.out.println("bmpWidth = " + bmpWidth);
//        System.out.println("surfaceViewHeight = " + surfaceViewHeight);
//        System.out.println("surfaceViewWidth = " + surfaceViewWidth);
//        Matrix matrix = new Matrix();
//        matrix.postScale(4.3f, 4.3f);
//        Bitmap resizeBmp = Bitmap.createBitmap(videoBit, 0, 0, bmpWidth, bmpHeight, matrix, true);
//        if (VideoInfo.videoType==4||VideoInfo.videoType==5) {
//            resizeBmp = adjustPhotoRotation(resizeBmp, 90);
//        }
//        if (surfaceHolder!=null) {
//            Canvas canvas = surfaceHolder.lockCanvas();
//            canvas.drawBitmap(resizeBmp, 0, 0, null);
//            surfaceHolder.unlockCanvasAndPost(canvas);
//        }
//    }

    Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }
        final float[] values = new float[9];
        m.getValues(values);
        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];
        m.postTranslate(targetX - x1, targetY - y1);
        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);
        return bm1;
    }

    @Override
    public void onBackPressed() {
        dialog = new AlertDialog.Builder(this)
                .setTitle("是否结束监控?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeVideo();
//                        SipInfo.IsVideoOn=false;
//                        dialog.dismiss();
                        finish();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("关闭视频")) {
            Log.i(TAG, "message is " + event.getMessage());
            closeVideo();
        }else
            if(event.getMessage().equals("停止浏览")){
                closeVideo();
            }
    }
    private  void closeVideo() {
        Message bye = SipMessageFactory.createByeRequest(SipInfo.sipUser, SipInfo.toDev, SipInfo.user_from);
        //创建结束视频请求
        SipInfo.sipUser.sendMessage(bye);
        EventBus.getDefault().post(new MessageEvent("结束"));
        finish();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void stopVideo() {
        closeVideo();
    }
}
//public class VideoPlay extends BaseActivity{
//    public static final String TAG = "VideoPlay";
//    private final byte[] SPS_DM365_CIF = {0x00, 0x00, 0x00, 0x01, 0x27, 0x64, 0x00, 0x1f, (byte) 0xad, (byte) 0x84, 0x09, 0x26, 0x6e, 0x23, 0x34, (byte) 0x90, (byte) 0x81, 0x24, (byte) 0xcd, (byte) 0xc4, 0x66, (byte) 0x92, 0x10, 0x24, (byte) 0x99, (byte) 0xb8, (byte) 0x8c, (byte) 0xd2, 0x42, 0x04, (byte) 0x93, 0x37, 0x11, (byte) 0x9a, 0x48, 0x40, (byte) 0x92, 0x66, (byte) 0xe2, 0x33, 0x49, 0x08, 0x12, 0x4c, (byte) 0xdc, 0x46, 0x69, 0x21, 0x05, 0x5a, (byte) 0xeb, (byte) 0xd7, (byte) 0xd7, (byte) 0xe4, (byte) 0xfe, (byte) 0xbf, 0x27, (byte) 0xd7, (byte) 0xae, (byte) 0xb5, 0x50, (byte) 0x82, (byte) 0xad, 0x75, (byte) 0xeb, (byte) 0xeb, (byte) 0xf2, 0x7f, 0x5f, (byte) 0x93, (byte) 0xeb, (byte) 0xd7, 0x5a, (byte) 0xab, 0x40, (byte) 0xb0, 0x4b, 0x20};
//    private final byte[] PPS_DM365_CIF = {0x00, 0x00, 0x00, 0x01, 0x28, (byte) 0xee, 0x3c, (byte) 0xb0};
//    private final byte[] SPS_MOBILE_QCIF = {0x00, 0x00, 0x00, 0x01, 0x27, 0x42, 0x10, 0x09, (byte) 0x96, 0x35, 0x05, (byte) 0x89, (byte) 0xc8};
//    private final byte[] PPS_MOBILE_QCIF = {0x00, 0x00, 0x00, 0x01, 0x28, (byte) 0xce, 0x02, (byte) 0xfc, (byte) 0x80};
////    private final byte[] SPS_MOBILE_CIF = {0x00, 0x00, 0x00, 0x01, 0x67, 0x42, 0x00, 0x29, (byte) 0x8d, (byte) 0x8d, 0x40, (byte) 0xb0, 0x4b, 0x40, 0x3c, 0x22, 0x11, 0x4e};
////    private final byte[] PPS_MOBILE_CIF = {0x00, 0x00, 0x00, 0x01, 0x68, (byte) 0xca, 0x43, (byte) 0xc8};
//    private final byte[] SPS_MOBILE_S6 = {0x00, 0x00, 0x00, 0x01, 0x27, 0x42, 0x00, 0x33, (byte) 0xe3, 0x50, 0x28, (byte) 0x3f, (byte) 0x20};
//    private final byte[] PPS_MOBILE_S6 = {0x00, 0x00, 0x00, 0x01, 0x28, (byte) 0xce, 0x02, (byte) 0xfc, (byte) 0x80};
//    private final byte[] SPS_MOBILE_S9 = {0x00, 0x00, 0x00, 0x01, 0x27, 0x42, 0x10, 0x09, (byte) 0x96, 0x35, 0x02, (byte) 0x83, (byte) 0xf2};
//    private final byte[] PPS_MOBILE_S9 = {0x00, 0x00, 0x00, 0x01, 0x28, (byte) 0xce, 0x02, (byte) 0xfc, (byte) 0x80};
//    private ffmpeg mFFmpeg = new ffmpeg();
//    private SurfaceHolder surfaceHolder;
//    private byte[] mPixel = new byte[VideoInfo.width * VideoInfo.height * 2];
//    private ByteBuffer buffer = ByteBuffer.wrap(mPixel);
//    private Bitmap videoBit = Bitmap.createBitmap(VideoInfo.width, VideoInfo.height, Bitmap.Config.RGB_565);
//    private int getNum = 0;
//    Timer timer = new Timer();
//    @Bind(R.id.surfaceView)
//    SurfaceView surfaceView;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video_play);
//        ButterKnife.bind(this);
//
//        surfaceHolder = surfaceView.getHolder();
//
//        playVideo();
//        timer.schedule(task, 0, 2000);
//    }
//    TimerTask task = new TimerTask() {
//
//        @Override
//        public void run() {
//            if (VideoInfo.isrec == 0) {
//                closeVideo();
//            } else if (VideoInfo.isrec == 2) {
//                VideoInfo.isrec = 0;
//            }
//        }
//    };
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        timer.cancel();
//        VideoInfo.isrec = 1;
//        SipInfo.decoding = false;
//        VideoInfo.rtpVideo.removeParticipant();
//        VideoInfo.sendActivePacket.stopThread();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                    mFFmpeg.Destroy();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        VideoInfo.rtpVideo.endSession();
//        VideoInfo.track.stop();
//        buffer.clear();
//    }
//
//    private void playVideo() {
//        new Thread(Video).start();
//    }
//    long a;
//    Runnable Video = new Runnable() {
//        @Override
//        public void run() {
//            mFFmpeg.init(VideoInfo.width, VideoInfo.height);
//            switch (VideoInfo.videoType) {
//                case 2:
//                    mFFmpeg.DecoderNal(SPS_DM365_CIF, 78, mPixel);
//                    mFFmpeg.DecoderNal(PPS_DM365_CIF, 8, mPixel);
//                    break;
//                case 3:
//                    mFFmpeg.DecoderNal(SPS_MOBILE_QCIF, 13, mPixel);
//                    mFFmpeg.DecoderNal(PPS_MOBILE_QCIF, 9, mPixel);
//                    break;
//                case 4:
//                    mFFmpeg.DecoderNal(SPS_MOBILE_S6, 13, mPixel);
//                    mFFmpeg.DecoderNal(PPS_MOBILE_S6, 9, mPixel);
//                    break;
//                case 5:
//                    mFFmpeg.DecoderNal(SPS_MOBILE_S9, 13, mPixel);
//                    mFFmpeg.DecoderNal(PPS_MOBILE_S9, 9, mPixel);
//                    break;
//                default:
//                    break;
//            }
//            while (SipInfo.decoding) {
//                if (SipInfo.isNetworkConnected) {
//                    byte[] nal = VideoInfo.nalBuffers[getNum].getReadableNalBuf();
//                    if (nal != null) {
//                        Log.i(TAG, "nalLen:" + nal.length);
//                        try {
//                            int iTemp = mFFmpeg.DecoderNal(nal, nal.length, mPixel);
//
//                            if (iTemp > 0) {
//                                doSurfaceDraw();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    VideoInfo.nalBuffers[getNum].readLock();
//                    VideoInfo.nalBuffers[getNum].cleanNalBuf();
//                    getNum++;
//                    if (getNum == 200) {
//                        getNum = 0;
//                    }
//                }
//            }
//        }
//    };
//
//    private void doSurfaceDraw() {
//        videoBit.copyPixelsFromBuffer(buffer);
//        buffer.position(0);
//        Log.i(TAG, "doSurfaceDraw");
//
//        int surfaceViewWidth = surfaceView.getWidth();
//        int surfaceViewHeight = surfaceView.getHeight();
//        int bmpWidth = videoBit.getWidth();
//        int bmpHeight = videoBit.getHeight();
//        System.out.println("bmpHeight = " + bmpHeight);
//        System.out.println("bmpWidth = " + bmpWidth);
//        System.out.println("surfaceViewHeight = " + surfaceViewHeight);
//        System.out.println("surfaceViewWidth = " + surfaceViewWidth);
//        Matrix matrix = new Matrix();
//        matrix.postScale(4.3f, 4.3f);
//        Bitmap resizeBmp = Bitmap.createBitmap(videoBit, 0, 0, bmpWidth, bmpHeight, matrix, true);
//        if (VideoInfo.videoType==4||VideoInfo.videoType==5) {
//            resizeBmp = adjustPhotoRotation(resizeBmp, 90);
//        }
//        if (surfaceHolder!=null) {
//            Canvas canvas = surfaceHolder.lockCanvas();
//            canvas.drawBitmap(resizeBmp, 0, 0, null);
//            surfaceHolder.unlockCanvasAndPost(canvas);
//        }
//    }
//
//    Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
//        Matrix m = new Matrix();
//        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
//        float targetX, targetY;
//        if (orientationDegree == 90) {
//            targetX = bm.getHeight();
//            targetY = 0;
//        } else {
//            targetX = bm.getHeight();
//            targetY = bm.getWidth();
//        }
//        final float[] values = new float[9];
//        m.getValues(values);
//        float x1 = values[Matrix.MTRANS_X];
//        float y1 = values[Matrix.MTRANS_Y];
//        m.postTranslate(targetX - x1, targetY - y1);
//        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
//        Paint paint = new Paint();
//        Canvas canvas = new Canvas(bm1);
//        canvas.drawBitmap(bm, m, paint);
//        return bm1;
//    }
//
//    @Override
//    public void onBackPressed() {
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("是否结束监控?")
//                .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        closeVideo();
//                    }
//                })
//                .setNegativeButton("否", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .create();
//        dialog.show();
//        dialog.setCanceledOnTouchOutside(false);
//    }
//
//    private void closeVideo() {
//        Message bye = SipMessageFactory.createByeRequest(SipInfo.sipUser, SipInfo.toDev, SipInfo.user_from);
//        SipInfo.sipUser.sendMessage(bye);
//        finish();
//    }
//}
