package com.app.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.UserInfoManager;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.ui.address.AddressManagerActivity;
import com.punuo.sys.app.activity.ActivityCollector;
import com.punuo.sys.app.activity.BaseSwipeBackActivity;
import com.punuo.sys.app.util.DataClearUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.app.model.Constant.groupid1;
import static com.app.sip.SipInfo.sipUser;


public class SettingActivity extends BaseSwipeBackActivity {


    @Bind(R.id.re_psds)
    RelativeLayout rePsds;
    @Bind(R.id.tv_phonenumber)
    TextView tvPhonenumber;
    @Bind(R.id.re_phonenumber)
    RelativeLayout reInstructions;
    @Bind(R.id.re_address)
    RelativeLayout reAddress;
    @Bind(R.id.re_message)
    RelativeLayout reMessage;
    @Bind(R.id.re_introduction)
    RelativeLayout reIntroduction;
    @Bind(R.id.re_buffer)
    RelativeLayout reBuffer;
    @Bind(R.id.logout)
    TextView logout;
    @Bind(R.id.re_personal)
    RelativeLayout rePersonal;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.tv_buff)
    TextView tvBuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        title.setText("设置");
        tvBuff.setText(DataClearUtil.getTotalCacheSize(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }
    }


    @OnClick({R.id.re_psds, R.id.re_phonenumber, R.id.re_personal, R.id.re_address,
            R.id.re_message, R.id.re_introduction, R.id.re_buffer, R.id.logout, R.id.back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_phonenumber:
                startActivity(new Intent(this, NumberBind.class));
                break;
            case R.id.re_personal:
                startActivity(new Intent(this, UserInfoActivity.class));
                break;
            case R.id.logout:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("注销账户?")
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sipUser.sendMessage(SipMessageFactory.createNotifyRequest(sipUser, SipInfo.user_to,
                                        SipInfo.user_from, BodyFactory.createLogoutBody()));
                                if ((groupid1 != null) && !("".equals(groupid1))) {
                                    SipInfo.sipDev.sendMessage(SipMessageFactory.createNotifyRequest(SipInfo.sipDev, SipInfo.dev_to,
                                            SipInfo.dev_from, BodyFactory.createLogoutBody()));
                                }
//                                if ((groupid1 != null) && !("".equals(groupid1))) {
//                                    GroupInfo.groupUdpThread.stopThread();
//                                    GroupInfo.groupKeepAlive.stopThread();
//                                }
                                dialog.dismiss();
                                SipInfo.running = false;
                                UserInfoManager.clearUserData();
                                ActivityCollector.finishToFirstView();
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
            case R.id.re_psds:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.re_address:
                startActivity(new Intent(this, AddressManagerActivity.class));
                break;
            case R.id.re_introduction:
                startActivity(new Intent(this, SoftwareInstructActivity.class));
                break;
            case R.id.re_buffer:
                //清除缓存
                DataClearUtil.cleanAllCache(this);
                Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
                tvBuff.setText(DataClearUtil.getTotalCacheSize(this));
                break;
            case R.id.re_message:
                startActivity(new Intent(this, MessageNotifyActivity.class));
                break;
            case R.id.back:
                scrollToFinishActivity();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
