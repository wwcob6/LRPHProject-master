package com.app.ui;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Chronometer;
import android.widget.Toast;

import com.app.R;
import com.app.camera.FileOperateUtil;
import com.app.sip.SipInfo;
import com.punuo.sys.app.activity.BaseActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class MovieRecord extends BaseActivity implements SurfaceHolder.Callback {

    // 显示视频预览的SurfaceView
    @Bind(R.id.surface_video)
    SurfaceView surfaceVideo;

    // 系统的视频文件
    File videoFile;
    MediaRecorder mRecorder;
    @Bind(R.id.time)
    Chronometer time;
    // 记录是否正在进行录制
    private boolean isRecording = false;
    private String moviePath;
    private Camera mCamera;
    private Camera.Parameters parameters;
    private Handler handler = new Handler();
    private SurfaceHolder surfaceHolder;
    private String TAG = "MovieRecord";
    private int numCamera;
    private int cameraState=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SipInfo.movieRecord = this;
        setContentView(R.layout.activity_movierecord);
        ButterKnife.bind(this);
        // 设置分辨率
        surfaceVideo.getHolder().setFixedSize(1920, 1080);
        //让屏幕保持常亮
        surfaceVideo.getHolder().setKeepScreenOn(true);
        //添加回到函数
        surfaceVideo.getHolder().addCallback(this);
        SipInfo.flag=false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged: ");
        numCamera = Camera.getNumberOfCameras();
        Log.i(TAG, "摄像头个数为" + numCamera);
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraId_back = -1;
        int cameraId_out = -1;
        for (int i = 0; i < numCamera; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId_back = i;     //获取后置摄像头的Id
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                int cameraId_front = i;    //获取前置摄像头的Id
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
            cameraState=cameraId_out;
        } catch (Exception e) {
            try {
                mCamera = Camera.open(cameraId_back);
                cameraState=cameraId_back;
            }catch (Exception e1){
                Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        try {
            if (mCamera == null) return;
            mCamera.setPreviewDisplay(surfaceHolder);
            initCamera();
            // 自动对焦
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        initCamera();
                        camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
                    }
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        startRecording();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void initCamera() {
        if (mCamera == null) return;
        parameters = mCamera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        setDispaly(parameters,mCamera);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
        mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上

    }

    private void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void initializeRecorder() throws IllegalStateException, IOException {
        if (mCamera == null) return;
        mCamera.unlock();
        mRecorder = new MediaRecorder();
        mRecorder.setCamera(mCamera);
        mRecorder.setOrientationHint(90);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        String path = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_VIDEO, "Camera");
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        moviePath = path + File.separator + "video" + FileOperateUtil.createFileNmae(".mp4");
        videoFile = new File(moviePath);
        if (videoFile.exists()) {
            videoFile.delete();
        }
        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        mRecorder.setProfile(cpHigh);
        mRecorder.setOutputFile(videoFile.getAbsolutePath());
        mRecorder.setPreviewDisplay(surfaceVideo.getHolder().getSurface());
        //0或负数为录制时间无限制
        mRecorder.setMaxDuration(0);
        mRecorder.prepare();
    }

    private void stopRecording() {

        if (isRecording) {
            mRecorder.stop();
            mRecorder.reset();
            mCamera.lock();
            isRecording = false;
            closeCamera();
            time.stop();
            time.setBase(SystemClock.elapsedRealtime());
            try {
                saveThumbnail();
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MovieRecord.this, "录像已经保存至我的相册,如需查看请到我的相册查看", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void startRecording() {
        if (!isRecording) {
            try {
                initializeRecorder();
                isRecording = true;
                mRecorder.start();
                time.setBase(SystemClock.elapsedRealtime());
                time.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //控制图像的正确显示方向
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }

    }

    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", int.class);
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, i);
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        SipInfo.flag=true;
    }

    @Override
    public void onBackPressed() {
        stopRecording();
        finish();
    }

    private Bitmap saveThumbnail() throws FileNotFoundException, IOException {
        if (moviePath != null) {
            //创建缩略图,该方法只能获取384X512的缩略图，舍弃，使用源码中的获取缩略图方法
            //			Bitmap bitmap=ThumbnailUtils.createVideoThumbnail(mRecordPath, Thumbnails.MINI_KIND);
            Bitmap bitmap = getVideoThumbnail(moviePath);

            if (bitmap != null) {
                String mThumbnailFolder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, "Camera");
                File folder = new File(mThumbnailFolder);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File file = new File(moviePath);
                file = new File(folder + File.separator + file.getName().replace(".mp4", ".jpg"));
                //存图片小图
                BufferedOutputStream bufferos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferos);
                bufferos.flush();
                bufferos.close();
                return bitmap;
            }
        }
        return null;
    }

    /**
     * 获取帧缩略图，根据容器的高宽进行缩放
     *
     * @param filePath
     * @return
     */
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        if (bitmap == null)
            return null;
        // Scale down the bitmap if it's too large.
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pWidth = surfaceVideo.getWidth();// 容器宽度
        int pHeight = surfaceVideo.getHeight();//容器高度
        //获取宽高跟容器宽高相比较小的倍数，以此为标准进行缩放
        float scale = Math.min((float) width / pWidth, (float) height / pHeight);
        int w = Math.round(scale * pWidth);
        int h = Math.round(scale * pHeight);
        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        return bitmap;
    }


}
