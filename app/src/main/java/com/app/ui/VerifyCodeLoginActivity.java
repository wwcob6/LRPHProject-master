package com.app.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.UserInfoManager;
import com.app.db.DatabaseInfo;
import com.app.db.MyDatabaseHelper;
import com.app.db.SQLiteManager;
import com.app.friendCircleMain.domain.Alldevid;
import com.app.friendCircleMain.domain.Group;
import com.app.friendCircleMain.domain.GroupList;
import com.app.friendCircleMain.domain.UserFromGroup;
import com.app.friendCircleMain.domain.UserList;
import com.app.groupvoice.GroupInfo;
import com.app.http.VerifyCodeManager;
import com.app.http.VerifyCodeManager1;
import com.app.model.Constant;
import com.app.model.Friend;
import com.app.model.PNUserInfo;
import com.app.request.GetAllGroupFromUserRequest;
import com.app.request.GetAllUserFromGroupRequest;
import com.app.request.GetDevIdFromIdRequest;
import com.app.sip.KeepAlive;
import com.app.sip.SipDev;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.sip.SipUser;
import com.app.views.CleanEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mob.MobSDK;
import com.punuo.sys.app.activity.BaseSwipeBackActivity;
import com.punuo.sys.app.httplib.HttpManager;
import com.punuo.sys.app.httplib.RequestListener;
import com.punuo.sys.app.util.RegexUtils;
import com.punuo.sys.app.util.ToastUtils;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.app.model.Constant.devid1;
import static com.app.model.Constant.devid2;
import static com.app.model.Constant.devid3;
import static java.lang.Thread.sleep;

public class VerifyCodeLoginActivity extends BaseSwipeBackActivity {
    @Bind(R.id.num_input4)
    CleanEditText numInput4;
    @Bind(R.id.vericode_input)
    CleanEditText vericodeinput;
    @Bind(R.id.get_verificode)
    TextView getVerificode;
    @Bind(R.id.password_login)
    TextView passwordLogin;
    @Bind(R.id.btn_login2)
    TextView btnLogin2;
    @Bind(R.id.iv_back5)
    ImageView ivBack5;
    @Bind(R.id.newAccount_register)
    TextView newAccountRegister;
    private VerifyCodeManager1 codeManager1;
    private EventHandler eventHandler;
    //前一次的账号
    private String lastUserAccount;
    //密码错误次数
    private int errorTime = 0;
    //网络连接失败窗口
    private AlertDialog newWorkConnectedDialog;

