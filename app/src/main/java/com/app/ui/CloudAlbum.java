package com.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.R;
import com.app.UserInfoManager;
import com.app.adapter.CloudAlbumAdapter;
import com.app.model.CloudPhotoCover;
import com.app.model.MessageEvent;
import com.app.model.PhotoCoverResult;
import com.app.publish.PublishedActivity1;
import com.app.request.GetPhotoCoverRequest;
import com.app.sip.SipInfo;
import com.app.ui.address.AddressDetailActivity;
import com.punuo.sys.app.activity.ActivityCollector;
import com.punuo.sys.app.httplib.HttpManager;
import com.punuo.sys.app.httplib.RequestListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CloudAlbum extends Activity {
    @Bind(R.id.rv_cloudPhoto)
    RecyclerView rvCloudPhoto;
    @Bind(R.id.back1)
    ImageView back1;
    @Bind(R.id.title1)
    TextView title1;
    @Bind(R.id.add1)
    ImageView add1;
    private List<CloudPhotoCover> mCloudPhotoList = new ArrayList<>();
    private GetPhotoCoverRequest mGetPhotoCoverRequest;
    private CloudAlbumAdapter mCloudAlbumAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_album);
        ActivityCollector.addActivity(this);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        title1.setText("相册");
        RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)title1.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

        putData();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvCloudPhoto.setLayoutManager(manager);
        mCloudAlbumAdapter=new CloudAlbumAdapter(new ArrayList<>());
        rvCloudPhoto.setAdapter(mCloudAlbumAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
//        initView();
    }

    private void putData() {
        getPhotoList();
    }

    private void getPhotoList() {
        if(mGetPhotoCoverRequest!=null&&!mGetPhotoCoverRequest.isFinish()){
            return;
        }
        mGetPhotoCoverRequest=new GetPhotoCoverRequest();
        mGetPhotoCoverRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetPhotoCoverRequest.setRequestListener(new RequestListener<PhotoCoverResult>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PhotoCoverResult result) {
                if(result==null){
                    return;
                }
                if(result.mCloudPhotoCover==null||result.mCloudPhotoCover.isEmpty()){

                }else{
                    mCloudAlbumAdapter.appendData(result.mCloudPhotoCover);
                    mCloudPhotoList=result.mCloudPhotoCover;
                }

            }

            @Override
            public void onError(Exception e) {
                Log.e("Clound","获取失败");
            }
        });
        HttpManager.addRequest(mGetPhotoCoverRequest);
    }

//    private void initView() {
//        CloudAlbumAdapter adapter = new CloudAlbumAdapter(mCloudPhotoList);
//        rvCloudPhoto.setAdapter(adapter);
//    }

    @OnClick({R.id.back1,R.id.add1})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.back1:
                ActivityCollector.removeActivity(this);
                finish();
                break;
            case R.id.add1:
                startActivity(new Intent(this, PublishedActivity1.class));
                break;
                default:
                    break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("照片")) {
            startActivity(new Intent(this, AlbumActivity.class));
            Log.d("mmm","照片");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
