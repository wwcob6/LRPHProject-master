package com.app.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.app.R;
import com.app.model.PNBaseModel;
import com.app.request.ChangePwdRequest;
import com.app.sip.SipInfo;
import com.app.views.CleanEditText;
import com.punuo.sys.app.activity.BaseSwipeBackActivity;
import com.punuo.sys.app.httplib.HttpManager;
import com.punuo.sys.app.httplib.RequestListener;
import com.punuo.sys.app.util.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author chzjy
 * Date 2016/12/19.
 * 修改密码页
 */

public class ChangePasswordActivity extends BaseSwipeBackActivity implements View.OnClickListener {


    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.oldpassword_input)
    CleanEditText oldpasswordInput;
    @Bind(R.id.newpassword_input)
    CleanEditText newpasswordInput;
    @Bind(R.id.newpassword_again)
    CleanEditText newpasswordAgain;
    @Bind(R.id.btn_revise)
    Button btnRevise;
    @Bind(R.id.layout_root)
    LinearLayout layoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frogetpwd);
        ButterKnife.bind(this);
        initViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }
    }


    private void initViews() {
        title.setText("修改密码");
        oldpasswordInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        newpasswordInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        newpasswordAgain.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        newpasswordAgain.setOnEditorActionListener(new OnEditorActionListener() {

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //注册回调监听接口
    private void commit() {
        final String old = oldpasswordInput.getText().toString().trim();
        SipInfo.passWord2 = newpasswordInput.getText().toString().trim();
        final String again = newpasswordAgain.getText().toString().trim();
        if (checkInput(old, SipInfo.passWord2, again)) {
            changePwd(SipInfo.userAccount, SipInfo.passWord2);
        }
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
                    startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
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


    private boolean checkInput(String old, String password, String again) {
        if (SipInfo.isVericodeLogin) {
            if (password.length() < 6 || password.length() > 32
                    || TextUtils.isEmpty(password)) {
                ToastUtils.showToast(R.string.tip_please_input_6_32_password);
            } else if (!password.equals(again)) {
                ToastUtils.showToast("两次密码不一致");
            } else {
                return true;
            }
        } else if (!(old.equals(SipInfo.passWord))) { // 旧密码输入错误
            ToastUtils.showToast(R.string.tip_password_not_same);
        } else if (password.length() < 6 || password.length() > 32
                || TextUtils.isEmpty(password)) { // 密码格式
            ToastUtils.showToast(R.string.tip_please_input_6_32_password);
        } else if (!password.equals(again)) {
            ToastUtils.showToast("两次密码不一致");
        } else {
            return true;
        }
        return false;
    }

    @OnClick({R.id.back, R.id.btn_revise})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_revise:
                commit();
                break;
            default:
                break;
        }
    }
}