    private String TAG = getClass().getSimpleName();
    //账号不存在
    private AlertDialog accountNotExistDialog;
    //登陆超时
    private AlertDialog timeOutDialog;
    private List<String> list = new ArrayList<String>();
    private List<UserList> userList = new ArrayList<UserList>();
    private List<GroupList> groupList = new ArrayList<GroupList>();
    private String[] groupname = new String[3];
    private String[] groupid = new String[3];
    private String[] appdevid = new String[3];
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificode_login);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
        initData();
        codeManager1 = new VerifyCodeManager1(this, numInput4, getVerificode);
    }

    private void initData() {
        numInput4.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        getVerificode.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        getVerificode.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // 点击虚拟键盘的done
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    login();
                }
                return false;
            }
        });
        MobSDK.init(this, "213c5d90b2394", "793f08e685abc8a57563a8652face144");
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                android.os.Message msg = new android.os.Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }


    @OnClick({R.id.get_verificode, R.id.password_login, R.id.btn_login2, R.id.newAccount_register})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_verificode:
                codeManager1.getVerifyCode(VerifyCodeManager.REGISTER);
                break;
            case R.id.password_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btn_login2:
                SipInfo.passWord = null;//验证码登录没有设置密码，将密码设置为空
                final String phone = numInput4.getText().toString().trim();
                final String code = vericodeinput.getText().toString().trim();
                if (checkInput(phone, code)) {
                    SMSSDK.submitVerificationCode("86", phone, code);
                }
                break;
            case R.id.newAccount_register:
                startActivity(new Intent(this, RegisterAccountActivity.class));
                break;
            case R.id.iv_back5:
                scrollToFinishActivity();
                break;
        }
    }

    private void login() {
        if (SipInfo.isNetworkConnected) {
            SipInfo.userAccount = numInput4.getText().toString();
            SipInfo.code = getVerificode.getText().toString();
            if (checkInput(SipInfo.userAccount, SipInfo.code)) {
                // TODO: 请求服务器登录账号
                if (!SipInfo.userAccount.equals(lastUserAccount)) {
                    errorTime = 0;
                }
                beforeLogin();
                showLoadingDialog();
                SipInfo.isVericodeLogin = true;
                new Thread(connecting).start();
            }
        } else {
            //弹出网络连接失败窗口
            handler.post(networkConnectedFailed);
        }

    }


    private void beforeLogin() {
        SipInfo.isAccountExist = true;
        SipInfo.passwordError = false;
        SipInfo.userLogined = false;
        SipInfo.loginTimeout = true;
        SipURL local = new SipURL(SipInfo.REGISTER_ID, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
        SipURL remote = new SipURL(SipInfo.SERVER_ID, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
        SipInfo.user_from = new NameAddress(SipInfo.userAccount, local);
        SipInfo.user_to = new NameAddress(SipInfo.SERVER_NAME, remote);
        SipInfo.devLogined = false;
        SipInfo.dev_loginTimeout = true;

        SipURL remote_dev = new SipURL(SipInfo.SERVER_ID, SipInfo.serverIp, SipInfo.SERVER_PORT_DEV);

        SipInfo.dev_to = new NameAddress(SipInfo.SERVER_NAME, remote_dev);
    }

    // 网络是否连接
    private Runnable networkConnectedFailed = new Runnable() {
        @Override
        public void run() {
            if (newWorkConnectedDialog == null || !newWorkConnectedDialog.isShowing()) {
                newWorkConnectedDialog = new AlertDialog.Builder(VerifyCodeLoginActivity.this)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent mIntent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(mIntent);
                            }
                        })
                        .setTitle("当前无网络,请检查网络连接")
                        .create();
                newWorkConnectedDialog.setCancelable(false);
                newWorkConnectedDialog.setCanceledOnTouchOutside(false);
                newWorkConnectedDialog.show();
            }
        }
    };

    private Runnable accountNotExist = new Runnable() {
        @Override
        public void run() {
            if (accountNotExistDialog == null || !accountNotExistDialog.isShowing()) {
                accountNotExistDialog = new AlertDialog.Builder(VerifyCodeLoginActivity.this)
                        .setTitle("不存在该账号")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                accountNotExistDialog.show();
                accountNotExistDialog.setCancelable(false);
                accountNotExistDialog.setCanceledOnTouchOutside(false);
            }
        }
    };
    private Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            if (timeOutDialog == null || !timeOutDialog.isShowing()) {
                timeOutDialog = new AlertDialog.Builder(VerifyCodeLoginActivity.this)
                        .setTitle("连接超时,请检查网络")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                timeOutDialog.show();
                timeOutDialog.setCancelable(false);
                timeOutDialog.setCanceledOnTouchOutside(false);
            }
        }
    };

    private void showDialogTip(final int errorTime) {
        if (errorTime < 2) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = new AlertDialog.Builder(VerifyCodeLoginActivity.this)
                            .setTitle("密码输入错误/还有" + (2 - errorTime) + "次输入机会")
                            .setPositiveButton("确定", null)
                            .create();
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(false);
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = new AlertDialog.Builder(VerifyCodeLoginActivity.this)
                            .setTitle("由于密码输入错误过多,该账号已被冻结")
                            .setPositiveButton("确定", null)//锁账号暂未完成
                            .create();
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(false);
                    Toast.makeText(getApplicationContext(), "该账号已被冻结", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    Runnable connecting = new Runnable() {
        @Override
        public void run() {
            try {

                int hostPort = new Random().nextInt(5000) + 2000;
                SipInfo.sipUser = new SipUser(null, hostPort, VerifyCodeLoginActivity.this);
                org.zoolu.sip.message.Message register = SipMessageFactory.createRegisterRequest(
                        SipInfo.sipUser, SipInfo.user_to, SipInfo.user_from);
                SipInfo.sipUser.sendMessage(register);
                sleep(1000);
                for (int i = 0; i < 2; i++) {
                    if (!SipInfo.isAccountExist) {
                        //用户账号不存在
                        break;
                    }
                    if (SipInfo.passwordError) {
                        //密码错误
                        break;
                    }
                    if (!SipInfo.loginTimeout) {
                        //没有超时
                        break;
                    }
                    SipInfo.sipUser.sendMessage(register);
                    sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

                if (!SipInfo.isAccountExist) {
                    dismissLoadingDialog();
                    /**账号不存在提示*/
                    handler.post(accountNotExist);
                } else if (SipInfo.passwordError) {
                    //密码错误提示
                    dismissLoadingDialog();
                    showDialogTip(errorTime++);
                    lastUserAccount = SipInfo.userAccount;
                } else if (SipInfo.loginTimeout) {
                    dismissLoadingDialog();
                    //超时
                    handler.post(timeOut);
                } else {

                    if (SipInfo.userLogined) {
                        Log.i(TAG, "用户登录成功!");
                        //开启用户保活心跳包
                        SipInfo.keepUserAlive = new KeepAlive();
                        SipInfo.keepUserAlive.setType(0);
                        SipInfo.keepUserAlive.startThread();
                        //数据库
                        String dbPath = SipInfo.userId + ".db";
//                        deleteDatabase(dbPath);
                        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(VerifyCodeLoginActivity.this, dbPath, null, 1);
                        DatabaseInfo.sqLiteManager = new SQLiteManager(myDatabaseHelper);

//                        SipInfo.applist.clear();
//                        //请求服务器上的app列表
//                        SipInfo.sipUser.sendMessage(SipMessageFactory.createSubscribeRequest(SipInfo.sipUser,
//                                SipInfo.user_to, SipInfo.user_from, BodyFactory.createAppsQueryBody()));
                        //启动设备注册线程
//                        new Thread(getuserinfo).start();
                        getUserInfo();
                    }
                }
            }
        }
    };
    //获取用户信息
    private void getUserInfo() {
        RequestListener listener = new RequestListener<PNUserInfo>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNUserInfo result) {
                if (result != null && result.isSuccess()) {
                    UserInfoManager.setUserInfo(result.userInfo);
                    SipInfo.friends.clear();
                    getGroupInfo();
                } else {
                    ToastUtils.showToastShort("获取用户数据失败请重试");
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showToastShort("获取用户数据失败请重试");
                dismissLoadingDialog();
            }
        };
        UserInfoManager.getInstance().refreshUserInfo(listener);
    }

    private GetAllGroupFromUserRequest mGetAllGroupFromUserRequest;

    //获取组信息
    private void getGroupInfo() {
        if (mGetAllGroupFromUserRequest != null && !mGetAllGroupFromUserRequest.isFinish()) {
            return;
        }
        mGetAllGroupFromUserRequest = new GetAllGroupFromUserRequest();
        mGetAllGroupFromUserRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetAllGroupFromUserRequest.setRequestListener(new RequestListener<Group>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(Group result) {
                if (result != null) {
                    if (result.groupList != null && !result.groupList.isEmpty()) {
                        //重构疑问：这个循环的意义
                        groupname = new String[]{null, null, null};
                        groupid = new String[]{null, null, null};
                        appdevid = new String[]{null, null, null};
                        for (int i = 0; i < result.groupList.size(); i++) {
                            groupname[i] = result.groupList.get(i).getGroup_name();
                            groupid[i] = result.groupList.get(i).getGroupid();
                        }
                        devid1 = groupname[0];
                        devid2 = groupname[1];
                        devid3 = groupname[2];

                        Constant.groupid1 = groupid[0];
                        Constant.groupid2 = groupid[1];
                        Constant.groupid3 = groupid[2];
                        Constant.groupid = Constant.groupid1;
                        if (!TextUtils.isEmpty(Constant.groupid1)) {
                            SipInfo.paddevId = Constant.devid1;
                            getDevIdInfo();
                        } else {
                            dismissLoadingDialog();
                            startActivity(new Intent(VerifyCodeLoginActivity.this, HomeActivity.class));
                        }
                    }
                } else {
                    ToastUtils.showToastShort("获取用户数据失败请重试");
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showToastShort("获取用户数据失败请重试");
                dismissLoadingDialog();
            }
        });
        HttpManager.addRequest(mGetAllGroupFromUserRequest);
    }
    private GetDevIdFromIdRequest mGetDevIdFromIdRequest;

    //获取用户设备信息
    private void getDevIdInfo() {
        if (mGetDevIdFromIdRequest != null && !mGetDevIdFromIdRequest.isFinish()) {
            return;
        }
        mGetDevIdFromIdRequest = new GetDevIdFromIdRequest();
        mGetDevIdFromIdRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetDevIdFromIdRequest.setRequestListener(new RequestListener<Alldevid>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(Alldevid result) {
                if (result != null) {
                    List<String> devIdLists = result.devid;
                    if (devIdLists.isEmpty()) {
                        ToastUtils.showToast("获取设备id失败");
                        startActivity(new Intent(VerifyCodeLoginActivity.this, HomeActivity.class));
                    } else {
                        appdevid[0] = devIdLists.get(0);
                        Constant.appdevid1 = appdevid[0];
                        if (!TextUtils.isEmpty(appdevid[0])) {
                            SipInfo.devId = appdevid[0];
                            SipURL local_dev = new SipURL(SipInfo.devId, SipInfo.serverIp, SipInfo.SERVER_PORT_DEV);
                            SipInfo.dev_from = new NameAddress(SipInfo.devId, local_dev);
                            new Thread(devConnecting).start();
                        }
                        //获取组内用户
                        getAllUserFormGroup();
                    }
                } else {
                    ToastUtils.showToast("获取用户devid失败请重试");
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showToastShort("获取用户devid失败请重试");
                dismissLoadingDialog();
            }
        });
        HttpManager.addRequest(mGetDevIdFromIdRequest);
    }

    private GetAllUserFromGroupRequest mGetAllUserFromGroupRequest;

    //获取组内用户
    private void getAllUserFormGroup() {
        if (mGetAllUserFromGroupRequest != null && !mGetAllUserFromGroupRequest.isFinish()) {
            return;
        }
        mGetAllUserFromGroupRequest = new GetAllUserFromGroupRequest();
        mGetAllUserFromGroupRequest.addUrlParam("groupid", Constant.groupid);
        mGetAllUserFromGroupRequest.setRequestListener(new RequestListener<UserFromGroup>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(UserFromGroup result) {
                if (result != null) {
                    if (result.userList != null && !result.userList.isEmpty()) {
                        for (int i = 0; i < result.userList.size(); i++) {
                            UserList userList = result.userList.get(i);
                            Friend friend = new Friend();
                            friend.setNickName(userList.getNickname());
                            friend.setPhoneNum(userList.getName());
                            friend.setUserId(userList.getUserid());
                            friend.setId(userList.getId());
                            friend.setAvatar(userList.getAvatar());
                            SipInfo.friends.add(friend);
                        }

                        GroupInfo.groupNum = "7000";
//                        String peer = peerElement.getFirstChild().getNodeValue();
                        GroupInfo.ip = "101.69.255.134";
//                        GroupInfo.port = 7000;
                        GroupInfo.level = "1";
                        SipInfo.devName = UserInfoManager.getUserInfo().nickname;
                        dismissLoadingDialog();
                        startActivity(new Intent(VerifyCodeLoginActivity.this, HomeActivity.class));
                    } else {
                        ToastUtils.showToastShort("获取用户数据失败请重试");
                        dismissLoadingDialog();
                    }
                } else {
                    ToastUtils.showToastShort("获取用户数据失败请重试");
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mGetAllUserFromGroupRequest);
    }

    //设备注册线程
    private Runnable devConnecting = new Runnable() {
        @Override
        public void run() {
            try {
                int hostPort = new Random().nextInt(5000) + 2000;
                SipInfo.sipDev = new SipDev(VerifyCodeLoginActivity.this, null, hostPort);//无网络时在主线程操作会报异常
                org.zoolu.sip.message.Message register = SipMessageFactory.createRegisterRequest(
                        SipInfo.sipDev, SipInfo.dev_to, SipInfo.dev_from);

                for (int i = 0; i < 3; i++) {//如果没有回应,最多重发2次
                    SipInfo.sipDev.sendMessage(register);
                    sleep(2000);
                    if (!SipInfo.dev_loginTimeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (SipInfo.devLogined) {
                    Log.d(TAG, "设备注册成功!");
                    Log.d(TAG, "设备心跳包发送!");

                    //启动设备心跳线程
                    SipInfo.keepDevAlive = new KeepAlive();
                    SipInfo.keepDevAlive.setSipDev(SipInfo.sipDev);
                    SipInfo.keepDevAlive.setDev_from(SipInfo.dev_from);
                    SipInfo.keepDevAlive.setType(1);
                    SipInfo.keepDevAlive.startThread();

                } else {
                    Log.e(TAG, "设备注册失败!");
                    Looper.prepare();
                    ToastUtils.showToastShort("设备注册失败请重新登录");
                    dismissLoadingDialog();
                    Looper.loop();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
        SMSSDK.unregisterEventHandler(eventHandler);
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
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
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
                    login();
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
                    Toast.makeText(VerifyCodeLoginActivity.this, des, Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
    });

}
