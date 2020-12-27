package com.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.camera.FileOperateUtil;
import com.app.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Author chzjy
 * Date 2016/12/19.
 * 悬浮窗录制视频(暂未完全实现)
 */

public class FloatWindowService extends Service {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private LayoutInflater mLayoutInflater;
    private View mFloatView;
    private int mCurrentX;
    private int mCurrentY;
    private static int mFloatViewWidth = 50;
    private static int mFloatViewHeight = 80;
    private SurfaceView surfaceView;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Camera.Parameters parameters;
    private long startTime;
    private long endTime;
    // 记录是否正在进行录制
    private boolean isRecording = false;
    private String moviePath;
    // 系统的视频文件
    File videoFile;
    MediaRecorder mRecorder;
    private Chronometer time;
    private ImageView rec;
    private Button back;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //初始化WindowManager对象和LayoutInflater对象
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mLayoutInflater = LayoutInflater.from(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createView();
        return super.onStartCommand(intent, flags, startId);
    }

    private void createView() {
        // TODO Auto-generated method stub
        //加载布局文件
        mFloatView = mLayoutInflater.inflate(R.layout.floating, null);
        //surfaceView
        surfaceView = (SurfaceView) mFloatView.findViewById(R.id.surfaceView);
        time = (Chronometer) mFloatView.findViewById(R.id.time);
        rec=(ImageView)mFloatView.findViewById(R.id.rec);
        back=(Button)mFloatView.findViewById(R.id.back);
        surfaceView.getHolder().setFixedSize(1280, 720);
        surfaceView.getHolder().setKeepScreenOn(true);
        time.setVisibility(View.INVISIBLE);
        rec.setVisibility(View.INVISIBLE);
        back.setVisibility(View.GONE);
        try {
            camera = Camera.open(2);
        } catch (Exception e) {
            try {
                camera = Camera.open(0);
            }catch (Exception e1){
                Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rec.setVisibility(View.INVISIBLE);
                time.setVisibility(View.INVISIBLE);
                back.setVisibility(View.GONE);
                mLayoutParams.width = 120;
                mLayoutParams.height = 160;
                mWindowManager.updateViewLayout(mFloatView, mLayoutParams);
            }
        });
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                surfaceHolder = holder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    initCamera();
                    camera.autoFocus(new Camera.AutoFocusCallback() {
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
                closeCamera();
            }
        });
        //为View设置监听，以便处理用户的点击和拖动
        mFloatView.setOnTouchListener(new OnFloatViewTouchListener());
       /*为View设置参数*/
        mLayoutParams = new WindowManager.LayoutParams();
        //设置View默认的摆放位置
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        //设置window type
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //设置背景为透明
        mLayoutParams.format = PixelFormat.RGB_565;
        //注意该属性的设置很重要，FLAG_NOT_FOCUSABLE使浮动窗口不获取焦点,若不设置该属性，屏幕的其它位置点击无效，应为它们无法获取焦点
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //设置视图的显示位置，通过WindowManager更新视图的位置其实就是改变(x,y)的值
        mCurrentX = mLayoutParams.x = 0;
        mCurrentY = mLayoutParams.y = 0;
        //设置视图的宽、高
        mLayoutParams.width = 120;
        mLayoutParams.height = 160;
        //将视图添加到Window中
        mWindowManager.addView(mFloatView, mLayoutParams);
    }

    private void closeCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private void initCamera() {
        if (camera == null) return;
        parameters = camera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.setPreviewSize(1280,720);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        setDispaly(parameters, camera);
        camera.setParameters(parameters);
        camera.startPreview();
        camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上

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

    private void initializeRecorder() throws IllegalStateException, IOException {
        if (camera == null) return;
        camera.unlock();
        mRecorder = new MediaRecorder();
        mRecorder.setCamera(camera);
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
        mRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
        //0或负数为录制时间无限制
        mRecorder.setMaxDuration(1000);
        mRecorder.prepare();
    }

    private void stopRecording() {

        if (isRecording) {
            mRecorder.stop();
            mRecorder.reset();
            camera.lock();
            isRecording = false;
            closeCamera();
            time.stop();
            time.setBase(SystemClock.elapsedRealtime());
//            try {
//                saveThumbnail();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(FloatWindowService.this, "录像已经保存至我的相册,如需查看请到我的相册查看", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
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
        int pWidth = surfaceView.getWidth();// 容器宽度
        int pHeight = surfaceView.getHeight();//容器高度
        //获取宽高跟容器宽高相比较小的倍数，以此为标准进行缩放
        float scale = Math.min((float) width / pWidth, (float) height / pHeight);
        int w = Math.round(scale * pWidth);
        int h = Math.round(scale * pHeight);
        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        return bitmap;
    }
    /*由于直接startService(),因此该方法没用*/
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*该方法用来更新视图的位置，其实就是改变(LayoutParams.x,LayoutParams.y)的值*/
    private void updateFloatView() {
        mLayoutParams.x = mCurrentX;
        mLayoutParams.y = mCurrentY;
        mWindowManager.updateViewLayout(mFloatView, mLayoutParams);
    }

    /*处理视图的拖动，这里只对Move事件做了处理，用户也可以对点击事件做处理，例如：点击浮动窗口时，启动应用的主Activity*/
    private class OnFloatViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // TODO Auto-generated method stub
            Log.i("FloatWindowService", "mCurrentX: " + mCurrentX + ",mCurrentY: "
                    + mCurrentY + ",mFloatViewWidth: " + mFloatViewWidth
                    + ",mFloatViewHeight: " + mFloatViewHeight);
           /*
            * getRawX(),getRawY()这两个方法很重要。通常情况下，我们使用的是getX(),getY()来获得事件的触发点坐标，
            * 但getX(),getY()获得的是事件触发点相对与视图左上角的坐标；而getRawX(),getRawY()获得的是事件触发点
            * 相对与屏幕左上角的坐标。由于LayoutParams中的x,y是相对与屏幕的，所以需要使用getRawX(),getRawY()。
            */
            mCurrentX = (int) event.getRawX() - mFloatViewWidth;
            mCurrentY = (int) event.getRawY() - mFloatViewHeight;
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    startTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateFloatView();
                    break;
                case MotionEvent.ACTION_UP:

                    endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < 0.25 * 1000L) {
                        rec.setVisibility(View.VISIBLE);
                        time.setVisibility(View.VISIBLE);
                        back.setVisibility(View.VISIBLE);
                        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                        mWindowManager.updateViewLayout(mFloatView, mLayoutParams);
                    }
                    break;
            }
            return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
        mWindowManager.removeViewImmediate(mFloatView);
    }
}
