package com.app.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.R;
import com.app.model.Constant;
import com.app.model.MessageEvent;
import com.app.model.Msg;
import com.app.model.MyFile;
import com.app.service.BinderPoolService;
import com.app.service.NewsService;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.sip.SipUser;
import com.app.ui.fragment.LaoRenFragment;
import com.app.ui.fragment.MessageFragment;
import com.app.ui.fragment.MyFragmentManager;
import com.app.ui.fragment.PersonFragment;
import com.punuo.sys.app.activity.BaseActivity;
import com.punuo.sys.app.fragment.WebViewFragment;
import com.punuo.sys.app.update.AutoUpdateService;
import com.punuo.sys.app.util.IntentUtil;
import com.punuo.sys.app.util.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.app.model.Constant.groupid1;
import static com.app.sip.SipInfo.running;
import static com.app.sip.SipInfo.sipDev;
import static com.app.sip.SipInfo.sipUser;

//import com.punuo.sys.app.util.IntentUtil;

/**
 * Author chzjy
 * Date 2016/12/19.
 * 主界面
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener, SipUser.LoginNotifyListener ,
        SipUser.BottomListener{
    private final String TAG = getClass().getSimpleName();
    @Bind(R.id.network_layout)
    LinearLayout networkLayout;
    @Bind(R.id.content_frame)
    FrameLayout contentFrame;
    @Bind(R.id.menu_layout)
    LinearLayout menuLayout;
    @Bind(R.id.newmessage_notify)
    TextView newMessageNotify;
    @Bind(R.id.home_tab)
    View mHomeTab;
    @Bind(R.id.shop_tab)
    View mShopTab;
    @Bind(R.id.community_tab)
    View mCommunityTab;
    @Bind(R.id.message_tab)
    View mMessageTab;
    @Bind(R.id.person_tab)
    View mPersonTab;

    public static final int TAB_COUNT = 5;
    private MyFragmentManager mMyFragmentManager;
    private View[] mTabBars = new View[TAB_COUNT];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
        mMyFragmentManager = new MyFragmentManager(this);
        switchFragment(Constant.HOME);
        StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, false);
        Intent intent = new Intent(this, AutoUpdateService.class);
        intent.putExtra("needToast", false);
        IntentUtil.startServiceInSafeMode(this, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        SipInfo.lastestMsgs = DatabaseInfo.sqLiteManager.queryLastestMsg();
//        SipInfo.messageCount = 0;
//        for (int i = 0; i < SipInfo.lastestMsgs.size(); i++) {
//            if (SipInfo.lastestMsgs.get(i).getType() == 0) {
//                SipInfo.messageCount += SipInfo.lastestMsgs.get(i).getNewMsgCount();
//            }
//        }
//        if (SipInfo.messageCount != 0) {
//            messageCount.setVisibility(View.VISIBLE);
//            messageCount.setText(String.valueOf(SipInfo.messageCount));
//        } else {
//            messageCount.setVisibility(View.INVISIBLE);
//        }

    }

    private void init() {
        initTabBars();

        sipUser.setLoginNotifyListener(this);
        sipUser.setBottomListener(this);
        //启动语音电话服务
        //startService(new Intent(HomeActivity.this, SipService.class));
        //启动监听服务
        startService(new Intent(this, NewsService.class));
        //启动aidl接口服务
        startService(new Intent(this, BinderPoolService.class));
        SipInfo.loginReplace = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                sipUser.sendMessage(SipMessageFactory.createNotifyRequest(sipUser, SipInfo.user_to,
                        SipInfo.user_from, BodyFactory.createLogoutBody()));
                if ((groupid1 != null) && !("".equals(groupid1))) {
                    sipDev.sendMessage(SipMessageFactory.createNotifyRequest(sipDev, SipInfo.dev_to,
                            SipInfo.dev_from, BodyFactory.createLogoutBody()));
                }
                //关闭语音电话服务
                //stopService(new Intent(HomeActivity.this, SipService.class));
                //关闭监听服务
                stopService(new Intent(HomeActivity.this, NewsService.class));
                //关闭PTT监听服务
//                stopService(new Intent(HomeActivity.this, PTTService.class));
                //关闭aidl接口服务
                stopService(new Intent(HomeActivity.this, BinderPoolService.class));
                //关闭用户心跳
                SipInfo.keepUserAlive.stopThread();
                //关闭设备心跳
                if ((groupid1 != null) && !("".equals(groupid1))) {
                    SipInfo.keepDevAlive.stopThread();
                }
                running = false;
                //重置登录状态
                SipInfo.userLogined = false;
                SipInfo.devLogined = false;
                //关闭集群呼叫
//                GroupInfo.rtpAudio.removeParticipant();
//                if ((groupid1 != null) && !("".equals(groupid1))) {
//                    GroupInfo.groupUdpThread.stopThread();
//                    GroupInfo.groupKeepAlive.stopThread();
//                }
                AlertDialog loginReplace = new AlertDialog.Builder(getApplicationContext())
                        .setTitle("账号异地登录")
                        .setMessage("请重新登录")
                        .setPositiveButton("确定", null)
                        .create();
                loginReplace.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                loginReplace.show();
                loginReplace.setCancelable(false);
                loginReplace.setCanceledOnTouchOutside(false);
                IntentUtil.jumpActivity(getApplicationContext(), LoginActivity.class);
                return true;
            }
        });
    }

    private void initTabBars() {
        mTabBars[0] = mHomeTab;
        mTabBars[1] = mShopTab;
        mTabBars[2] = mCommunityTab;
        mTabBars[3] = mMessageTab;
        mTabBars[4] = mPersonTab;
        for (int i = 0; i < TAB_COUNT; i++) {
            mTabBars[i].setOnClickListener(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event ){
        if(event.getMessage().equals("小红点出来吧")){
            handler.sendEmptyMessage(0x11);
        }else
            if(event.getMessage().equals("取消新评论提示")){
                handler.sendEmptyMessage(0x22);
            }
    }

     Handler handler=new Handler(){
        @Override
        public void handleMessage(Message message){
            super.handleMessage(message);
            switch(message.what){
                case 0x11:
                    newMessageNotify.setVisibility(View.VISIBLE);
                    break;
                case 0x22:
                    newMessageNotify.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        ButterKnife.unbind(this);
        if ((groupid1 != null) && !("".equals(groupid1))) {
            SipInfo.keepUserAlive.stopThread();
            SipInfo.keepDevAlive.stopThread();
        }
        //关闭集群呼叫
//       GroupInfo.wakeLock.release();
//        if ((groupid1 != null) && !("".equals(groupid1))) {
//        GroupInfo.rtpAudio.removeParticipant();
//            GroupInfo.groupUdpThread.stopThread();
//            GroupInfo.groupKeepAlive.stopThread();
//        }
        SipInfo.userLogined = false;
        SipInfo.devLogined = false;
        SipInfo.loginReplace = null;
        //停止语音电话服务
        //stopService(new Intent(HomeActivity.this, SipService.class));
        //关闭监听服务
        stopService(new Intent(HomeActivity.this, NewsService.class));
        //停止PPT监听服务
//        stopService(new Intent(this, PTTService.class));
        //停止aidl接口服务
        stopService(new Intent(HomeActivity.this, BinderPoolService.class));
        sipUser.setLoginNotifyListener(null);
        sipUser.setBottomListener(null);
        //关闭线程池
        sipUser.shutdown();
        if ((groupid1 != null) && !("".equals(groupid1))) {
            sipDev.shutdown();
        }
        //关闭监听线程
        sipUser.halt();
        if ((groupid1 != null) && !("".equals(groupid1))) {
            sipDev.halt();
        }
        System.gc();
        running = false;
    }

    /**
     * 显示Fragment
     */
    public void switchFragment(int index) {
        changeTab(index);
        Bundle bundle = new Bundle();
        switch (index) {
            case Constant.HOME:
                mMyFragmentManager.switchFragmentWithCache(LaoRenFragment.class.getName(), bundle);
                break;
            case Constant.SHOP:
                //shop隐藏了，去掉了
//                mMyFragmentManager.switchFragmentWithCache(ShopFragment.class.getName(), bundle);
                break;
            case Constant.COMMUNITY:
                StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, true); //单独处理顶部状态栏颜色
                bundle.putString("url", "http://pet.qinqingonline.com:8889?user_id="+ SipInfo.userId);
                mMyFragmentManager.switchFragmentWithCache(WebViewFragment.class.getName(), bundle);
                break;
            case Constant.MESSAGE:
                mMyFragmentManager.switchFragmentWithCache(MessageFragment.class.getName(), bundle);
                break;
            case Constant.PERSON:
                mMyFragmentManager.switchFragmentWithCache(PersonFragment.class.getName(), bundle);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_tab:
                switchFragment(Constant.HOME);
                break;
            case R.id.shop_tab:
                switchFragment(Constant.SHOP);
                break;
            case R.id.community_tab:
                switchFragment(Constant.COMMUNITY);
                break;
            case R.id.message_tab:
                switchFragment(Constant.MESSAGE);
                break;
            case R.id.person_tab:
                switchFragment(Constant.PERSON);
                break;
        }
    }

    private void changeTab(int type) {
        for (int i = 0; i < TAB_COUNT; ++i) {
            if (i == type) {
                mTabBars[i].setSelected(true);
            } else {
                mTabBars[i].setSelected(false);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("keyCode = " + keyCode);
        if (keyCode == 82) {
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
                            sipUser.sendMessage(SipMessageFactory.createNotifyRequest(sipUser,
                                    SipInfo.user_to,
                                    SipInfo.user_from, BodyFactory.createLogoutBody()));
                            if ((groupid1 != null) && !("".equals(groupid1))) {
                                SipInfo.sipDev.sendMessage(SipMessageFactory.createNotifyRequest
                                        (SipInfo.sipDev, SipInfo.dev_to,
                                                SipInfo.dev_from, BodyFactory.createLogoutBody()));
                            }
//                            if ((groupid1 != null) && !("".equals(groupid1))) {
//                                GroupInfo.groupUdpThread.stopThread();
//                                GroupInfo.groupKeepAlive.stopThread();
//                            }
                            dialog.dismiss();
                            running = false;
                            com.punuo.sys.app.activity.ActivityCollector.finishToFirstView();
                        }
                    }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            return true;
        }
        if (keyCode == 4) {
            switchFragment(Constant.HOME);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onDevNotify() {
//        laorenFragment.devNotify();
    }

    @Override
    public void onUserNotify() {
//        audioFragment.userNotify();
//        contactFragment.notifyFriendListChanged();
    }

    @Override
    public void onReceivedBottomMessage(Msg msg) {
//        SipInfo.messageCount++;
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                messageCount.setVisibility(View.VISIBLE);
//                messageCount.setText(String.valueOf(SipInfo.messageCount));
//            }
//        });
    }

    @Override

    public void onReceivedBottomFileshare(MyFile myfile) {

    }


}
