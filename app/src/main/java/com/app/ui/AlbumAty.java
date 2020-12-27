package com.app.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.app.camera.FileOperateUtil;
import com.app.camera.album.view.AlbumGridView;
import com.app.ftp.Ftp;
import com.app.ftp.FtpListener;
import com.app.model.Constant;
import com.app.sip.SipInfo;
import com.punuo.sys.app.activity.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @author LinJ
 * @ClassName: AlbumAty
 * @Description: 相册Activity
 * @date 2015-1-6 下午5:03:48
 */
public class AlbumAty extends BaseActivity implements View.OnClickListener, AlbumGridView.OnCheckedChangeListener {
    public final static String TAG = "AlbumAty";
    /**
     * 显示相册的View
     */
    private AlbumGridView mAlbumView;

    private String mSaveRoot;
    private String SdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
    private TextView mEnterView;
    private TextView mLeaveView;
    private TextView mSelectedCounterView;
    private TextView mSelectAllView;
    private Button mDeleteButton;
    private ImageView mBackView;
    private Button mCutButton;
    private Button sendPic;
    private ProgressDialog dialog;
    private int num;
    private int type;
    private Ftp mFtp;
    ArrayList<String> paths;
    private FtpListener upLoad=new FtpListener() {
        @Override
        public void onStateChange(String currentStep) {

        }

        @Override
        public void onUploadProgress(String currentStep, long uploadSize, File targetFile) {
            if (currentStep.equals(Constant.FTP_UPLOAD_SUCCESS)) {
                num++;
                if (num < paths.size()) {
                    dialog.setProgress(num);
                } else {
                    dialog.dismiss();
                }
//                                    Log.d(TAG, "-----上传成功--");
            } else if (currentStep.equals(Constant.FTP_UPLOAD_LOADING)) {
            }
        }

        @Override
        public void onDownLoadProgress(String currentStep, long downProcess, File targetFile) {

        }

        @Override
        public void onDeleteProgress(String currentStep) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album);
        type = getIntent().getIntExtra("type", 0);

        mAlbumView = (AlbumGridView) findViewById(R.id.albumview);
        mEnterView = (TextView) findViewById(R.id.header_bar_enter_selection);
        mLeaveView = (TextView) findViewById(R.id.header_bar_leave_selection);
        mSelectAllView = (TextView) findViewById(R.id.select_all);
        mSelectedCounterView = (TextView) findViewById(R.id.header_bar_select_counter);
        mDeleteButton = (Button) findViewById(R.id.delete);
        mCutButton = (Button) findViewById(R.id.move);
        mBackView = (ImageView) findViewById(R.id.header_bar_back);
        sendPic = (Button) findViewById(R.id.send_pic);
        mSelectedCounterView.setText("0");

