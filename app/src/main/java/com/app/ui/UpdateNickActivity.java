package com.app.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.app.UserInfoManager;
import com.app.model.PNBaseModel;
import com.app.request.UpdateNickRequest;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.views.CleanEditText;
import com.punuo.sys.app.activity.BaseActivity;
import com.punuo.sys.app.httplib.HttpManager;
import com.punuo.sys.app.httplib.RequestListener;
import com.punuo.sys.app.util.ToastUtils;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import static com.app.sip.SipInfo.devName;


public class UpdateNickActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick);
        final String nick = UserInfoManager.getUserInfo().nickname;
        final CleanEditText et_nick = (CleanEditText) this.findViewById(R.id.et_nick);
        et_nick.setText(nick);
        ImageView back = (ImageView) this.findViewById(R.id.iv_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateNickActivity.this, UserInfoActivity.class));
                finish();
            }
        });
        TextView tv_save = (TextView) this.findViewById(R.id.tv_save);
        tv_save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String newNick = et_nick.getText().toString().trim();
                if (nick.equals(newNick) || newNick.equals("") || newNick.equals("0")) {
                    return;
                }
                updateNick(newNick);
            }

        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }
    }
    private UpdateNickRequest mUpdateNickRequest;
    private void updateNick(final String newNick) {
        if (mUpdateNickRequest != null && !mUpdateNickRequest.isFinish()) {
            return;
        }
        showLoadingDialog("正在更新...");
        mUpdateNickRequest = new UpdateNickRequest();
        mUpdateNickRequest.addUrlParam("userid", SipInfo.userId);
        mUpdateNickRequest.addUrlParam("name", newNick);
        mUpdateNickRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {
                dismissLoadingDialog();
            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    ToastUtils.showToast("更新成功");
                    UserInfoManager.getInstance().refreshUserInfo();
                    //通知平板更新昵称
                    String devId = SipInfo.paddevId;
                    SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                    SipInfo.toDev = new NameAddress(devName, sipURL);
                    org.zoolu.sip.message.Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                            SipInfo.user_from, BodyFactory.createListUpdate("addsuccess"));
                    SipInfo.sipUser.sendMessage(query);
                    finish();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mUpdateNickRequest);
    }
}

