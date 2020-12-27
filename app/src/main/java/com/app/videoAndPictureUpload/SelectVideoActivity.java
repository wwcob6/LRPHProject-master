package com.app.videoAndPictureUpload;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.ftp.Ftp;
import com.app.ftp.FtpListener;
import com.app.model.Constant;
import com.app.sip.SipInfo;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.punuo.sys.app.activity.BaseActivity;
import com.punuo.sys.app.util.CommonUtil;
import com.punuo.sys.app.util.StatusBarUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.widget.Toast.makeText;

public class SelectVideoActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private Ftp mFtp;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private Map<String, List<Video>> AllList;
    private RelativeLayout actionbar;
    private ImageView img_album_arrow;
    private TextView select_video;
    private int selectTotal = 0;
    private ProgressDialog dialog;
    private ArrayList<String> list;
    private int num;
    Map<String, String> map = new HashMap<String, String>();
    private FtpListener upLoad=new FtpListener() {
        @Override
        public void onStateChange(String currentStep) {

        }

        @Override
        public void onUploadProgress(String currentStep, long uploadSize, File targetFile) {
            if (currentStep.equals(Constant.FTP_UPLOAD_SUCCESS)) {
                num++;
                if (num <list.size()) {
                    dialog.setProgress(num);
                } else {
                    dialog.dismiss();
                    Looper.prepare();
                    Toast.makeText(SelectVideoActivity.this,"上传成功!",Toast.LENGTH_LONG).show();
                    Looper.loop();
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
        setContentView(R.layout.activity_select_video);
        initView();
        initData();
    }

    protected void initView() {
        actionbar = (RelativeLayout) findViewById(R.id.actionbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.Gray, R.color.Gray, R.color.Gray, R.color.Gray);
        startRefreshing(swipeRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        findViewById(R.id.title_back).setOnClickListener(this);
        findViewById(R.id.title_send).setOnClickListener(this);
        findViewById(R.id.select_video).setOnClickListener(this);

        img_album_arrow = (ImageView) findViewById(R.id.img_album_arrow);
        select_video = (TextView) findViewById(R.id.select_video);
    }

    protected void initData() {
        new initVideosThread().start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:

                finish();
                break;
            case R.id.select_video:
                if (bottomListDialog != null) {
                    bottomListDialog.show();
                    img_album_arrow.setSelected(true);
                }
                break;
            case R.id.title_send:
                list=new ArrayList<String>();
                Collection<String> c = map.values();
                Iterator<String> it = c.iterator();
                for (; it.hasNext();) {
                    list.add(it.next());
                }
                dialog = new ProgressDialog(SelectVideoActivity.this);
                dialog.setTitle("上传进度");
                dialog.setMessage("已经上传了");
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCancelable(false);
                dialog.setIndeterminate(false);
                dialog.setMax(list.size());
                dialog.show();
                num = 0;
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            mFtp=new Ftp(SipInfo.serverIp,21,"ftpaller","123456",upLoad);
                            mFtp.uploadMultiFile(list,"/"+ SipInfo.paddevId+"pad/video/");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
        }
    }

    private BottomListDialog bottomListDialog;
    private Adapter adapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConstant.WHAT.NINE:
                    Toast.makeText(SelectVideoActivity.this, "最多选择九张图片", Toast.LENGTH_LONG).show();
                    break;
                case AppConstant.WHAT.SUCCESS:
                    stopRefreshing(swipeRefreshLayout);
                    adapter = new Adapter(R.layout.adapter_select_video_item, (List<Video>) msg.obj);
                    mRecyclerView.setAdapter(adapter);
                    mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
                        @Override
                        public void SimpleOnItemClick(final BaseQuickAdapter adapter, View view, final int position) {
                            makeText(SelectVideoActivity.this, "video_path====" + ((List<Video>) adapter.getData()).get(position).getPath(), Toast.LENGTH_LONG);
                        }
                    });

                    final BottomListDialogAdapter bottomListDialogAdapter = new BottomListDialogAdapter(SelectVideoActivity.this, AllList);

                    bottomListDialog = new BottomListDialog.Builder(SelectVideoActivity.this
                            , bottomListDialogAdapter,
                            CommonUtil.getHeight() - actionbar.getHeight() - StatusBarUtil.getStatusBarHeight(SelectVideoActivity.this)
                    ).setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                        @Override
                        public void onClick(Dialog dialog, int which) {
                            dialog.dismiss();
                            String album = (String) bottomListDialogAdapter.getAllList().keySet().toArray()[which];
                            adapter.setNewData(bottomListDialogAdapter.getAllList().get(album));
                            select_video.setText(album);
                            img_album_arrow.setSelected(false);
                        }
                    }).create();
                    break;

                case AppConstant.WHAT.FAILURE:
                    stopRefreshing(swipeRefreshLayout);
                    break;
            }
        }
    };


    class initVideosThread extends Thread {
        @Override
        public void run() {
            super.run();
            AbstructProvider provider = new VideoProvider(SelectVideoActivity.this);
            List<Video> list = (List<Video>) provider.getList();

            List<Video> templist = new ArrayList<>();
            AllList = new HashMap<>();

            //我需要可以查看所有视频 所以加了这样一个文件夹名称
            AllList.put(" " + "所有视频", list);

            //主要是读取文件夹的名称 做分文件夹的展示

            for (Video video : list) {
                String album = video.getAlbum();
                if (TextUtils.isEmpty(album)) {
                    album = "Camera";
                }

                if (AllList.containsKey(album)) {
                    AllList.get(album).add(video);
                } else {
                    templist = new ArrayList<>();
                    templist.add(video);
                    AllList.put(album, templist);
                }
            }

            //在子线程读取好数据后使用handler 更新
            if (list == null || list.size() == 0) {
                Message message = new Message();
                message.what = AppConstant.WHAT.FAILURE;
                mHandler.sendMessage(message);
            } else {
                Message message = new Message();
                message.what = AppConstant.WHAT.SUCCESS;
                message.obj = list;
                mHandler.sendMessage(message);
            }
        }
    }

    @Override
    public void onRefresh() {
        initData();
    }

    protected void startRefreshing(final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    protected void stopRefreshing(final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    class Adapter extends BaseQuickAdapter<Video> {


        public Adapter(int layoutResId, List<Video> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, final Video item) {
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(item.getDuration()),
                    TimeUnit.MILLISECONDS.toMinutes(item.getDuration()) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(item.getDuration())),
                    TimeUnit.MILLISECONDS.toSeconds(item.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(item.getDuration())));

            helper.setText(R.id.text_duration, hms);
            ImageView simpleDraweeView = AdapterUtils.getAdapterView(helper.getConvertView(), R.id.simpleDraweeView);
            int width = (CommonUtil.getWidth() - 4) / 4;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width);
            simpleDraweeView.setLayoutParams(layoutParams);
            final ImageView selected = AdapterUtils.getAdapterView(helper.getConvertView(), R.id.isselected);
            final TextView textView = AdapterUtils.getAdapterView(helper.getConvertView(), R.id.item_image_grid_text);
            if (item.isSelected) {
                selected.setImageResource(R.drawable.icon_data_select);
                textView.setBackgroundResource(R.drawable.bgd_relatly_line);
            } else {
                selected.setImageResource(android.R.color.transparent);
                textView.setBackgroundColor(0x00000000);
            }
            Glide.with(SelectVideoActivity.this)
                    .asBitmap()
                    .load(Uri.fromFile(new File(item.getPath())))
                    .into(simpleDraweeView);
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String path = item.getPath();

                    if ( selectTotal < 9) {
                        item.isSelected = !item.isSelected;
                        if (item.isSelected) {
                            selected.setImageResource(R.drawable.icon_data_select);
                            textView.setBackgroundResource(R.drawable.bgd_relatly_line);
                            selectTotal++;
//                            if (textcallback != null)
//                                textcallback.onListen(selectTotal);
                            map.put(path, path);

                        } else if (!item.isSelected) {
                            selected.setImageResource(android.R.color.transparent);
                            textView.setBackgroundColor(0x00000000);
                            selectTotal--;
//                            if (textcallback != null)
//                                textcallback.onListen(selectTotal);
                            map.remove(path);
                        }
                    } else if ( selectTotal >= 9) {
                        if (item.isSelected == true) {
                            item.isSelected = !item.isSelected;
                            selected.setImageResource(android.R.color.transparent);
                            selectTotal--;
                            map.remove(path);

                        } else {
                            Message message = Message.obtain(mHandler, AppConstant.WHAT.NINE);
                            message.sendToTarget();
                        }
                    }
                }

            });
        }
    }
}
