package com.app.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.R;
import com.app.camera.FileOperateUtil;
import com.app.camera.album.view.FilterImageView;
import com.app.camera.camera.view.CameraContainer;
import com.app.camera.camera.view.CameraView;
import com.app.ftp.Ftp;
import com.app.ftp.FtpListener;
import com.app.model.Constant;
import com.app.sip.SipInfo;
import com.punuo.sys.app.activity.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by acer on 2016/11/14.
 */

public class MyCamera extends BaseActivity implements View.OnClickListener, CameraContainer.TakePictureListener
        , SensorEventListener {
    @Bind(R.id.btn_thumbnail)
    FilterImageView btnThumbnail;
    @Bind(R.id.videoicon)
    ImageView videoicon;
    @Bind(R.id.btn_shutter_record)
    ImageButton btnShutterRecord;
    @Bind(R.id.btn_shutter_camera)
    ImageButton btnShutterCamera;
    @Bind(R.id.btn_switch_mode)
    ImageButton btnSwitchMode;
    @Bind(R.id.btn_switch_camera)
    ImageView btnSwitchCamera;
    @Bind(R.id.btn_flash_mode)
    ImageView btnFlashMode;
    @Bind(R.id.btn_other_setting)
    ImageView btnOtherSetting;
    @Bind(R.id.camera_header_bar)
    LinearLayout cameraHeaderBar;
    @Bind(R.id.cameracontainer)
    CameraContainer cameracontainer;
    @Bind(R.id.camera_bottom_bar)
    LinearLayout cameraBottomBar;
    @Bind(R.id.pic)
    ImageView pic;
    @Bind(R.id.cancel)
    Button cancel;
    @Bind(R.id.upload)
    Button upload;
    @Bind(R.id.upload_layout)
    LinearLayout uploadLayout;
    @Bind(R.id.icon_play)
    ImageView iconPlay;
    @Bind(R.id.videoviewlayout)
    RelativeLayout videoviewlayout;
    @Bind(R.id.reset)
    Button reset;
    @Bind(R.id.use)
    Button use;
    @Bind(R.id.chose_layout)
    LinearLayout choseLayout;
    private boolean mIsRecordMode = false;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Calendar mCalendar;
    private int mX, mY, mZ;
    private long lastStaticStamp = 0;
    boolean isFocusing = false;
    boolean canFocusIn = false;  //内部是否能够对焦控制机制
    boolean isTake = false;

    public static final int DELEY_DURATION = 500;

    public static final int STATUS_NONE = 0;
    public static final int STATUS_STATIC = 1;
    public static final int STATUS_MOVE = 2;
    private int STATUE = STATUS_NONE;
    private String rootPath = "Camera";
    private Date date;
    private ProgressDialog dialog;
    private boolean isRecording = false;
    private boolean mIsVideo = false;
    private String ftpPath;
    private File file;
    long exitTime;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SipInfo.myCamera=this;
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.mycamera);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        if (type == 1) {
            btnThumbnail.setVisibility(View.GONE);
            btnSwitchMode.setVisibility(View.GONE);
            videoicon.setVisibility(View.GONE);
        }
        mSensorManager = (SensorManager) this.getSystemService(Activity.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        btnThumbnail.setOnClickListener(this);
        btnShutterCamera.setOnClickListener(this);
        btnShutterRecord.setOnClickListener(this);
        btnFlashMode.setOnClickListener(this);
        btnSwitchMode.setOnClickListener(this);
        btnSwitchCamera.setOnClickListener(this);
        btnOtherSetting.setOnClickListener(this);
        iconPlay.setOnClickListener(this);
        cancel.setOnClickListener(this);
        upload.setOnClickListener(this);
        reset.setOnClickListener(this);
        use.setOnClickListener(this);
        cameracontainer.setRootPath(rootPath);
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        restParams();
        initThumbnail();

        SipInfo.flag=false;
    }

    /**
     * 加载缩略图
     */
    private void initThumbnail() {
        String thumbFolder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, rootPath);
        List<File> files = FileOperateUtil.listFiles(thumbFolder, ".jpg");
        if (files != null && files.size() > 0) {
            Bitmap thumbBitmap = BitmapFactory.decodeFile(files.get(0).getAbsolutePath());
            if (thumbBitmap != null) {
                btnThumbnail.setImageBitmap(thumbBitmap);
                //视频缩略图显示播放图案
                if (files.get(0).getAbsolutePath().contains("video")) {
                    videoicon.setVisibility(View.VISIBLE);
                } else {
                    videoicon.setVisibility(View.GONE);
                }
            }
        } else {
            btnThumbnail.setImageBitmap(null);
            videoicon.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initThumbnail();
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mSensor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this, mSensor);
        SipInfo.flag=true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_shutter_camera:
                if (!isTake) {
                    btnSwitchCamera.setClickable(false);
                    cameracontainer.takePicture(this, type);
                    isTake = true;
                }
                break;
            case R.id.btn_flash_mode:
                if (cameracontainer.getFlashMode() == CameraView.FlashMode.ON) {
                    cameracontainer.setFlashMode(CameraView.FlashMode.OFF);
                    btnFlashMode.setImageResource(R.drawable.btn_flash_off);
                } else if (cameracontainer.getFlashMode() == CameraView.FlashMode.OFF) {
                    cameracontainer.setFlashMode(CameraView.FlashMode.AUTO);
                    btnFlashMode.setImageResource(R.drawable.btn_flash_auto);
                } else if (cameracontainer.getFlashMode() == CameraView.FlashMode.AUTO) {
                    cameracontainer.setFlashMode(CameraView.FlashMode.TORCH);
                    btnFlashMode.setImageResource(R.drawable.btn_flash_torch);
                } else if (cameracontainer.getFlashMode() == CameraView.FlashMode.TORCH) {
                    cameracontainer.setFlashMode(CameraView.FlashMode.ON);
                    btnFlashMode.setImageResource(R.drawable.btn_flash_on);
                }
                break;
            case R.id.btn_switch_camera:
                cameracontainer.switchCamera();
                break;
            case R.id.btn_switch_mode:
                if (mIsRecordMode) {
                    if (isRecording) {
                        if (System.currentTimeMillis() - exitTime > 2000) {
                            btnSwitchMode.setImageResource(R.drawable.ic_switch_camera);
                            btnShutterCamera.setVisibility(View.VISIBLE);
                            btnShutterRecord.setVisibility(View.GONE);
                            //拍照模式下显示顶部菜单
                            cameraHeaderBar.setVisibility(View.VISIBLE);
                            mIsRecordMode = false;
                            cameracontainer.switchMode(0);
                            stopRecord();
                            exitTime = 0;
                        } else {
                            Toast.makeText(this, "请勿操作太快!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        btnSwitchMode.setImageResource(R.drawable.ic_switch_camera);
                        btnShutterCamera.setVisibility(View.VISIBLE);
                        btnShutterRecord.setVisibility(View.GONE);
                        //拍照模式下显示顶部菜单
                        cameraHeaderBar.setVisibility(View.VISIBLE);
                        mIsRecordMode = false;
                        cameracontainer.switchMode(0);
                    }
                } else {
                    btnSwitchMode.setImageResource(R.drawable.ic_switch_video);
                    btnShutterCamera.setVisibility(View.GONE);
                    btnShutterRecord.setVisibility(View.VISIBLE);
                    //录像模式下隐藏顶部菜单
                    cameraHeaderBar.setVisibility(View.GONE);
                    mIsRecordMode = true;
                    cameracontainer.switchMode(0);
                }
                break;
            case R.id.btn_shutter_record:
                if (!isRecording) {
                    isRecording = cameracontainer.startRecord();
                    exitTime = System.currentTimeMillis();
                    if (isRecording) {
                        btnShutterRecord.setBackgroundResource(R.drawable.btn_shutter_recording);
                    }
                } else {
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        stopRecord();
                        exitTime = 0;
                    } else {
                        Toast.makeText(this, "请勿操作太快!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.cancel:
                cameracontainer.setVisibility(View.VISIBLE);
                cameraBottomBar.setVisibility(View.VISIBLE);
                cameraHeaderBar.setVisibility(View.VISIBLE);
                pic.setVisibility(View.GONE);
                videoviewlayout.setVisibility(View.GONE);
                uploadLayout.setVisibility(View.GONE);
                break;
            case R.id.upload:
                if (mIsVideo) {
                    ftpPath = "/upload/" + SipInfo.userAccount + "/video/";
                    file = new File(cameracontainer.mCameraView.getmRecordPath());
                } else {
                    ftpPath = "/upload/" + SipInfo.userAccount + "/picture/";
                    file = new File(cameracontainer.getImagePath());
                }
                dialog = new ProgressDialog(MyCamera.this);
                dialog.setTitle("上传进度");
                dialog.setMessage("已经上传了");
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCancelable(false);
                dialog.setIndeterminate(false);
                dialog.setMax(100);
                dialog.show();

                FtpListener uploadListener = new FtpListener() {
                    @Override
                    public void onStateChange(String currentStep) {

                    }

                    @Override
                    public void onUploadProgress(String currentStep, long uploadSize, File targetFile) {
                        if (currentStep.equals(Constant.FTP_UPLOAD_SUCCESS)) {
//                                    Log.d(TAG, "-----上传成功--");
                            dialog.setProgress(100);
                            dialog.dismiss();
                        } else if (currentStep.equals(Constant.FTP_UPLOAD_LOADING)) {
                            long fize = file.length();
                            float num = (float) uploadSize / (float) fize;
                            int result = (int) (num * 100);
                            dialog.setProgress(result);
                        }
                    }

                    @Override
                    public void onDownLoadProgress(String currentStep, long downProcess, File targetFile) {

                    }

                    @Override
                    public void onDeleteProgress(String currentStep) {

                    }
                };
                final Ftp mFtp = new Ftp(SipInfo.serverIp, 21, "ftpaller", "123456", uploadListener);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            mFtp.upload(file.getAbsolutePath(), ftpPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.reset:
                cameracontainer.setVisibility(View.VISIBLE);
                cameraBottomBar.setVisibility(View.VISIBLE);
                cameraHeaderBar.setVisibility(View.VISIBLE);
                pic.setVisibility(View.GONE);
                videoviewlayout.setVisibility(View.GONE);
                choseLayout.setVisibility(View.GONE);
                break;
            case R.id.use:
                Intent intent = new Intent();
                intent.putExtra("picpath", cameracontainer.getImagePath());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.icon_play:
                Intent i = new Intent(MyCamera.this, VideoLook.class);
                i.putExtra("Path", cameracontainer.mCameraView.getmRecordPath());
                startActivity(i);
                break;
            case R.id.btn_thumbnail:
                startActivity(new Intent(this, AlbumAty.class));
                finish();
                break;
        }
    }

    @Override
    public void onTakePictureEnd(Bitmap bm) {
        btnShutterCamera.setClickable(true);
    }


    @Override
    public void onAnimtionEnd(Bitmap bm, boolean isVideo) {
        //拍完照片
        isTake = false;
        cameracontainer.setVisibility(View.GONE);
        cameraBottomBar.setVisibility(View.GONE);
        cameraHeaderBar.setVisibility(View.GONE);
        pic.setVisibility(View.VISIBLE);
        pic.setImageBitmap(bm);
        if (type != 1) {
            uploadLayout.setVisibility(View.VISIBLE);
            mIsVideo = isVideo;
            if (isVideo) {
                videoviewlayout.setVisibility(View.VISIBLE);
                videoicon.setVisibility(View.VISIBLE);
            } else {
                videoviewlayout.setVisibility(View.GONE);
                videoicon.setVisibility(View.GONE);
            }
            if (bm != null) {
                //生成缩略图
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bm, 213, 213);
                btnThumbnail.setImageBitmap(thumbnail);
            }
        } else {
            choseLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            Log.i("111", "sensor is null");
            return;
        }
        if (isFocusing) {
            restParams();
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
            mCalendar = Calendar.getInstance();
            long stamp = mCalendar.getTimeInMillis();// 1393844912
            int second = mCalendar.get(Calendar.SECOND);// 53
            if (STATUE != STATUS_NONE) {
                int px = Math.abs(mX - x);
                int py = Math.abs(mY - y);
                int pz = Math.abs(mZ - z);
                double value = Math.sqrt(px * px + py * py + pz * pz);
                if (value > 1.4) {
//                    textviewF.setText("检测手机在移动..");
//                    Log.i(TAG,"mobile moving");
                    STATUE = STATUS_MOVE;
                } else {
//                    textviewF.setText("检测手机静止..");
//                    Log.i(TAG,"mobile static");
                    //上一次状态是move，记录静态时间点
                    if (STATUE == STATUS_MOVE) {
                        lastStaticStamp = stamp;
                        canFocusIn = true;
                    }

                    if (canFocusIn) {
                        if (stamp - lastStaticStamp > DELEY_DURATION) {
                            //移动后静止一段时间，可以发生对焦行为
                            if (!isFocusing) {
                                canFocusIn = false;
                                cameracontainer.mCameraView.onFocus(new Point(cameracontainer.getWidth() / 2,
                                        cameracontainer.getHeight() / 2), cameracontainer.autoFocusCallback);
                            }
                        }
                    }
                    STATUE = STATUS_STATIC;
                }
            } else {
                lastStaticStamp = stamp;
                STATUE = STATUS_STATIC;
            }

            mX = x;
            mY = y;
            mZ = z;
        }
    }

    private void restParams() {
        STATUE = STATUS_NONE;
        canFocusIn = false;
        mX = 0;
        mY = 0;
        mZ = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void stopRecord() {
        cameracontainer.stopRecord(this);
        isRecording = false;
        btnShutterRecord.setBackgroundResource(R.drawable.btn_shutter_record);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_CAMERA:
                return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_CAMERA:
                if (!isTake) {
                    btnSwitchCamera.setClickable(false);
                    cameracontainer.takePicture(this, type);
                    isTake = true;
                }
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
