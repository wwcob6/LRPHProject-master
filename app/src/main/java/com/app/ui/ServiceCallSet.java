package com.app.ui;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.app.model.CallModel;
import com.app.model.Constant;
import com.app.request.ChangeCallRequest;
import com.app.request.GetCallRequest;
import com.app.sip.SipInfo;
import com.punuo.sys.app.activity.BaseActivity;
import com.punuo.sys.app.httplib.HttpManager;
import com.punuo.sys.app.httplib.RequestListener;
import com.punuo.sys.app.util.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by maojianhui on 2018/6/26.
 */

public class ServiceCallSet extends BaseActivity {
    @Bind(R.id.et_call1)
    EditText et_call1;
    @Bind(R.id.et_call2)
    EditText et_call2;
    @Bind(R.id.et_call3)
    EditText et_call3;
    @Bind(R.id.iv_back1)
    ImageView ivBack1;
    @Bind(R.id.titleset)
    TextView titleset;

    @Bind(R.id.setcall)
    Button setcall;


    private SharedPreferences pref;
    private SharedPreferences.Editor editor1;
    private SharedPreferences.Editor editor2;
    private SharedPreferences.Editor editor3;
//    private SharedPreferences.Editor editor4;

    String devId = SipInfo.paddevId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicecall1);
        ButterKnife.bind(this);
        titleset.setText("服务电话");
        TextPaint tp = titleset.getPaint();
        tp.setFakeBoldText(true);
