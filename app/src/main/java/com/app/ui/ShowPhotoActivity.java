package com.app.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.app.R;
import com.app.db.DatabaseInfo;
import com.app.ftp.Ftp;
import com.app.ftp.FtpListener;
import com.app.model.Constant;
import com.app.sip.SipInfo;
import com.punuo.sys.app.activity.BaseActivity;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by acer on 2016/11/11.
 */
public class ShowPhotoActivity extends BaseActivity {
    @Bind(R.id.photo)
    ImageView photo;
    private String mPhotoPath;
    private int type;
    private String ftpPath;
    private String localPath;
    private String msgid;
    private Handler handler=new Handler();
    private Ftp mFtp;

    private Bitmap currentBitmap=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showphoto);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        mPhotoPath=intent.getStringExtra("path");
        type=intent.getIntExtra("type",0);
        switch (type){
            case 0:
                if (new File(mPhotoPath).exists()) {
                    currentBitmap=BitmapFactory.decodeFile(mPhotoPath);
                    photo.setImageBitmap(currentBitmap);
                }else{
                    photo.setImageDrawable(getDrawable(R.drawable.ic_error));

                }
                break;
            case 1:
                final File file=new File(mPhotoPath);
                if (file.exists()) {
                    ftpPath = intent.getStringExtra("ftpPath");
                    ftpPath = ftpPath.replace("/Thumbnail/", "/");
                    Log.d("111", ftpPath);
                    msgid = intent.getStringExtra("msgid");
                    localPath = SipInfo.localSdCard+"Files/Camera/Image/";
                    final String localphotoPath = localPath + file.getName();
                    FtpListener download=new FtpListener() {
                        @Override
                        public void onStateChange(String currentStep) {

                        }

                        @Override
                        public void onUploadProgress(String currentStep, long uploadSize, File targetFile) {

                        }

                        @Override
                        public void onDownLoadProgress(String currentStep, long downProcess, File targetFile) {
                            if (currentStep.equals(Constant.FTP_DOWN_SUCCESS)) {
                                DatabaseInfo.sqLiteManager.updateFileDownload(msgid, 1);
                                DatabaseInfo.sqLiteManager.updateLocalPath(msgid, localphotoPath);
                            }
                        }

                        @Override
                        public void onDeleteProgress(String currentStep) {

                        }
                    };
                    mFtp=new Ftp(SipInfo.serverIp,21,"ftpaller","123456",download);
                    if (!new File(localphotoPath).exists()) {
                        currentBitmap=BitmapFactory.decodeFile(mPhotoPath);
                        photo.setImageBitmap(currentBitmap);
                        showLoadingDialog();
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    mFtp.download(ftpPath,localPath);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    dismissLoadingDialog();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            currentBitmap=BitmapFactory.decodeFile(localphotoPath);
                                            photo.setImageBitmap(currentBitmap);
                                        }
                                    });

                                }
                            }
                        }.start();
                    } else {
                        photo.setImageBitmap(BitmapFactory.decodeFile(localphotoPath));
                    }
                }else{
                    photo.setImageDrawable(getDrawable(R.drawable.ic_error));
                }
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentBitmap!=null) {
            currentBitmap.recycle();
            currentBitmap=null;
        }
        System.gc();
    }
}
