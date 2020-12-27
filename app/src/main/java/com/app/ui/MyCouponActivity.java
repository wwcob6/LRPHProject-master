package com.app.ui;

import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.punuo.sys.app.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyCouponActivity extends BaseActivity {

    @Bind(R.id.iv_back1)
    ImageView ivBack1;
    @Bind(R.id.titleset)
    TextView titleset;
    @Bind(R.id.tv_use)
    TextView tvUse;
    @Bind(R.id.tv_abate)
    TextView tvAbate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupon);
        ButterKnife.bind(this);
        init();
    }

    public void init(){
        titleset.setText("优惠券");
        TextPaint tp=titleset.getPaint();
        tp.setFakeBoldText(true);
    }


    @OnClick({R.id.iv_back1,R.id.tv_use,R.id.tv_abate})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_back1:
                finish();
                break;
            case R.id.tv_use:
                break;
            case R.id.tv_abate:
                break;
        }
    }
}
