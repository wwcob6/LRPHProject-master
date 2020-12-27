package com.punuo.sys.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;

import com.punuo.sys.app.R;
import com.punuo.sys.app.fragment.WebViewFragment;
import com.punuo.sys.app.util.StatusBarUtil;

/**
 * Created by han.chen.
 * Date on 2019/4/4.
 **/
public class WebViewActivity extends BaseSwipeBackActivity {
    private WebViewFragment mWebViewFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        setContentView(R.layout.webview_activity);
        StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, true);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mWebViewFragment = new WebViewFragment();
        mWebViewFragment.setArguments(data.getExtras());
        fragmentTransaction.replace(R.id.id_content, mWebViewFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        mWebViewFragment.handleMessage(msg);
    }
}
