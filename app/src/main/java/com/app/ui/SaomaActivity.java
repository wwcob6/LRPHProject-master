package com.app.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.UserInfoManager;
import com.app.R;
import com.app.groupvoice.GroupInfo;
import com.app.http.GetPostUtil;
import com.app.model.Constant;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.zxing.android.CaptureActivity;
import com.punuo.sys.app.activity.ActivityCollector;
import com.punuo.sys.app.activity.BaseActivity;
import com.punuo.sys.app.util.ToastUtils;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import java.lang.ref.WeakReference;

import static com.app.model.Constant.appdevid1;
import static com.app.model.Constant.devid1;
import static com.app.model.Constant.groupid1;
import static com.app.sip.SipInfo.devName;
import static com.app.sip.SipInfo.paduserid;
import static com.app.sip.SipInfo.sipUser;

public class SaomaActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_SCAN1 = 0x0000;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private View inflate;
    private TextView saoma;
    private TextView shoudong;
    private ImageView back1;
    private Dialog dialog;
    TextView cancel;
    private String response;
    String devid;
    //    String paduserid;
    //配置文件路径
    private String configPath;
    private String SdCard;


    String devId = SipInfo.paddevId;
    SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
    private String string;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saoma2);
        TextView title=(TextView) findViewById(R.id.tv_binddev);
        TextPaint tp=title.getPaint();
        tp.setFakeBoldText(true);
        back1 = (ImageView)findViewById(R.id.iv_back1);
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button bt_bind=(Button)findViewById(R.id.bt_bind);
        bt_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    show(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        SdCard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qinqingzaixian";
        configPath = SdCard + "/config.properties";

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  //该参数指布局能延伸到navigationbar，我们场景中不应加这个参数
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT); //设置navigationbar颜色为透明
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }


        if ((devid1 != null) && !("".equals(devid1))){
            finish();
         startActivity(new Intent(this, DevBindSuccessActivity.class));
        }
        // Check if we have write PermissionUtils
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have PermissionUtils so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (resultCode == RESULT_OK) {
            if (data != null) {
                String text=data.getStringExtra(DECODED_CONTENT_KEY);
                String[] s=text.split(" ");
                devid=s[0];
                GroupInfo.port=Integer.parseInt(s[1]);
                paduserid=s[2];
                Log.i("paduserid",paduserid);
                Log.i("端口号：","aaa"+    GroupInfo.port);
                SipInfo.toDev = new NameAddress(devName, sipURL);

//              devid = data.getStringExtra(DECODED_CONTENT_KEY);
                String devName = "pad";
                SipURL sipURL = new SipURL(devid, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                SipInfo.toDev = new NameAddress(devName, sipURL);
                //Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        response = GetPostUtil.sendGet1111(Constant.URL_InquireBind, "devid=" + devid);
                        Log.i("jonsresponse...........", response);
                        JSONObject obj1 = JSON.parseObject(response);
                        String msg1 = obj1.getString("msg");
                        if (msg1.equals("未绑定")) {
                            response = GetPostUtil.sendGet1111(Constant.URL_Bind, "id=" + UserInfoManager.getUserInfo().id + "&devid=" + devid);
                            Log.i("jonsresponse...........", response);
                            if (response != "") {
                                JSONObject obj = JSON.parseObject(response);
                                String msg = obj.getString("msg");
                                if (msg.equals("success")) {
                                    if (requestCode == REQUEST_CODE_SCAN1) {
                                        handler.sendEmptyMessage(1111);
                                    }
                                }else if (msg.equals("已绑定")) {
                                        handler.sendEmptyMessage(222);
                                    } else {
                                        handler.sendEmptyMessage(333);
                                    }
                                }

                            } else if (msg1.equals("已绑定")) {
                                //发消息给平台，转发给群组验证，通过后绑定
                                response = GetPostUtil.sendGet1111(Constant.URL_joinGroup, "devid=" + devid + "&id=" + UserInfoManager.getUserInfo().id);
                                Log.i("jonsresponse...........", response);
                                JSONObject obj2 = JSON.parseObject(response);
                                String msg2 = obj2.getString("msg");
                                if (msg2.equals("success")) {
                                    if (requestCode == REQUEST_CODE_SCAN1) {
                                        handler.sendEmptyMessage(1111);
                                    }
                                } else if (msg2.equals("已经加群")) {
                                    handler.sendEmptyMessage(222);
                                } else {
                                    handler.sendEmptyMessage(333);
                                }
                            }
                        }
                }).start();
            }
