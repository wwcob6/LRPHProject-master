package com.app.ui;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.R;
import com.app.http.GetPostUtil;
import com.punuo.sys.app.util.RegexUtils;
import com.app.http.VerifyCodeManager;
import com.app.model.Constant;
import com.app.sip.SipInfo;
import com.app.views.CleanEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mob.MobSDK;
import com.punuo.sys.app.activity.BaseActivity;
import com.punuo.sys.app.util.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class NumberBind extends BaseActivity {
    @Bind(R.id.iv_back1)
    ImageView ivBack1;
    @Bind(R.id.textView4)
    TextView textView4;
    @Bind(R.id.tv_currentnum)
    TextView tvCurrentnum;
    @Bind(R.id.currentphone)
    TextView currentphone;
    @Bind(R.id.tv_phone)
    CleanEditText tvPhone;
    @Bind(R.id.verificode_input2)
    CleanEditText verificodeInput2;
    @Bind(R.id.btn_send_verifi_code)
    Button btnSendVerifiCode;
    @Bind(R.id.rl_phone)
    RelativeLayout rlPhone;
    @Bind(R.id.btn_confirm)
    Button btnConfirm;
    private VerifyCodeManager codeManager;
    String response;
    private EventHandler eventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_bind);
        ButterKnife.bind(this);
        initView();
        codeManager = new VerifyCodeManager(this, tvPhone, btnSendVerifiCode);
        currentphone.setText(SipInfo.userAccount);
//        btnConfirm.setBackgroundColor(Color.parseColor("#f3b337"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
    }

    private void initView() {
        tvPhone.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        verificodeInput2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        verificodeInput2.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // 点击虚拟键盘的done
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    commit();
                }
                return false;
            }
        });
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
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    private void commit() {
        SipInfo.userAccount=tvPhone.getText().toString().trim();
        String code=verificodeInput2.getText().toString().trim();
        if(checkInput(SipInfo.userAccount,code)){
            new Thread() {
                @Override
                public void run() {
                    response = GetPostUtil.sendGet1111(Constant.URL_ChPhoneNum, "tel_num=" + SipInfo.userAccount + "&" + "userid=" + SipInfo.userId);
                    Log.i("jonsresponse", response);
                    if ((response != null) && !("".equals(response))) {
                        JSONObject obj = JSON.parseObject(response);
                        String msg = obj.getString("msg");
                        if(msg.equals("success")){
                            handler1.sendEmptyMessage(1111);
                            Log.d("1234","绑定的手机号更改");
                        }else if(msg.equals("更新失败")){
                            handler1.sendEmptyMessage(2222);
                            Log.d("1234","绑定的手机号更改失败");
                        }
                    }
                }
            }.start();
        }
    }


    @OnClick({R.id.tv_phone, R.id.verificode_input2, R.id.btn_send_verifi_code,
            R.id.rl_phone, R.id.btn_confirm, R.id.currentphone,R.id.iv_back1})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_verifi_code:
                codeManager.getVerifyCode(VerifyCodeManager.REGISTER);
                break;
            case R.id.btn_confirm:
                final String phone = tvPhone.getText().toString().trim();
                final String code = verificodeInput2.getText().toString().trim();
                if (checkInput(phone, code)) {
                    SMSSDK.submitVerificationCode("86", phone, code);
                }
            case R.id.iv_back1:
                finish();
            default:
                break;
        }
    }
    Handler handler1=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what==1111){
                finish();
                ToastUtils.showToastShort("更改成功");
            }else if(msg.what==2222){
                ToastUtils.showToastShort("更改失败");
            }
        }
    };

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            Log.e("result", "result=" + result);
            // 短信注册成功后，返回LoginActivity,然后提示
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
//                    Toast.makeText(RegisterAccountActivity.this, "验证成功",
//                            Toast.LENGTH_SHORT).show();
                    final String phone = tvPhone.getText().toString().trim();
                    String code = verificodeInput2.getText().toString().trim();
                    if (checkInput(phone, code)) {
                        commit();
                    } else {
                        Toast.makeText(NumberBind.this, "填写信息格式不正确", Toast.LENGTH_SHORT).show();
                    }
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
                    Toast.makeText(NumberBind.this, des, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    };

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
}
