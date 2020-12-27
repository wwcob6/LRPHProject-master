package com.app.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.UserInfoManager;
import com.app.db.DatabaseInfo;
import com.app.db.MyDatabaseHelper;
import com.app.db.SQLiteManager;
import com.app.friendCircleMain.domain.Alldevid;
import com.app.friendCircleMain.domain.Group;
import com.app.friendCircleMain.domain.UserFromGroup;
import com.app.friendCircleMain.domain.UserList;
import com.app.groupvoice.GroupInfo;
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
import com.app.tools.PermissionUtils;
import com.app.views.CleanEditText;
import com.punuo.sys.app.activity.BaseActivity;
import com.punuo.sys.app.httplib.HttpManager;
import com.punuo.sys.app.httplib.RequestListener;
import com.punuo.sys.app.util.StatusBarUtil;
import com.punuo.sys.app.util.ToastUtils;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.app.groupvoice.GroupInfo.wakeLock;
import static java.lang.Thread.sleep;

/**
 * 用户登陆页
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private String groupname;
    private String groupid;
    private String appdevid;
    private Handler handler = new Handler();

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    //前一次的账号
    private String lastUserAccount;
    //网络连接失败窗口
    private AlertDialog newWorkConnectedDialog;
    //账号不存在
    private AlertDialog accountNotExistDialog;
    //登陆超时
    private AlertDialog timeOutDialog;
    //密码错误次数
    private int errorTime = 0;
    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    public static List<Activity> activityList = new LinkedList();

    @Bind(R.id.num_input2)
    CleanEditText numInput2;
    @Bind(R.id.password_input)
    CleanEditText passwordInput;
    @Bind(R.id.hidepassword)
    ImageView hidepassword;
    @Bind(R.id.vericode_login)
    TextView vericodeLogin;
    @Bind(R.id.password_forget)
    TextView passwordForget;
    @Bind(R.id.btn_login1)
    TextView loginBtn;
    @Bind(R.id.tv_register)
    TextView tvRegister;
    @Bind(R.id.layout_root)
    RelativeLayout layoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        ButterKnife.bind(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        LoginActivity.activityList.add(this);
        showAboutDialog();
        setUpSplash();
        initViews();
        StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, false);
        View statusBar = findViewById(R.id.status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBar.setVisibility(View.VISIBLE);
            statusBar.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(this);
            statusBar.requestLayout();
        }
    }

    private void setUpSplash() {
        Subscription splash = Observable.timer(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> requestPermission());
        mCompositeSubscription.add(splash);
    }

    //检查网络是否连接
    public boolean isNetworkreachable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {
            SipInfo.isNetworkConnected = false;
        } else {
            SipInfo.isNetworkConnected = info.getState() == NetworkInfo.State.CONNECTED;
        }
        return SipInfo.isNetworkConnected;
    }

    public void showAboutDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        TextView title = new TextView(this);
        title.setText("服务协议和隐私政策");
        title.setPadding(10,10,10,10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);
        title.setTextColor(getResources().getColor(R.color.common_title_bg));
        dialog.setCustomTitle(title);
        TextView content = new TextView(this);
        content.setText(Html.fromHtml(
                "\n" +"请你务必审慎阅读.充分理解“服务协议”和“隐私政策”各条款，包括但不限于:为了向你提供即时通讯、内容分享等服务，我们需要收集你的设备信息、操作日志等个人信息。你可以在“设置”中查看，变更，删除个人信息并管理你的授权\n" +
                "\n" +
                "你可阅读"+"<a href=\"http://feeder.mengshitech.com/test/userPolicy.html\">《服务协议》</a>"+"和"+"<a href=\"http://feeder.mengshitech.com/test/privacy.html\">《隐私政策》</a>"+"了解详细信息。如你同意，请点击同意开始接受我们的服务。"));
        content.setMovementMethod(LinkMovementMethod.getInstance());
        dialog.setView(content);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("暂不使用", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exit();
            }
        });
        dialog.show();
    }
    public void exit(){

        for(Activity act:activityList){

            act.finish();

        }

        System.exit(0);

    }
    private void initViews() {
        /*SharedPreferences sharedPreferences=this.getSharedPreferences("share",MODE_PRIVATE);

        boolean isFirstRun=sharedPreferences.getBoolean("isFirstRun",true);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        if(isFirstRun){
            editor.putBoolean("isFirstRun",false);
            editor.commit();
            showAboutDialog();
        } else {
            Log.i(TAG, "不是第一次");
        }*/
        //showAboutDialog();
        numInput2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        numInput2.setTransformationMethod(HideReturnsTransformationMethod
                .getInstance());
        String account = pref.getString("account", "");
        numInput2.setText(account);

        passwordInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordInput.setImeOptions(EditorInfo.IME_ACTION_GO);
        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    clickLogin();
                }
                return false;
            }
        });
        String password = pref.getString("password", "");
        passwordInput.setText(password);
        SipInfo.localSdCard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/faxin/";
        isNetworkreachable();
    }

    // 网络是否连接
    private Runnable networkConnectedFailed = new Runnable() {
        @Override
        public void run() {
            if (newWorkConnectedDialog == null || !newWorkConnectedDialog.isShowing()) {
                newWorkConnectedDialog = new AlertDialog.Builder(LoginActivity.this)
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

    private void clickLogin() {
//        verifyStoragePermissions(this);
//        requestPower();
        if (SipInfo.isNetworkConnected) {
            SipInfo.userAccount = numInput2.getText().toString();
            SipInfo.passWord = passwordInput.getText().toString();
            editor = pref.edit();
            editor.putString("account", SipInfo.userAccount);
            editor.putString("password", SipInfo.passWord);
            editor.apply();
            if (checkInput(SipInfo.userAccount, SipInfo.passWord)) {
                // TODO: 请求服务器登录账号
                if (!SipInfo.userAccount.equals(lastUserAccount)) {
                    errorTime = 0;
                }
                beforeLogin();
                showLoadingDialog();

                new Thread(connecting).start();
            }
        } else {
            //弹出网络连接失败窗口
            handler.post(networkConnectedFailed);
        }
    }

    private void beforeLogin() {
        SipInfo.phoneType = Build.MODEL;
        Log.i("手机型号", "model" + SipInfo.phoneType);
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

    Runnable connecting = new Runnable() {
        @Override
        public void run() {
            try {
                int hostPort = new Random().nextInt(5000) + 2000;
                SipInfo.sipUser = new SipUser(null, hostPort, LoginActivity.this);
                Message register = SipMessageFactory.createRegisterRequest(
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
                    /*账号不存在提示*/
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
                        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(LoginActivity.this, dbPath, null, 1);
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

    //群组获取线程
    private GetAllGroupFromUserRequest mGetAllGroupFromUserRequest;

    //获取组信息
    private void getGroupInfo() {
        if (mGetAllGroupFromUserRequest != null && !mGetAllGroupFromUserRequest.isFinish()) {
            return;
        }
        groupname = null;
        groupid = null;
        appdevid = null;
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
                        for (int i = 0; i < result.groupList.size(); i++) {
                            groupname = result.groupList.get(i).getGroup_name();
                            groupid = result.groupList.get(i).getGroupid();
                        }
                        Constant.devid1 = groupname;
                        Constant.groupid1 = groupid;
                        Constant.groupid = Constant.groupid1;
                        if (!TextUtils.isEmpty(Constant.groupid1)) {
                            SipInfo.paddevId = Constant.devid1;
                            getDevIdInfo();
                        } else {
                            dismissLoadingDialog();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        }
                    }else {
                        dismissLoadingDialog();
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
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
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
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

    private GetDevIdFromIdRequest mGetDevIdFromIdRequest;

    //获取用户设备信息
    private void getDevIdInfo() {
        if (mGetDevIdFromIdRequest != null && !mGetDevIdFromIdRequest.isFinish()) {
            return;
        }
        mGetDevIdFromIdRequest = new GetDevIdFromIdRequest();
        mGetDevIdFromIdRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        Log.i(TAG, "getUserId"+UserInfoManager.getUserInfo().id);
        mGetDevIdFromIdRequest.setRequestListener(new RequestListener<Alldevid>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(Alldevid result) {
                if (result != null) {
                    List<String> devIdLists = result.devid;
                    if (devIdLists.isEmpty()) {
//                        ToastUtils.showToast("获取设备id失败");
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    } else {
                        appdevid = devIdLists.get(0);
                        Log.i(TAG, "appdevid"+appdevid);
                        Constant.appdevid1 = appdevid;
                        if (!TextUtils.isEmpty(appdevid)) {
                            SipInfo.devId = appdevid;
                            Log.i(TAG, "SipInfodevid"+SipInfo.devId);
                            SipURL local_dev = new SipURL(SipInfo.devId, SipInfo.serverIp, SipInfo.SERVER_PORT_DEV);
                            SipInfo.dev_from = new NameAddress(SipInfo.devId, local_dev);
                            new Thread(devConnecting).start();
                        }
                        //获取组内用户
                        getAllUserFormGroup();
                    }
                } else {
//                    ToastUtils.showToast("获取用户devid失败请重试");
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Exception e) {
//                ToastUtils.showToastShort("获取用户devid失败请重试");
                dismissLoadingDialog();
            }
        });
        HttpManager.addRequest(mGetDevIdFromIdRequest);
    }

    //设备注册线程
    private Runnable devConnecting = new Runnable() {
        @Override
        public void run() {
            try {
                int hostPort = new Random().nextInt(5000) + 2000;
                SipInfo.sipDev = new SipDev(LoginActivity.this, null, hostPort);//无网络时在主线程操作会报异常
                Message register = SipMessageFactory.createRegisterRequest(
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

    private void showDialogTip(final int errorTime) {
        if (errorTime < 2) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
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
                    AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
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

    private Runnable accountNotExist = new Runnable() {
        @Override
        public void run() {
            if (accountNotExistDialog == null || !accountNotExistDialog.isShowing()) {
                accountNotExistDialog = new AlertDialog.Builder(LoginActivity.this)
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
                timeOutDialog = new AlertDialog.Builder(LoginActivity.this)
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

    private boolean checkInput(String userAccount, String passWord) {
        // 账号为空时提示
        if (userAccount == null || userAccount.trim().equals("")) {
            Toast.makeText(LoginActivity.this, R.string.tip_account_empty, Toast.LENGTH_LONG)
                    .show();
        } else {
            // 账号不匹配手机号格式（11位数字且以1开头）
//            if (!RegexUtils.checkMobile(account)) {
//                Toast.makeText(LoginActivity.this, R.string.tip_account_regex_not_right,
//                        Toast.LENGTH_LONG).show();
            if (passWord == null || passWord.trim().equals("")) {
                Toast.makeText(LoginActivity.this, R.string.tip_password_can_not_be_empty,
                        Toast.LENGTH_LONG).show();
            } else {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((wakeLock != null) && (wakeLock.isHeld() == false)) {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        //ButterKnife.unbind(this);//空间解绑
    }

    @OnClick({R.id.num_input2, R.id.password_input, R.id.hidepassword,
            R.id.vericode_login, R.id.password_forget, R.id.btn_login1, R.id.tv_register})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login1:
                clickLogin();
                break;
            case R.id.hidepassword:
                if (passwordInput.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    hidepassword.setImageResource(R.drawable.ic_eye);
                    passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else if (passwordInput.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                    hidepassword.setImageResource(R.drawable.ic_hide);
                    passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.vericode_login:
                startActivity(new Intent(this, VerifyCodeLoginActivity.class));
                break;
            case R.id.password_forget:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterAccountActivity.class));
                break;
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.PermissionUtils.READ_EXTERNAL_STORAGE",
            "android.PermissionUtils.WRITE_EXTERNAL_STORAGE"};


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限

            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.PermissionUtils.WRITE_EXTERNAL_STORAGE");
            if (permission != PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestPower() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "需要读写权限，请打开设置开启对应的权限", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }
    }

    /**
     * onRequestPermissionsResult方法重写，Toast显示用户是否授权
     */
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        String requestPermissionsResult = "";
//        if (requestCode == 1) {
//            for (int i = 0; i < permissions.length; i++) {
//                if (grantResults[i] == PERMISSION_GRANTED) {
//                    requestPermissionsResult += permissions[i] + " 申请成功\n";
//                } else {
//                    requestPermissionsResult += permissions[i] + " 申请失败\n";
//                }
//            }
//        }
//        Toast.makeText(this, requestPermissionsResult, Toast.LENGTH_SHORT).show();
//    }

    private PermissionUtils.PermissionGrant mGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {

        }

        @Override
        public void onPermissionCancel() {
            Toast.makeText(LoginActivity.this, getString(R.string.alirtc_permission), Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private void requestPermission() {
        PermissionUtils.requestMultiPermissions(this,
                new String[]{
                        PermissionUtils.PERMISSION_CAMERA,
                        PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE,
                        PermissionUtils.PERMISSION_RECORD_AUDIO,
                        PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, mGrant);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.CODE_MULTI_PERMISSION) {
            PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mGrant);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionUtils.REQUEST_CODE_SETTING) {
            new Handler().postDelayed(this::requestPermission, 500);

        }

    }
}
