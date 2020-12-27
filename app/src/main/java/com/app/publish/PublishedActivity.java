package com.app.publish;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.app.R;
import com.app.UserInfoManager;
import com.app.friendCircleMain.event.FriendReLoadEvent;
import com.app.friendcircle.FileUtils;
import com.app.model.PNBaseModel;
import com.app.publish.adapter.GridImageAdapter;
import com.app.publish.event.ChooseImageResultEvent;
import com.app.publish.event.EditImageEvent;
import com.app.request.UploadPostRequest;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.punuo.sys.app.activity.BaseSwipeBackActivity;
import com.punuo.sys.app.httplib.HttpManager;
import com.punuo.sys.app.httplib.RequestListener;
import com.punuo.sys.app.util.CommonUtil;
import com.punuo.sys.app.util.StatusBarUtil;
import com.punuo.sys.app.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PublishedActivity extends BaseSwipeBackActivity {

    private RecyclerView mGridView;
    private GridImageAdapter mGridImageAdapter;
    private EditText mEditText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_layout);
        init();
        StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, false);
        View statusBar = findViewById(R.id.status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBar.setVisibility(View.VISIBLE);
            statusBar.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(this);
            statusBar.requestLayout();
        }
    }

    public void init() {
        mEditText = (EditText) findViewById(R.id.edit_input);
        mGridView = (RecyclerView) findViewById(R.id.grid_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mGridView.setLayoutManager(gridLayoutManager);
        mGridImageAdapter = new GridImageAdapter(this, new ArrayList<>(), new ICallBack() {
            @Override
            public void itemClick(String path, int position) {
                CommonUtil.hideKeyboard(PublishedActivity.this);
                PictureSelector.create(PublishedActivity.this)
                        .openGallery(PictureMimeType.ofImage())
                        .imageSpanCount(4)
                        .selectionMode(PictureConfig.SINGLE)
                        .imageFormat(PictureMimeType.JPEG)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            }
        });
        mGridView.setAdapter(mGridImageAdapter);
        mGridImageAdapter.resetData(new ArrayList<>());
        TextView publish = (TextView) findViewById(R.id.activity_selectimg_send);
        publish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String dongTai = mEditText.getText().toString();
                uploadPost(dongTai, compressBitmap(mGridImageAdapter.getData()));
            }
        });

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }
    private List<String> compressBitmap(List<String> selectBitmap) {
        showLoadingDialog("正在压缩图片...");
        List<String> tempList = new ArrayList<>();
        for (int i = 0; i < selectBitmap.size(); i++) {
            Bitmap bitmap = FileUtils.compressBitmap(selectBitmap.get(i));
            String temp = FileUtils.saveBitmap(bitmap, String.valueOf(System.currentTimeMillis()));
            tempList.add(temp);
        }
        return tempList;
    }

    private UploadPostRequest mUploadPostRequest;

    private void uploadPost(String content, List<String> list) {
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showToast("发送的内容不能为空");
            return;
        }
        if (mUploadPostRequest != null && !mUploadPostRequest.isFinish()) {
            return;
        }
        showLoadingDialog("正在上传...");
        mUploadPostRequest = new UploadPostRequest();
        mUploadPostRequest.addEntityParam("id", UserInfoManager.getUserInfo().id);
        mUploadPostRequest.addEntityParam("content", content);
        List<File> files = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                String filePath = list.get(i);
                File file = new File(filePath);
                if (file.exists()) {
                    files.add(file);
                }
            }
        }
        if (!files.isEmpty()) {
            mUploadPostRequest.addEntityParam("file[]", files);
        }
        mUploadPostRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {
                dismissLoadingDialog();
                FileUtils.deleteCircleDir();
            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result.isSuccess()) {
                    ToastUtils.showToast("状态上传成功");
                    EventBus.getDefault().post(new FriendReLoadEvent());
                    finish();
                } else {
                    ToastUtils.showToast("状态上传失败请重试");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mUploadPostRequest);
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (resultCode == RESULT_OK) {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    LocalMedia localMedia = selectList.get(0);
                    String path = localMedia.getPath();
                    mGridImageAdapter.addData(path);
                }
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChooseImageResultEvent event) {
        List<String> images = event.mImages;
        mGridImageAdapter.resetData(images);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EditImageEvent event) {
        List<String> images = event.mImages;
        mGridImageAdapter.resetData(images);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