        mEnterView.setOnClickListener(this);
        mLeaveView.setOnClickListener(this);
        mSelectAllView.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        mCutButton.setOnClickListener(this);
        mBackView.setOnClickListener(this);
        sendPic.setOnClickListener(this);
        mAlbumView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (mAlbumView.getEditable()) return;
                Intent intent = new Intent(AlbumAty.this, AlbumItemAty.class);
                Log.i("111", arg1.getTag().toString());
                intent.putExtra("path", arg1.getTag().toString());
                startActivity(intent);
            }
        });
        mAlbumView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                if (mAlbumView.getEditable()) return true;
                enterEdit();
                return true;
            }
        });
        mSaveRoot = "Camera";


    }

    /**
     * 加载图片
     *
     * @param rootPath 根目录文件夹名
     * @param format   需要加载的文件格式
     */
    public void loadAlbum(String rootPath, String format) {
        //获取根目录下缩略图文件夹
        String thumbFolder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, rootPath);
        //List<File> files = FileOperateUtil.listFiles(thumbFolder, format);
        List<File> files = FileOperateUtil.listFiles(SdCard+"/DCIM/Camera/", format);
        List<String> paths = new ArrayList<String>();
        String videoPath = null;
        String imagepath = null;
        if (files != null && files.size() > 0) {
            for (File file : files) {
                    videoPath = thumbFolder.replace(getString(R.string.Thumbnail), getString(R.string.Video))+"/"+file.getName();
                    videoPath = videoPath.replace(".jpg", ".mp4");
                    imagepath = thumbFolder.replace(getString(R.string.Thumbnail), getString(R.string.Image))+"/"+file.getName();
                switch (type) {
                    case 0:
                        //if (new File(videoPath).exists()||new File(imagepath).exists()) {
                            paths.add(file.getAbsolutePath());
                        //}
                        break;
                    case 1:
                        if (!file.getAbsolutePath().contains("video")) {
                            if (new File(imagepath).exists()) {
                                paths.add(file.getAbsolutePath());
                            }
                        }
                        break;
                }
            }
        }
        mAlbumView.setAdapter(mAlbumView.new AlbumViewAdapter(paths));
    }


    @Override
    protected void onResume() {
        loadAlbum(mSaveRoot, ".jpg");
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.header_bar_enter_selection:
                enterEdit();
                break;
            case R.id.header_bar_leave_selection:
                leaveEdit();
                break;
            case R.id.select_all:
                selectAllClick();
                break;
            case R.id.delete:
                Log.w("delete","删除了一张照片");
                showDeleteDialog();
                break;
            case R.id.header_bar_back:
                finish();
                break;
            case R.id.move:
                Set<String> items = mAlbumView.getSelectedItems();
                paths = new ArrayList<>();
                for (String item : items) {
                    if (item.contains("video")) {
                        item = item.replace(getString(R.string.Thumbnail),
                                getString(R.string.Video));
                        item = item.replace(".jpg", ".mp4");
                    } else {
                        item = item.replace(getString(R.string.Thumbnail),
                                getString(R.string.Image));
                    }
                    paths.add(item);
                }
                dialog = new ProgressDialog(AlbumAty.this);
                dialog.setTitle("上传进度");
                dialog.setMessage("已经上传了");
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCancelable(false);
                dialog.setIndeterminate(false);
                dialog.setMax(paths.size());
                dialog.show();
                num = 0;
                new Thread() {
                    @Override
                    public void run() {

                        for (final String path : paths) {
                            String ftpPath;
                            if (path.contains("video")) {
                                ftpPath = "/upload/" + SipInfo.userAccount + "/video";
                            } else {
                                ftpPath = "/upload/" + SipInfo.userAccount + "/picture";
                            }

                            try {
                                mFtp=new Ftp(SipInfo.serverIp,21,"ftpaller","123456",upLoad);
                                mFtp.uploadMultiFile(paths,ftpPath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
                break;
            case R.id.send_pic:
                Set<String> picitems = mAlbumView.getSelectedItems();
                final ArrayList<String> picpaths = new ArrayList<>();
                for (String item : picitems) {
                    picpaths.add(item);
                }
                Intent o = new Intent();
                o.putStringArrayListExtra("picpaths", picpaths);
                setResult(RESULT_OK, o);
                finish();
                break;
            default:
                break;
        }
    }

    private void enterEdit() {
        mAlbumView.setEditable(true, this);
        mSelectAllView.setText(getResources().getString(R.string.album_phoot_select_all));
        switch (type) {
            case 0:
                mDeleteButton.setEnabled(false);
                mCutButton.setEnabled(false);
                findViewById(R.id.header_bar_navi).setVisibility(View.GONE);
                findViewById(R.id.header_bar_select).setVisibility(View.VISIBLE);
                findViewById(R.id.album_bottom_bar).setVisibility(View.VISIBLE);
                break;
            case 1:
                sendPic.setEnabled(false);
                findViewById(R.id.header_bar_navi).setVisibility(View.GONE);
                findViewById(R.id.header_bar_select).setVisibility(View.VISIBLE);
                sendPic.setVisibility(View.VISIBLE);
        }
    }

    private void leaveEdit() {
        mAlbumView.setEditable(false);
        switch (type) {
            case 0:
                mCutButton.setEnabled(false);
                findViewById(R.id.header_bar_navi).setVisibility(View.VISIBLE);
                findViewById(R.id.header_bar_select).setVisibility(View.GONE);
                findViewById(R.id.album_bottom_bar).setVisibility(View.GONE);
                break;
            case 1:
                sendPic.setEnabled(false);
                findViewById(R.id.header_bar_navi).setVisibility(View.VISIBLE);
                findViewById(R.id.header_bar_select).setVisibility(View.GONE);
                sendPic.setVisibility(View.GONE);
                break;
        }
    }

    private void selectAllClick() {
        if (mSelectAllView.getText().equals(getResources().getString(R.string.album_phoot_select_all))) {
            mAlbumView.selectAll(this);
            mSelectAllView.setText(getResources().getString(R.string.album_phoot_unselect_all));
        } else {
            mAlbumView.unSelectAll(this);
            mSelectAllView.setText(getResources().getString(R.string.album_phoot_select_all));
        }

    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定要要删除?")
                .setPositiveButton("确认", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Set<String> items = mAlbumView.getSelectedItems();
                        for (String path : items) {
                            boolean flag = FileOperateUtil.deleteThumbFile(path, AlbumAty.this);
                            if (!flag) Log.i(TAG, path);
                        }
                        loadAlbum(mSaveRoot, ".jpg");
                        leaveEdit();
                    }
                })
                .setNegativeButton("取消", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }


    @Override
    public void onCheckedChanged(Set<String> set) {
        // TODO Auto-generated method stub
        mSelectedCounterView.setText(String.valueOf(set.size()));
        if (set.size() > 0) {
            mDeleteButton.setEnabled(true);
            mCutButton.setEnabled(true);
            sendPic.setEnabled(true);
        } else {
            mDeleteButton.setEnabled(false);
            mCutButton.setEnabled(false);
            sendPic.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (mAlbumView.getEditable()) {
            leaveEdit();
            return;
        }
        super.onBackPressed();
    }
}