//        pref = PreferenceManager.getDefaultSharedPreferences(this);
//
//
//        String call1 = pref.getString("call1", "");
//        et_call1.setText(call1);
//
//        String call2 = pref.getString("call2", "");
//        et_call2.setText(call2);
//
//        String call3 = pref.getString("call3", "");
//        et_call3.setText(call3);
        getServiceCall();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }

    }

    private GetCallRequest mGetCallRequest;
    public void getServiceCall(){
        if (mChangeCallRequest != null && !mChangeCallRequest.isFinish()) {
            return;
        }
        mGetCallRequest = new GetCallRequest();
        mGetCallRequest.addUrlParam("devid",Constant.devid1);
        Log.i("", "设备id"+Constant.devid1);
        mGetCallRequest.setRequestListener(new RequestListener<CallModel>() {
            @Override
            public void onComplete() {

            }
            @Override
            public void onSuccess(CallModel result) {

                if (result == null || result.mData == null || result.mData.isEmpty()) {
                    return;
                }
//                    String house = result.mData.mPhones.get(0);
//                    Log.i("sss", "获取到的服务电话 "+result.mData.mPhones);
//                    String order = result.mData.mPhones.get(1);
//                    String property = result.mData.mPhones.get(2);

                ToastUtils.showToast("获取号码成功");
                CallModel.Data data = result.mData.get(0);
                String house = data.housekeep;
                String order = data.orderfood;
                String property = data.property;
                et_call1.setText(house);
                et_call2.setText(order);
                et_call3.setText(property);

            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mGetCallRequest);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private ChangeCallRequest mChangeCallRequest;

    @OnClick({R.id.setcall,R.id.iv_back1})
    public void onViewClick1(View view) {
        switch (view.getId()){
            case R.id.setcall:
                String HouseKeepingCall = et_call1.getText().toString();
                String OrderingCall = et_call2.getText().toString();
                String PropertyCall = et_call3.getText().toString();
                if (mChangeCallRequest != null && !mChangeCallRequest.isFinish()) {
                    return;
                }
                mChangeCallRequest = new ChangeCallRequest();
//                mChangeCallRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
                mChangeCallRequest.addUrlParam("devid", Constant.devid1);
                mChangeCallRequest.addUrlParam("housekeep", HouseKeepingCall);
                mChangeCallRequest.addUrlParam("orderfood", OrderingCall);
                mChangeCallRequest.addUrlParam("property", PropertyCall);
                mChangeCallRequest.setRequestListener(new RequestListener<String>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSuccess(String result) {
                        if (result == null) {
                            return;
                        }
                        ToastUtils.showToast("服务电话修改成功");
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                HttpManager.addRequest(mChangeCallRequest);

            case R.id.iv_back1:
                finish();
                break;
            default:
                break;
        }
    }
}
//    @OnClick({R.id.bt_set1, R.id.bt_set2, R.id.bt_set3, R.id.iv_back1})
//
//    public void onViewClick(View view) {
//        switch (view.getId()) {
//            case R.id.bt_set1:
//                Log.i("111222", "123414");
//                String type1 = et_type1.getText().toString();
//                String call1 = et_call1.getText().toString();
//                Log.i("mmma", type1);
//                Log.i("ammm", call1);
////                if (type1.equals("") || type1 == null) {
////                    Toast.makeText(ServiceCallSet.this, "服务类型为空", Toast.LENGTH_SHORT).show();
////                } else
//                if (call1.equals("") || call1 == null) {
//                    Toast.makeText(ServiceCallSet.this, "电话号码为空", Toast.LENGTH_SHORT).show();
//                } else {
//                    editor1 = pref.edit();
////                    editor1.putString("type1", type1);
//                    editor1.putString("call1", call1);
//                    editor1.apply();
//                    Toast.makeText(ServiceCallSet.this, "设置完成", Toast.LENGTH_SHORT).show();
////                    String devId1 = SipInfo.paddevId;
////                    devId = devId1.substring(0, devId1.length() - 4).concat("0160");//设备id后4位替换成0160
////                    String devName1 = "pad";
////                    final String devType1 = "2";
//                    SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
//                    SipInfo.toDev = new NameAddress(devName, sipURL);
//                    Message response = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
//                            SipInfo.user_from, BodyFactory.createServiceCall(type1, call1));
//                    SipInfo.sipUser.sendMessage(response);
//                }
//                break;
//            case R.id.bt_set2:
//                String type2 = et_type2.getText().toString();
//                String call2 = et_call2.getText().toString();
////                if (type2.equals("") || type2 == null) {
////                    Toast.makeText(ServiceCallSet.this, "服务类型为空", Toast.LENGTH_SHORT).show();
////                } else
//                if (call2.equals("") || call2 == null) {
//                    Toast.makeText(ServiceCallSet.this, "电话号码为空", Toast.LENGTH_SHORT).show();
//                } else {
//                    editor2 = pref.edit();
////                    editor2.putString("type2", type2);
//                    editor2.putString("call2", call2);
//                    editor2.apply();
//                    Toast.makeText(ServiceCallSet.this, "设置完成", Toast.LENGTH_SHORT).show();
////                    String devId1 = SipInfo.paddevId;
////                    devId = devId1.substring(0, devId1.length() - 4).concat("0160");//设备id后4位替换成0160
////                    String devName1 = "pad";
////                    final String devType1 = "2";
//                    SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
//                    SipInfo.toDev = new NameAddress(devName, sipURL);
//                    Message response = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
//                            SipInfo.user_from, BodyFactory.createServiceCall(type2, call2));
//                    SipInfo.sipUser.sendMessage(response);
//                }
//                break;
//            case R.id.bt_set3:
//                String type3 = et_type3.getText().toString();
//                String call3 = et_call3.getText().toString();
////                if (type3.equals("") || type3 == null) {
////                    Toast.makeText(ServiceCallSet.this, "服务类型为空", Toast.LENGTH_SHORT).show();
////                } else
//                if (call3.equals("") || call3 == null) {
//                    Toast.makeText(ServiceCallSet.this, "电话号码为空", Toast.LENGTH_SHORT).show();
//                } else {
//                    editor3 = pref.edit();
////                    editor3.putString("type3", type3);
//                    editor3.putString("call3", call3);
//                    editor3.apply();
//                    Toast.makeText(ServiceCallSet.this, "设置完成", Toast.LENGTH_SHORT).show();
////                    String devId1 = SipInfo.paddevId;
////                    devId = devId1.substring(0, devId1.length() - 4).concat("0160");//设备id后4位替换成0160
////                    String devName1 = "pad";
////                    final String devType1 = "2";
//                    SipURL sipURL1 = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
//                    SipInfo.toDev = new NameAddress(devName, sipURL1);
//                    Message response = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
//                            SipInfo.user_from, BodyFactory.createServiceCall(type3, call3));
//                    SipInfo.sipUser.sendMessage(response);
//                }
//                break;
//            case R.id.iv_back1:
//                finish();
//                break;
//            default:
//                break;
//        }
//    }









