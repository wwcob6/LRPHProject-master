package com.app.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.R;
import com.app.friendCircleMain.domain.UserList;
import com.app.model.MessageEvent;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.ui.FamilyCircleActivity;
import com.app.ui.FriendCallActivity;
import com.app.ui.VideoDial;
import com.app.ui.VideoPlay;
import com.app.ui.message.WeixinActivity;
import com.app.video.RtpVideo;
import com.app.video.SendActivePacket;
import com.app.video.VideoInfo;
import com.app.view.CircleImageView;
import com.punuo.sys.app.fragment.BaseFragment;
import com.punuo.sys.app.util.IntentUtil;
import com.punuo.sys.app.util.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static com.app.model.Constant.devid1;


public class LaoRenFragment extends BaseFragment implements View.OnClickListener {

    TextView title;
    private Boolean shan = true;
    private Handler handlervideo = new Handler();
    String SdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String TAG = "MicroActivity";
    private List<UserList> userList = new ArrayList<UserList>();
    private CircleImageView alarm;
    private ImageView camera;
    private RelativeLayout re_background;
    private RelativeLayout re_funcation;
    private View mStatusBar;
    //private static String res="";//json数据


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.micro_list_header1, container, false);
        mStatusBar = view.findViewById(R.id.status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mStatusBar.setVisibility(View.VISIBLE);
            mStatusBar.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(getActivity());
            mStatusBar.requestLayout();
        }
        init(view);
        return view;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 666) {
                alarm.setVisibility(View.INVISIBLE);
            } else if (msg.what == 888) {
                alarm.setVisibility(View.VISIBLE);
            }
        }
    };
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                while (shan) {
                    handler.sendEmptyMessage(666);
                    Thread.sleep(500);
                    handler.sendEmptyMessage(888);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void aaa(MessageEvent messageEvent) {
        if (messageEvent.getMessage().equals("警报")) {
            Log.d("111111   ", "111111111");
            /*修改xml中某一区域的背景*/
            //方法一：
//            Resources resources = getActivity().getResources();
//            Drawable btnDrawable = resources.getDrawable(R.drawable.background2);
//            re_background.setBackgroundDrawable(btnDrawable);
            //方法二：
            re_background.setBackgroundResource(R.drawable.background2);
            shan = true;
            new Thread(runnable).start();
        }
    }

    private void init(View view) {
        EventBus.getDefault().register(this);  //注册
        re_background = view.findViewById(R.id.re_background);
        re_funcation = view.findViewById(R.id.re_funcation);
        camera = re_background.findViewById(R.id.iv_camera);
        camera.setVisibility(View.VISIBLE);
        camera.setOnClickListener(this);

        alarm = re_background.findViewById(R.id.alarm1);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shan = false;
                alarm.setVisibility(View.VISIBLE);
                re_background.setBackgroundResource(R.drawable.background1);
            }
        });
//        ImageView application =re_funcation.findViewById(R.id.application);
        ImageView video = re_funcation.findViewById(R.id.video);
        ImageView browse = re_funcation.findViewById(R.id.browse);
        ImageView chat = re_funcation.findViewById(R.id.chat);
//        application.setOnClickListener(this);
        video.setOnClickListener(this);
        browse.setOnClickListener(this);
        chat.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_camera:
                startActivity(new Intent(getActivity(), FamilyCircleActivity.class));
                break;
            case R.id.browse:
                if ((devid1 == null) || ("".equals(devid1))) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                            .setTitle("请先绑定设备")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    dialog.show();

                } else {
                    SipInfo.single = true;
                    String devId = SipInfo.paddevId;
                    devId = devId.substring(0, devId.length() - 4).concat("0160");//设备id后4位替换成0160
                    String devName = "pad";
                    final String devType = "2";
                    SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                    SipInfo.toDev = new NameAddress(devName, sipURL);
                    org.zoolu.sip.message.Message response = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                            SipInfo.user_from, BodyFactory.createStartMonitor(true,SipInfo.devId,SipInfo.userId));
                    SipInfo.sipUser.sendMessage(response);
                    SipInfo.queryResponse = false;
                    SipInfo.inviteResponse = false;
                    showLoadingDialog();
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                org.zoolu.sip.message.Message query = SipMessageFactory.createOptionsRequest(SipInfo.sipUser, SipInfo.toDev,
                                        SipInfo.user_from, BodyFactory.createQueryBody(devType));
                                outer:
                                for (int i = 0; i < 3; i++) {
                                    SipInfo.sipUser.sendMessage(query);
                                    for (int j = 0; j < 20; j++) {
                                        sleep(100);
                                        if (SipInfo.queryResponse) {
                                            break outer;
                                        }
                                    }
                                    if (SipInfo.queryResponse) {
                                        break;
                                    }
                                }
                                if (SipInfo.queryResponse) {
                                    org.zoolu.sip.message.Message invite = SipMessageFactory.createInviteRequest(SipInfo.sipUser,
                                            SipInfo.toDev, SipInfo.user_from, BodyFactory.createMediaBody(VideoInfo.resultion, "H.264", "G.711", devType));
                                    outer2:
                                    for (int i = 0; i < 3; i++) {
                                        SipInfo.sipUser.sendMessage(invite);
                                        for (int j = 0; j < 20; j++) {
                                            sleep(100);
                                            if (SipInfo.inviteResponse) {
                                                break outer2;
                                            }
                                        }
                                        if (SipInfo.inviteResponse) {
                                            break;
                                        }
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissLoadingDialog();
                                    }
                                });
                                if (SipInfo.queryResponse && SipInfo.inviteResponse) {
                                    Log.i("DevAdapter", "视频请求成功");
                                    SipInfo.decoding = true;
                                    try {
                                        VideoInfo.rtpVideo = new RtpVideo(VideoInfo.rtpIp, VideoInfo.rtpPort);
                                        VideoInfo.sendActivePacket = new SendActivePacket();
                                        VideoInfo.sendActivePacket.startThread();
                                        getActivity().startActivity(new Intent(getActivity(), VideoPlay.class));
                                    } catch (SocketException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.i("DevAdapter", "视频请求失败");
                                    handlervideo.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle("视频请求失败！")
                                                    .setMessage("请重新尝试")
                                                    .setPositiveButton("确定", null).show();

                                        }
                                    });
                                }
                            }
                        }
                    }.start();
                }
                break;
            case R.id.chat:
                IntentUtil.jumpActivity(getActivity(), FriendCallActivity.class);
                break;
//            case R.id.application:
///*                String url = "http://pet.qinqingonline.com:9000";
//                IntentUtil.openWebViewActivity(getActivity(), url);*/
//                startActivity(new Intent(getActivity(),WeixinActivity.class));
//                break;
            case R.id.video:
                SipInfo.single = false;
                String devId1 = SipInfo.paddevId;
//                    devId = devId1.substring(0, devId1.length() - 4).concat("0160");
// 设备id后4位替换成0160
                String devName1 = "pad";
                final String devType1 = "2";
                SipURL sipURL1 = new SipURL(devId1, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                SipInfo.toDev = new NameAddress(devName1, sipURL1);
                //视频
                org.zoolu.sip.message.Message query1 =
                        SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                        SipInfo.user_from, BodyFactory.createCallRequest("request", SipInfo.devId
                                        , SipInfo.userId));
                SipInfo.sipUser.sendMessage(query1);

                startActivity(new Intent(getActivity(), VideoDial.class));
                break;
            default:
                break;
        }
    }
}


