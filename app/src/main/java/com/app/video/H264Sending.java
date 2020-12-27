package com.app.video;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.StrictMode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.app.R;
import com.app.groupvoice.G711;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.tools.AECManager;
import com.app.tools.AvcEncoder;
import com.punuo.sys.app.activity.BaseActivity;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.app.sip.SipInfo.devName;


/**
 * Author chzjy
 * Date 2016/12/19.
 */
public class H264Sending extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    @Bind(R.id.h264suf)
    SurfaceView h264suf;

    SurfaceHolder m_surfaceHolder;
    public static RTPSending rtpsending = null;
    private String TAG = H264Sending.class.getSimpleName();    //取得类名
    public static boolean G711Running = true;
    int frameSizeG711 = 160;
    private final int previewFrameRate = 10;  //演示帧率
    private final int previewWidth = 352;     //水平像素
    private final int previewHeight = 288;     //垂直像素
//    private final int previewWidth = 1280;     //水平像素
//    private final int previewHeight = 720;     //垂直像素
    private AvcEncoder avcEncoder;
    /**
     * 手机摄像头的个数
     */
    private int numCamera;
    /**
     * 前置摄像头的Id
     */
    private int cameraId_front = -1;
    /**
     * 后置摄像头的Id
     */
    private int cameraId_back = -1;
    //外置摄像头的Id
    private int cameraId_out = -1;
    /**
     * 判断前置摄像头是否存在的标志位
     */
    private boolean frontExist = false;
    /**
     * 打包发送的数组大小定义
     */
    byte[] rtppkt = new byte[VideoInfo.divide_length + 2];

    public Camera mCamera;

    private boolean isStop = false;

    private long time = System.currentTimeMillis();   //以毫秒形式返回当前系统时间
    private int cameraState = 0;
    Camera.Parameters parame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h264sending);
        ButterKnife.bind(this);
//        Button qiehuan=(Button)findViewById(R.id.bt_qiehuan);
//        qiehuan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (cameraState==cameraId_back){
//                    mCamera = Camera.open(cameraId_front);
//                    cameraState=cameraId_front;
//                }else if(cameraState==cameraId_front){
//                    mCamera = Camera.open(cameraId_back);
//                    cameraState=cameraId_back;
//                }
//            }
//        });

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (rtpsending != null) {
            rtpsending = null;
        }
        rtpsending = new RTPSending();
        // 得到SurfaceHolder对象
        SurfaceHolder holder = h264suf.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //设置回调函数
        holder.addCallback(H264Sending.this);   //添加回调接口
        //设置风格
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        long Ssrc = (VideoInfo.media_info_magic[15] & 0x000000ff) | ((VideoInfo.media_info_magic[14] << 8) & 0x0000ff00) | ((VideoInfo.media_info_magic[13] << 16) & 0x00ff0000) | ((VideoInfo.media_info_magic[12] << 24) & 0xff000000);
        rtpsending.rtpSession2.setSsrc(Ssrc);
        G711Running = true;
        G711_recored();
        VideoInfo.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                G711Running = true;
                G711_recored();
            }
        };
        Log.d(TAG, "创建成功");

        //软解码初始化
