package com.punuo.sys.app.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.punuo.sys.app.R;
import com.punuo.sys.app.util.BaseHandler;
import com.punuo.sys.app.view.PNLoadingDialog;

/**
 * Created by Wxcily on 16/1/5.
 */
public class BaseActivity extends AppCompatActivity implements BaseHandler.MessageHandler {
    private PNLoadingDialog mLoadingDialog;
    protected BaseHandler mBaseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        initLoadingDialog();
        mBaseHandler = new BaseHandler(this);
    }

    public BaseHandler getBaseHandler() {
        return mBaseHandler;
    }

    private void initLoadingDialog() {
        mLoadingDialog = new PNLoadingDialog(this);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setCanceledOnTouchOutside(false);
    }

    public void showLoadingDialog() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    public void showLoadingDialog(String msg) {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setLoadingMsg(msg);
            showLoadingDialog();
        }
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 复写返回键操作,返回true则不继续下发
     *
     * @return
     */
    protected boolean onPressBack() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (onPressBack()) {
            return;
        }
        try {
            super.onBackPressed();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        }
        overridePendingTransition(R.anim.push_right_in, R.anim.right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        dismissLoadingDialog();
    }

    @Override
    public void handleMessage(Message msg) {

    }
}
