package com.app.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.http.VerifyCodeManager;
import com.app.http.VerifyCodeManager1;
import com.app.model.PNBaseModel;
import com.app.request.ChangePwdRequest;
import com.app.sip.SipInfo;
import com.app.views.CleanEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mob.MobSDK;
import com.punuo.sys.app.activity.BaseSwipeBackActivity;
import com.punuo.sys.app.httplib.HttpManager;
import com.punuo.sys.app.httplib.RequestListener;
import com.punuo.sys.app.util.RegexUtils;
import com.punuo.sys.app.util.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 密码重置页
 */

public class ForgetPasswordActivity extends BaseSwipeBackActivity {
    private static final String TAG = "Changepassword1Activity";
    private EventHandler eventHandler;
    private VerifyCodeManager1 codeManager1;

    @Bind(R.id.num_input3)
    CleanEditText numInput3;
    @Bind(R.id.verificode_input1)
    CleanEditText verificodeInput1;
    @Bind(R.id.verificode_get)
    TextView verificodeGet;
    @Bind(R.id.btn_nextstep)
    TextView btnNextstep;
    @Bind(R.id.iv_back)
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password1);
        ButterKnife.bind(this);
        initViews();
        codeManager1 = new VerifyCodeManager1(this, numInput3, verificodeGet);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
    }

    private void initViews() {
        numInput3.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        verificodeInput1.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        MobSDK.init(this, "213c5d90b2394", "793f08e685abc8a57563a8652face144");
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
//        注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    @OnClick({R.id.verificode_get, R.id.btn_nextstep, R.id.iv_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verificode_get:
                codeManager1.getVerifyCode(VerifyCodeManager.REGISTER);
                break;
            case R.id.btn_nextstep:
                SipInfo.code = verificodeInput1.getText().toString().trim();
                SipInfo.userAccount2 = numInput3.getText().toString().trim();
                startActivity(new Intent(this, SetNewPasswordActivity.class));
                finish();
                break;
            case R.id.iv_back:
                scrollToFinishActivity();
                break;
        }
    }

    private boolean checkInput(String phone, String code) {
        if (TextUtils.isEmpty(phone)) { // 电话号码为空
            ToastUtils.showToast(R.string.tip_phone_can_not_be_empty);
        } else {
            if (!RegexUtils.checkMobile(phone)) { // 电话号码格式有误
                ToastUtils.showToast(R.string.tip_phone_regex_not_right);
            } else if (TextUtils.isEmpty(code)) { // 验证码不正确
                ToastUtils.showToast(R.string.tip_please_input_code);
            } else {
                return true;
            }
        }
        return false;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            // 短信注册成功后，返回LoginActivity,然后提示
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    commit();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "验证码已经发送",
                            Toast.LENGTH_SHORT).show();
                }
            } else if (result == SMSSDK.RESULT_ERROR) {
                Throwable throwable = (Throwable) data;
                throwable.printStackTrace();
                JsonObject obj = new JsonParser().parse(throwable.getMessage()).getAsJsonObject();
                String des = obj.get("detail").getAsString();//错误描述
                int status = obj.get("status").getAsInt();//错误代码
                if (status > 0 && !TextUtils.isEmpty(des)) {
                    Toast.makeText(ForgetPasswordActivity.this, des, Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
    });

    private void commit() {
        changePwd(SipInfo.userAccount2, SipInfo.passWord2);
    }

    private ChangePwdRequest mChangePwdRequest;

    private void changePwd(String telNum, String newPwd) {
        if (mChangePwdRequest != null && !mChangePwdRequest.isFinish()) {
            return;
        }
        mChangePwdRequest = new ChangePwdRequest();
        mChangePwdRequest.addUrlParam("tel_num", telNum);
        mChangePwdRequest.addUrlParam("password", newPwd);
        mChangePwdRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result.isSuccess()) {
                    ToastUtils.showToast("密码修改成功");
                    startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                } else {
                    if (!TextUtils.isEmpty(result.msg)) {
                        ToastUtils.showToast(result.msg);
                    }
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mChangePwdRequest);
    }
}
