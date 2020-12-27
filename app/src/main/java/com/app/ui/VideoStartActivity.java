package com.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.app.R;
import com.app.model.MessageEvent;
import com.app.tools.SipCallManager;
import com.punuo.sys.app.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.app.camera.FileOperateUtil.TAG;

/**
 * Created by maojianhui on 2018/7/11.
 */

public class VideoStartActivity extends BaseActivity {
    private Handler handlervideo = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);  //注册
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videostart);
        SipCallManager.getInstance().callVideoChat(this,true);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getMessage().equals("关闭")) {
            Log.i(TAG, "111message is " + event.getMessage());
            // 更新界面
            finish();
//            Toast.makeText(this,"对方已取消",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销订阅者
        EventBus.getDefault().unregister(this);
    }


}
