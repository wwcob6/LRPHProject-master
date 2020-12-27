package com.app.ui;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.friendcircle.AlbumHelper;
import com.app.friendcircle.ImageGridAdapter;
import com.app.friendcircle.ImageItem;
import com.app.ftp.Ftp;
import com.app.ftp.FtpListener;
import com.app.model.Constant;
import com.app.sip.SipInfo;
import com.punuo.sys.app.activity.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 林逸磊 on 2018/5/2.
 */

public class ImageGridUploadPictureActivity extends BaseActivity {
    public static final String EXTRA_IMAGE_LIST = "imagelist";

    // ArrayList<Entity> dataList;//用来装载数据源的列表
    private Ftp mFtp;
    private ProgressDialog dialog;
    private int num;
    private List<ImageItem> dataList;
    private GridView gridView;
    private ImageGridAdapter adapter;// 自定义的适配�?
    private AlbumHelper helper;
    private Button bt;
    TextView t1;
    private ArrayList<String> list;
    public static String serverIp = "101.69.255.132";
    private FtpListener upLoad = new FtpListener() {
        @Override
        public void onStateChange(String currentStep) {

        }

        @Override
        public void onUploadProgress(String currentStep, long uploadSize, File targetFile) {
            if (currentStep.equals(Constant.FTP_UPLOAD_SUCCESS)) {
                num++;
                if (num < list.size()) {
                    dialog.setProgress(num);
                } else {
                    dialog.dismiss();
                    Looper.prepare();
                    Toast.makeText(ImageGridUploadPictureActivity.this, "上传成功!", Toast.LENGTH_LONG).show();
                    Looper.loop();
                    adapter.mList.clear();
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
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(ImageGridUploadPictureActivity.this, "最多选择九张图片", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("sasa", "sasaa");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagegriduploadpicture);

        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        dataList = (List<ImageItem>) getIntent().getSerializableExtra(
                EXTRA_IMAGE_LIST);

        initView();
        t1 = (TextView) findViewById(R.id.t1);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


//                if (Bimp.act_bool) {
//                    Intent intent = new Intent(ImageGridUploadPictureActivity.this,
//                            PublishedActivity.class);
//                    startActivity(intent);
//                    Bimp.act_bool = false;
//                }
//                for (int i = 0; i < list.size(); i++) {
//                    if (Bimp.drr.size() < 9) {
//                        Bimp.drr.add(list.get(i));
//                    }
//                }
                dialog = new ProgressDialog(ImageGridUploadPictureActivity.this);
                dialog.setTitle("上传进度");
                dialog.setMessage("已经上传了");
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCancelable(false);
                dialog.setIndeterminate(false);
                dialog.setMax(adapter.mList.size());
                dialog.show();
                num = 0;
                new Thread() {
                    @Override
                    public void run() {
                        try {
//                            mFtp=new Ftp(SipInfo.serverIp,21,"ftpaller","123456",upLoad);
                            mFtp = new Ftp(serverIp, 21, "ftpall", "123456", upLoad);
                            mFtp.uploadMultiFile(adapter.mList, "/" + SipInfo.paddevId + "pad/camera");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }

        });
    }

    /**
     * 初妾化view视图
     */
    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ImageGridAdapter(ImageGridUploadPictureActivity.this, dataList,
                mHandler);
        gridView.setAdapter(adapter);
        adapter.setTextCallback(new ImageGridAdapter.TextCallback() {
            public void onListen(int count) {
                bt.setText("上传" + "(" + count + ")");
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /**
                 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状�
                 * ?�? 来判断是否显示�?中效果�? 至于选中效果的��则，下面适配器的代码中会有说�?
                 */
                // if(dataList.get(position).isSelected()){
                // dataList.get(position).setSelected(false);
                // }else{
                // dataList.get(position).setSelected(true);
                // }
                /**
                 * 通知适配器，绑定的数据发生了改变，应当刷新覹�?
                 */
                adapter.notifyDataSetChanged();
            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