//        NativeH264Encoder.InitEncoder(previewWidth, previewHeight, previewFrameRate);
        avcEncoder = new AvcEncoder();
        SipInfo.flag = false;
    }

    /**
     * g711采集编码线程
     */
    private void G711_recored() {
        new Thread(G711_encode).start();
    }

    Runnable G711_encode = new Runnable() {
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
            AudioRecord record = getAudioRecord();
            //int frame_size = 160;
            short[] audioData = new short[frameSizeG711];
            byte[] encodeData = new byte[frameSizeG711];
            int numRead = 0;

            while (G711Running) {
                numRead = record.read(audioData, 0, frameSizeG711);
                if (numRead <= 0) continue;
                calc2(audioData, 0, numRead);
                //进行pcmu编码
                G711.linear2ulaw(audioData, 0, encodeData, numRead);
                rtpsending.rtpSession2.payloadType(0x45);
                rtpsending.rtpSession2.sendData(encodeData);
            }
            record.stop();
            record.release();
            Log.i("zlj", "G711_encode stopped!");
        }
    };

    void calc2(short[] lin, int off, int len) {
        int i, j;

        for (i = 0; i < len; i++) {
            j = lin[i + off];
            lin[i + off] = (short) (j >> 1);
        }
    }

    /**
     * 取得音频采集对象引用
     */
    private AudioRecord getAudioRecord() {
        int samp_rate = 8000;
        int min = AudioRecord.getMinBufferSize(samp_rate,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        Log.e(TAG, "min buffer size:" + min);

        AudioRecord record = null;
        record = new AudioRecord(
                MediaRecorder.AudioSource.MIC,//the recording source
                samp_rate, //采样频率，一般为8000hz/s
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                min);

        if(AECManager.isDeviceSupport()){
            AECManager.getInstance().initAEC(record.getAudioSessionId());
        }
        record.startRecording();
        return record;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);

        AECManager.getInstance().release();
        VideoInfo.handler = null;
        if (mCamera != null)  //没有背面摄像头的情况
        {
            mCamera.setPreviewCallback(null);//must do this，停止接收回叫信号
            mCamera.stopPreview();   //停止捕获和绘图
            mCamera.release();   //断开与摄像头的连接，并释放摄像头资源
            mCamera = null;
        }
        avcEncoder.close();
        rtpsending = null;
        SipInfo.flag = true;
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated: ");
        m_surfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged: ");
        numCamera = Camera.getNumberOfCameras();
        Log.i(TAG, "摄像头个数为" + numCamera);
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < numCamera; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId_back = i;     //获取后置摄像头的Id
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId_front = i;    //获取前置摄像头的Id
                frontExist = true;
            } else {
                cameraId_out = i;
            }
        }
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);//停止接收回调信号
            mCamera.stopPreview();//停止捕获和绘图
            mCamera.release();
            mCamera = null;
        }
        try {
            mCamera = Camera.open(cameraId_out);
            cameraState = cameraId_out;
        } catch (Exception e) {
            try {
                mCamera = Camera.open(cameraId_front);
                cameraState = cameraId_front;
            } catch (Exception e1) {
                Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        try {
            if (mCamera == null) return;
            mCamera.setPreviewDisplay(m_surfaceHolder);
            mCamera.setPreviewCallback(this);
            mCamera.setDisplayOrientation(90);//显示的图像旋转
            parame = mCamera.getParameters();    //获取配置参数对象
            parame.setPreviewFrameRate(previewFrameRate);    //设置Camera的演示帧率
            parame.setPreviewFormat(ImageFormat.YV12);
            //s6 YV12 S9 NV21
            parame.setPreviewSize(previewWidth, previewHeight);    //设置屏幕分辨率
            parame.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            //android2.3.3以后无需下步
            mCamera.setParameters(parame);
            //开始对演示帧进行捕获和绘图到surface
            mCamera.startPreview();
            // 自动对焦
//            mCamera.autoFocus(new Camera.AutoFocusCallback() {
//                @Override
//                public void onAutoFocus(boolean success, Camera camera) {
//                    if (success) {
//                        Camera.Parameters parameters = camera.getParameters();
//                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
//                        camera.setParameters(parameters);
//                        camera.startPreview();
//                        camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
//                    }
//                }
//
//            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed: ");
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("是否要关闭监控?")
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String devId = SipInfo.paddevId;
                        SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                        SipInfo.toDev = new NameAddress(devName, sipURL);
                        org.zoolu.sip.message.Message response = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                                SipInfo.user_from, BodyFactory.createStopMonitor(devId));
                        SipInfo.sipUser.sendMessage(response);
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.i(TAG, "网络连接状况:" + (SipInfo.isNetworkConnected ? "正常" : "不正常"));
        Log.e(TAG, "onPreviewFrame: " + data.length);
        if (SipInfo.isNetworkConnected) {
            if (VideoInfo.endView) {    //收到BYE命令，关闭当前视频采集功能，重新回到注册之后的等待邀请界面
                VideoInfo.nalfirst = 0; //0表示未收到首包，1表示收到
                VideoInfo.index = 0;
                VideoInfo.query_response = false;
                VideoInfo.endView = false;
                G711Running = false;
                isStop = true;
                H264Sending.this.finish();
            }
            if (!isStop) {
//                软解码
//                byte[] encodeResult = NativeH264Encoder.EncodeFrame(data, time);  //进行编码，将编码结果存放进数组
//                time += 1000 / previewFrameRate;    //计算出一帧所耗费的时间，单位为毫秒
//                int encodeState = NativeH264Encoder.getLastEncodeStatus();//获取最后的编码状态，0——表示成功！！
//                if (encodeState == 0 && encodeResult.length > 0) {
//                    Log.e(TAG, "encode len:" + encodeResult.length);//打印编码结果的长度
//                    setSSRC_PAYLOAD();
//                    DivideAndSendNal(encodeResult);
//                }
//                硬解码
                byte[] encodeResult = avcEncoder.offerEncoder(data); //进行编码，将编码结果存放进数组

                if (encodeResult != null && encodeResult.length > 0) {
                    Log.e(TAG, "encode len:" + encodeResult.length);//打印编码结果的长度
                    setSSRC_PAYLOAD();
                    DivideAndSendNal(encodeResult);
                }
            }
        } else {
            VideoInfo.nalfirst = 0; //0表示未收到首包，1表示收到
            VideoInfo.index = 0;
            VideoInfo.query_response = false;
            isStop = true;
            G711Running = false;
            H264Sending.this.finish();
        }
    }

    /**
     * 设置ssrc与payload
     */
    public void setSSRC_PAYLOAD() {
        byte msg[] = new byte[20];
        long Ssrc = 0;
        msg[0] = 0x00;
        msg[1] = 0x01;
        msg[2] = 0x00;
        msg[3] = 0x10;
        try {
            System.arraycopy(VideoInfo.media_info_magic, 0, msg, 4, 16);  //生成RTP心跳保活包，即在Info.media_info_megic之前再加上0x00 0x01 0x00 0x10
        } catch (Exception e) {
            Log.d("ZR", "System.arraycopy failed!");
        }
        rtpsending.rtpSession1.payloadType(0x7a);    //设置RTP包的负载类型为0x7a

        //取Info.media_info_megic的后四组设为RTP的同步源码（Ssrc）
        Ssrc = (VideoInfo.media_info_magic[15] & 0x000000ff) | ((VideoInfo.media_info_magic[14] << 8) & 0x0000ff00) | ((VideoInfo.media_info_magic[13] << 16) & 0x00ff0000) | ((VideoInfo.media_info_magic[12] << 24) & 0xff000000);
        rtpsending.rtpSession1.setSsrc(Ssrc);
//        for (int i = 0; i < 2; i++) {
//            rtpsending.rtpSession1.sendData(msg);
//        }
    }

    /**
     * 分片、发送方法
     */
    public void DivideAndSendNal(byte[] h264) {

        if (h264.length > 0) {  //有数据才进行分片发送操作
            if (h264.length > VideoInfo.divide_length) {
                VideoInfo.dividingFrame = true;
                VideoInfo.status = true;
                VideoInfo.firstPktReceived = false;
                VideoInfo.pktflag = 0;

                while (VideoInfo.status) {
                    if (!VideoInfo.firstPktReceived) {  //首包
                        sendFirstPacket(h264);
                    } else {
                        if (h264.length - VideoInfo.pktflag > VideoInfo.divide_length) {  //中包
                            sendMiddlePacket(h264);
                        } else {   //末包
                            sendLastPacket(h264);
                        }
                    } //end of 首包
                }//end of while
            } else {   //不分片包
                sendCompletePacket(h264);
            }
        }
    }

    /**
     * 发送首包
     */
    public void sendFirstPacket(byte[] h264) {
        Log.d("H264Sending", "发送首包");
        rtppkt[0] = (byte) (h264[4] & 0xe0);
        rtppkt[0] = (byte) (rtppkt[4] + 0x1c);
        rtppkt[1] = (byte) (0x80 + (h264[4] & 0x1f));
        try {
            System.arraycopy(h264, 0, rtppkt, 2, VideoInfo.divide_length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        VideoInfo.pktflag = VideoInfo.pktflag + VideoInfo.divide_length;
        VideoInfo.firstPktReceived = true;
        //设置RTP包的负载类型为0x62
        rtpsending.rtpSession1.payloadType(0x62);
        //发送打包数据
        rtpsending.rtpSession1.sendData(rtppkt);   //发送打包数据
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送中包
     */
    public void sendMiddlePacket(byte[] h264) {
        Log.d("H264Sending", "发送中包");
        rtppkt[0] = (byte) (h264[0] & 0xe0);//获取Nalu单元的前三位
        rtppkt[0] = (byte) (rtppkt[0] + 0x1c);//加上Fu-A的type值28（0x1c）即组成FU indicator
        rtppkt[1] = (byte) (0x00 + (h264[0] & 0x1f));//中包的ser为000加上Nalu的type组成 FU header


        try {
            System.arraycopy(h264, VideoInfo.pktflag, rtppkt, 2, VideoInfo.divide_length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        VideoInfo.pktflag = VideoInfo.pktflag + VideoInfo.divide_length;
        //设置RTP包的负载类型为0x62
        rtpsending.rtpSession1.payloadType(0x62);
        //发送打包数据
        rtpsending.rtpSession1.sendData(rtppkt);   //发送打包数据   //发送打包数据
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送末包
     */
    public static void sendLastPacket(byte[] h264) {
        Log.d("H264Sending", "发送末包");
        byte[] rtppktLast = new byte[h264.length - VideoInfo.pktflag + 2];
        rtppktLast[0] = (byte) (h264[0] & 0xe0);
        rtppktLast[0] = (byte) (rtppktLast[0] + 0x1c);
        rtppktLast[1] = (byte) (0x40 + (h264[0] & 0x1f));
        try {
            System.arraycopy(h264, VideoInfo.pktflag, rtppktLast, 2, h264.length - VideoInfo.pktflag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置RTP包的负载类型为0x62
        rtpsending.rtpSession1.payloadType(0x62);
        //发送打包数据
        rtpsending.rtpSession1.sendData(rtppktLast);   //发送打包数据  //发送打包数据
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        VideoInfo.status = false;  //打包组包结束，下一步进行解码
        VideoInfo.dividingFrame = false;  //一帧分片打包完毕，时间戳改下一帧
    }

    /**
     * 发送完整包
     */
    public void sendCompletePacket(byte[] h264) {
        Log.d("H264Sending", "发送单包");
        //设置RTP包的负载类型为0x62
        rtpsending.rtpSession1.payloadType(0x62);
        //发送打包数据
        rtpsending.rtpSession1.sendData(h264);   //发送打包数据   //发送打包数据
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}