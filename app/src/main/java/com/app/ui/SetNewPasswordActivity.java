package com.app.ui;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.app.sip.SipInfo;
import com.app.views.CleanEditText;
import com.punuo.sys.app.activity.BaseSwipeBackActivity;
import com.punuo.sys.app.util.RegexUtils;
import com.punuo.sys.app.util.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.SMSSDK;

public class SetNewPasswordActivity extends BaseSwipeBackActivity {
    @Bind(R.id.newpassword_set)
    CleanEditText newpasswordSet;
    @Bind(R.id.newpassword_confirm)
    CleanEditText newpasswordConfirm;
    @Bind(R.id.hidepassword1)
    ImageView hidepassword1;
    @Bind(R.id.btn_down)
    TextView btnDown;
    @Bind(R.id.iv_back)
    ImageView ivBack4;
    @Bind(R.id.hidepassword2)
    ImageView hidepassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }
        newpasswordSet.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newpasswordConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @OnClick({R.id.hidepassword1, R.id.hidepassword2, R.id.btn_down, R.id.iv_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hidepassword1:
                if (newpasswordSet.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    hidepassword1.setImageResource(R.drawable.ic_eye);
                    newpasswordSet.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else if (newpasswordSet.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                    hidepassword1.setImageResource(R.drawable.ic_hide);
                    newpasswordSet.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.hidepassword2:
                if (newpasswordConfirm.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    hidepassword2.setImageResource(R.drawable.ic_eye);
                    newpasswordConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else if (newpasswordConfirm.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                    hidepassword2.setImageResource(R.drawable.ic_hide);
                    newpasswordConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.btn_down:
                SipInfo.passWord2 = newpasswordSet.getText().toString().trim();
                final String again = newpasswordConfirm.getText().toString().trim();
                if (checkInput(SipInfo.userAccount2, SipInfo.passWord2, SipInfo.code, again)) {
                    SMSSDK.submitVerificationCode("86", SipInfo.userAccount2, SipInfo.code);
                }
                break;
            case R.id.iv_back:
                scrollToFinishActivity();
                break;
        }
    }


    private boolean checkInput(String phone, String password, String code, String again) {
        if (TextUtils.isEmpty(phone)) { // 电话号码为空
            ToastUtils.showToast(R.string.tip_phone_can_not_be_empty);
        } else {
            if (!RegexUtils.checkMobile(phone)) { // 电话号码格式有误
                ToastUtils.showToast(R.string.tip_phone_regex_not_right);
            } else if (TextUtils.isEmpty(code)) { // 验证码不正确
                ToastUtils.showToast(R.string.tip_please_input_code);
            } else if (password.length() < 6 || password.length() > 32
                    || TextUtils.isEmpty(password)) { // 密码格式
                ToastUtils.showToast(R.string.tip_please_input_6_32_password);
            } else if (!password.equals(again)) {
                ToastUtils.showToast("两次密码不一致");
            } else {
                return true;
            }
        }

        return false;
    }
}
