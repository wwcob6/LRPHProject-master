package com.punuo.sys.app.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.punuo.sys.app.R;


public class PNLoadingDialog extends Dialog {
    private String mLoadingMsg = null;
    private TextView tvLoading;

    public PNLoadingDialog(Context context) {
        this(context, R.style.LoadingViewDialog);
    }

    public PNLoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_view_dialog);
        tvLoading = findViewById(R.id.text_loading);
        if (TextUtils.isEmpty(mLoadingMsg)) {
            tvLoading.setVisibility(View.GONE);
        } else {
            tvLoading.setVisibility(View.VISIBLE);
            tvLoading.setText(mLoadingMsg);
        }
    }

    public void setLoadingMsg(String loadingMsg) {
        this.mLoadingMsg = loadingMsg;
        if (tvLoading != null && !TextUtils.isEmpty(loadingMsg)) {
            tvLoading.setText(mLoadingMsg);
        }
    }
}
