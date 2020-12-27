package com.app.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.camera.FileOperateUtil;
import com.app.camera.album.view.AlbumViewPager;
import com.app.camera.album.view.MatrixImageView;
import com.app.camera.video.view.VideoPlayerContainer;
import com.app.ftp.Ftp;
import com.app.ftp.FtpListener;
import com.app.model.Constant;
import com.app.sip.SipInfo;
import com.punuo.sys.app.activity.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.app.R.id.upload;


/**
 * @author LinJ
 * @ClassName: AlbumItemAty
 * @Description:相册图片大图Activity 包含图片编辑功能
 * @date 2015-1-12 下午5:18:25
 */
public class AlbumItemAty extends BaseActivity implements OnClickListener, MatrixImageView.OnSingleTapListener
        , AlbumViewPager.OnPlayVideoListener {
    public final static String TAG = "AlbumDetailAty";
    private String mSaveRoot;
    private AlbumViewPager mViewPager;//显示大图
    private VideoPlayerContainer mContainer;
    private ImageView mBackView;
    private ImageView mCameraView;
    private TextView mCountView;
    private View mHeaderBar, mBottomBar;
    private Button mDeleteButton;
    private Button mUploadButton;
    private ProgressDialog dialog;
    private File file;
    private String ftpPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albumitem);

        mViewPager = (AlbumViewPager) findViewById(R.id.albumviewpager);
        mContainer = (VideoPlayerContainer) findViewById(R.id.videoview);
        mBackView = (ImageView) findViewById(R.id.header_bar_photo_back);
        mCameraView = (ImageView) findViewById(R.id.header_bar_photo_to_camera);
        mCountView = (TextView) findViewById(R.id.header_bar_photo_count);
        mHeaderBar = findViewById(R.id.album_item_header_bar);
        mBottomBar = findViewById(R.id.album_item_bottom_bar);
        mDeleteButton = (Button) findViewById(R.id.delete);
        mUploadButton = (Button) findViewById(upload);

        mBackView.setOnClickListener(this);
        mCameraView.setOnClickListener(this);
        mCountView.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        mUploadButton.setOnClickListener(this);

        mSaveRoot = "Camera";
        mViewPager.setOnPageChangeListener(pageChangeListener);
        mViewPager.setOnSingleTapListener(this);
        mViewPager.setOnPlayVideoListener(this);
        String currentFileName = null;
        if (getIntent().getExtras() != null)
            currentFileName = getIntent().getExtras().getString("path");
        if (currentFileName != null) {
            File file = new File(currentFileName);
            currentFileName = file.getName();
            if (currentFileName.indexOf(".") > 0)
                currentFileName = currentFileName.substring(0, currentFileName.lastIndexOf("."));
        }

        loadAlbum(mSaveRoot, currentFileName);
    }
String Sdcard= Environment.getExternalStorageDirectory().getAbsolutePath();

    /**
     * 加载图片
     *
     * @param rootPath 图片根路径
     */
    public void loadAlbum(String rootPath, String fileName) {
        //获取根目录下缩略图文件夹
        String folder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_IMAGE, rootPath);
        Log.w("folder........",folder);
        String thumbnailFolder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, rootPath);
        //获取图片文件大图
        List<File> imageList = FileOperateUtil.listFiles(Sdcard+"/DCIM/Camera",".jpg");
        //获取视频文件缩略图
        List<File> videoList = FileOperateUtil.listFiles(thumbnailFolder, ".jpg", "video");
        List<File> files = new ArrayList<File>();
        //将视频文件缩略图加入图片大图列表中
        if (videoList != null && videoList.size() > 0) {
            files.addAll(videoList);
        }
        if (imageList != null && imageList.size() > 0) {
            files.addAll(imageList);
        }
        FileOperateUtil.sortList(files, false);
        if (files.size() > 0) {
            List<String> paths = new ArrayList<String>();
            int currentItem = 0;
            for (File file : files) {
                if (fileName != null && file.getName().contains(fileName))
                    currentItem = files.indexOf(file);
                paths.add(file.getAbsolutePath());
            }
            mViewPager.setAdapter(mViewPager.new ViewPagerAdapter(paths));
            mViewPager.setCurrentItem(currentItem);
            mCountView.setText((currentItem + 1) + "/" + paths.size());
            mDeleteButton.setEnabled(true);
            mUploadButton.setEnabled(true);
        } else {
            mCountView.setText("0/0");
            mDeleteButton.setEnabled(false);
            mUploadButton.setEnabled(false);
        }
    }


    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (mViewPager.getAdapter() != null) {
                String text = (position + 1) + "/" + mViewPager.getAdapter().getCount();
                mCountView.setText(text);
            } else {
                mCountView.setText("0/0");
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    public void onSingleTap() {
        if (mHeaderBar.getVisibility() == View.VISIBLE) {
            AlphaAnimation animation = new AlphaAnimation(1, 0);
            animation.setDuration(300);
            mHeaderBar.startAnimation(animation);
            mBottomBar.startAnimation(animation);
            mHeaderBar.setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
        } else {
            AlphaAnimation animation = new AlphaAnimation(0, 1);
            animation.setDuration(300);
            mHeaderBar.startAnimation(animation);
            mBottomBar.startAnimation(animation);
            mHeaderBar.setVisibility(View.VISIBLE);
            mBottomBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.header_bar_photo_back:
                finish();
                break;
            case R.id.header_bar_photo_to_camera:
				startActivity(new Intent(this,MyCamera.class));
                break;
            case R.id.delete:
                String result = mViewPager.deleteCurrentPath();
                if (result != null)
                    if (result.equals("0/0")){
                        mDeleteButton.setEnabled(false);
                        mUploadButton.setEnabled(false);
                    }
                    mCountView.setText(result);
                break;
            case R.id.upload:
                final String path=mViewPager.getFilePath();
                if (path.contains("video")){
                    ftpPath = "/upload/" + SipInfo.userAccount+ "/video/";
                }else{
                    ftpPath = "/upload/" + SipInfo.userAccount + "/picture/";
                }
                file = new File(path);
                dialog = new ProgressDialog(this);
                dialog.setTitle("上传进度");
                dialog.setMessage("已经上传了");
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCancelable(false);
                dialog.setIndeterminate(false);
                dialog.setMax(100);
                dialog.show();
                FtpListener upload=new FtpListener() {
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
                final Ftp mFtp=new Ftp(SipInfo.serverIp,21,"ftpaller","123456",upload);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            mFtp.upload(path,ftpPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mContainer.getVisibility() == View.VISIBLE)
            mContainer.stopPlay();
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        if (mContainer.getVisibility() == View.VISIBLE)
            mContainer.stopPlay();
        super.onStop();
    }

    @Override
    public void onPlay(String path) {
        // TODO Auto-generated method stub
        try {
            mContainer.playVideo(path);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