//            UserInfoManager.getInstance(this).setUserInfo("devid", devid1);
            //qrCodeImage.setImageBitmap(bitmap);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1111) {
                org.zoolu.sip.message.Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                        SipInfo.user_from, BodyFactory.createListUpdate("addsuccess"));
                SipInfo.sipUser.sendMessage(query);

                org.zoolu.sip.message.Message query1 = SipMessageFactory.createNotifyRequest
                        (SipInfo.sipUser, SipInfo.toDev, SipInfo.user_from,
                                BodyFactory.createGroupBindNotify(SipInfo.userId,paduserid,SipInfo.port));
                SipInfo.sipUser.sendMessage(query1);

                AlertDialog dialog = new AlertDialog.Builder(SaomaActivity.this)
                        .setCancelable(false)
                        .setTitle("绑定设备成功")
                        .setMessage("请重新登录")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
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
                                Constant.appdevid1=null;
                                SipInfo.running=false;
                                ActivityCollector.finishToFirstView();
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                return;
            }
            else if (msg.what == 222) {
                ToastUtils.showToastShort("已经绑定过该设备");
                return;
            } else if (msg.what == 333) {
                ToastUtils.showToastShort("绑定失败，不是一个合法的设备");
                return;
            }else if(msg.what==444){
              Log.d("jiebang","111");
            }
        }
    };



    public void show(View view){
        dialog = new Dialog(this,R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        inflate = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        //初始化控件
        saoma = (TextView) inflate.findViewById(R.id.tv_saoma);
        shoudong = (TextView) inflate.findViewById(R.id.tv_shoudong);
        cancel = (TextView) inflate.findViewById(R.id.cancel);
        saoma.setOnClickListener(this);
        shoudong.setOnClickListener(this);
        cancel.setOnClickListener(this);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        Display display=getWindowManager().getDefaultDisplay();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width=(int)display.getWidth();
        lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_saoma:
//                Toast.makeText(this,"点击了拍照",Toast.LENGTH_SHORT).show();
//                boolOpenCarmer();
                Intent intent = new Intent(SaomaActivity.this,
                        CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN1);
                break;
            case R.id.tv_shoudong:
//                Toast.makeText(this,"点击了从相册选择",Toast.LENGTH_SHORT).show();
                final EditText editText = new EditText(this);
                new AlertDialog.Builder(this).setTitle("请输入设备号").setIcon(
                        android.R.drawable.ic_dialog_info).setView(editText
                ).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        devid = editText.getText().toString();
                        String devName = "pad";
                        SipURL sipURL = new SipURL(devid, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                        SipInfo.toDev = new NameAddress(devName, sipURL);
                        org.zoolu.sip.message.Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                                SipInfo.user_from, BodyFactory.createAdddevNotify(devid,SipInfo.userId));
                        SipInfo.sipUser.sendMessage(query);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                response = GetPostUtil.sendGet1111(Constant.URL_InquireBind, "devid=" + devid);
                                Log.i("jonsresponse...........", response);
                                JSONObject obj1 = JSON.parseObject(response);
                                String msg1 = obj1.getString("msg");
                                if (msg1.equals("未绑定")) {
                                    response = GetPostUtil.sendGet1111(Constant.URL_Bind, "id=" + UserInfoManager.getUserInfo().id + "&devid=" + devid);
                                    Log.i("jonsresponse...........", response);
                                    JSONObject obj = JSON.parseObject(response);
                                    String msg = obj.getString("msg");
                                    if (msg.equals("success")) {
                                        handler.sendEmptyMessage(1111);
                                    } else if (msg.equals("已绑定")) {
                                        handler.sendEmptyMessage(222);
                                    } else if (msg.equals("设备绑定用户失败")) {
                                        handler.sendEmptyMessage(333);
                                    }
                                } else if (msg1.equals("已绑定")) {
                                    //发消息给平台，转发给群主验证，通过后绑定
                                    response = GetPostUtil.sendGet1111(Constant.URL_joinGroup, "devid=" + devid+"&id="+UserInfoManager.getUserInfo().id);
                                    Log.i("jonsresponse...........", response);
                                    JSONObject obj2 = JSON.parseObject(response);
                                    String msg2 = obj2.getString("msg");
                                    if (msg2.equals("success")) {
                                        handler.sendEmptyMessage(1111);
                                    } else if (msg2.equals("已经加群")) {
                                        handler.sendEmptyMessage(222);
                                    } else  {
                                        handler.sendEmptyMessage(333);
                                    }
                                }
                            }
                        }).start();

                    }
                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).show();
                break;
            case R.id.cancel:
                Toast.makeText(this,"点击了取消",Toast.LENGTH_SHORT).show();
                break;
        }
        dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void boolOpenCarmer(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)  //打开相机权限
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)   //可读
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)  //可写
                        != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE
                            ,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);}
    }
}

