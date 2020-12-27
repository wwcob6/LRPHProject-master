package com.app.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.app.R;
import com.app.adapter.PictureAdapter;
import com.app.groupvoice.GroupInfo;
import com.app.groupvoice.GroupKeepAlive;
import com.app.groupvoice.GroupSignaling;
import com.app.groupvoice.GroupUdpThread;
import com.app.groupvoice.RtpAudio;
import com.app.model.Device;
import com.app.model.Friend;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.sip.SipUser;
import com.app.tools.MyToast;
import com.app.video.H264SendingManager;
import com.app.video.VideoInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.punuo.sys.app.activity.BaseActivity;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.app.model.Constant.groupid1;
import static com.app.sip.SipInfo.devId;
import static com.app.sip.SipInfo.groupid;
import static com.app.sip.SipUser.list;
//import static com.app.sip.SipUser.qinliaouserid;

/**
 * Author chzjy
 * Date 2016/12/19.
 * 集群呼叫
 */
//public class ChsChange extends BaseActivity implements View.OnTouchListener {
@Deprecated
public class ChsChange extends BaseActivity implements SipUser.QinliaoUpdateListener {
    //    @Bind(R.id.b1)
//    Button b1;
    @Bind(R.id.picture_recycler_view)
    RecyclerView pictureRecyclerView;
    @Bind(R.id.bt_jingyin)
    ImageView bt_jingyin;
    @Bind(R.id.bt_guan)
    ImageView bt_guan;
    public PictureAdapter pictureAdapter;
    public static List<ImageView> imageList = new ArrayList<ImageView>();
    @Bind(R.id.titleset)
    TextView titleset;
    private Bitmap bitmap;
    private String id;
    private String avatar;
    private ImageView imageView;
    ImageView[] icons = new ImageView[]{};


    //     private ArrayList<Friend> friendslist;
    private List<Device> devices;
    //    private List<Device1> devices;
//     String url[];
    AlertDialog dialog;
    private static final String TAG = "ChsChange";
    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0X111:
                    for (int i = 0; i < SipInfo.friends.size(); i++) {
                        for (int j = 0; j < list.size(); j++) {
                            if (list.get(j).equals(SipInfo.friends.get(i).getUserId())) {
                                Log.i(TAG, "qinliao111" + list.get(j));
                                SipInfo.friends.get(i).setStaus(false);

                                Log.d(TAG, "111112" + SipInfo.friends.get(i).getStaus());
                            }
                        }
                    }
                    Collections.sort(SipInfo.friends, new Comparator<Friend>() {
                        @Override
                        public int compare(Friend o1, Friend o2) {
                            if (o1.getStaus() ^ o2.getStaus()) {
                                return o1.getStaus() ? -1 : 1;
                            } else {
                                return 0;
                            }

                        }
                    });

//                    pictureAdapter.notifyDataSetChanged();
                    pictureAdapter.appendData(SipInfo.friends);
                    break;

                case 0X222:
                    for (int i = 0; i < SipInfo.friends.size(); i++) {
                        for (int j = 0; j < list.size(); j++) {
                            if (list.get(j).equals(SipInfo.friends.get(i).getUserId())) {
                                Log.i(TAG, "qinliao111" + list.get(j));
                                SipInfo.friends.get(i).setStaus(true);
                                Log.d(TAG, "111112" + SipInfo.friends.get(i).getStaus());
                            }
                        }
                    }
                    //比较器comparator对状态进行排序
                    Collections.sort(SipInfo.friends, new Comparator<Friend>() {
                        @Override
                        public int compare(Friend o1, Friend o2) {
                            if (o1.getStaus() ^ o2.getStaus()) {
                                return o1.getStaus() ? -1 : 1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    //更新
//                    pictureAdapter.notifyDataSetChanged();
                    pictureAdapter.appendData(SipInfo.friends);
                    break;
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chschange1);

        ButterKnife.bind(this);
        Log.i(TAG, GroupInfo.port + "duankou");
        Log.i(TAG, SipInfo.paduserid + "paduserid");
        titleset.setText("亲聊");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        pictureRecyclerView.setLayoutManager(layoutManager);
        pictureAdapter = new PictureAdapter(this);
        pictureRecyclerView.setAdapter(pictureAdapter);
        pictureRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
//        showPicture();
        //设置接口的监听
        SipInfo.sipUser.setQinliaoUpdateListener(this);
        //将每个成员得数据加载进来
//        pictureAdapter.appendData(SipInfo.friends);

        String devId2 = SipInfo.paddevId;
        String devName2 = "pad";
        final String devType2 = "2";
        SipURL sipURL2 = new SipURL(devId2, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
        SipInfo.toDev = new NameAddress(devName2, sipURL2);
        org.zoolu.sip.message.Message query1 = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                SipInfo.user_from, BodyFactory.createGetPort(SipInfo.userId));
        SipInfo.sipUser.sendMessage(query1);
        org.zoolu.sip.message.Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                SipInfo.user_from, BodyFactory.createOnlineNotify(SipInfo.userId));
        SipInfo.sipUser.sendMessage(query);
        Thread groupVoice = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GroupInfo.rtpAudio = new RtpAudio(SipInfo.serverIp, GroupInfo.port);
                    GroupInfo.groupUdpThread = new GroupUdpThread(SipInfo.serverIp, GroupInfo.port);
                    GroupInfo.groupUdpThread.startThread();
                    GroupInfo.groupKeepAlive = new GroupKeepAlive();
                    GroupInfo.groupKeepAlive.startThread();
//                        Intent PTTIntent = new Intent(LoginActivity.this, PTTService.class);
//                        LoginActivity.this.startService(PTTIntent);
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "groupVoice");
        groupVoice.start();

        MyToast.show(this, "正在说话...", Toast.LENGTH_LONG);
        if (GroupInfo.rtpAudio != null) {
            System.out.println(111);
//                    GroupInfo.rtpAudio.pttChanged(true);
            if (VideoInfo.track != null) {
                VideoInfo.track.play();
            }
            H264SendingManager.G711Running = false;
            waitFor();
            GroupSignaling groupSignaling = new GroupSignaling();
            groupSignaling.setStart(SipInfo.devId);
            groupSignaling.setLevel(GroupInfo.level);
            String start = JSON.toJSONString(groupSignaling);
            GroupInfo.groupUdpThread.sendMsg(start.getBytes());
        }
        //改变状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    //改变图片的亮度方法 0--原样 >0---调亮 <0---调暗
    public void changeLight(ImageView imageView, int brightness) {
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness, // 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});
        imageView.setColorFilter(new ColorMatrixColorFilter(cMatrix));
    }


    private Bitmap returnBitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;
        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
//        GroupInfo.wakeLock.release();
        if ((groupid1 != null) && !("".equals(groupid1))) {
            GroupInfo.rtpAudio.removeParticipant();
            GroupInfo.groupUdpThread.stopThread();
            GroupInfo.groupKeepAlive.stopThread();
        }
    }

    private void closeChat() {

        MyToast.show(this, "结束说话...", Toast.LENGTH_SHORT);
        if (GroupInfo.rtpAudio != null) {
            System.out.println(222);
            GroupInfo.rtpAudio.pttChanged(false);
            if (GroupInfo.isSpeak) {
                GroupSignaling groupSignaling = new GroupSignaling();
                groupSignaling.setEnd(SipInfo.devId);
                String end = JSON.toJSONString(groupSignaling);
                GroupInfo.groupUdpThread.sendMsg(end.getBytes());
                waitFor();
                if (VideoInfo.track != null) {
                    VideoInfo.track.play();
                }
                //发送消息通知H264Sending重新开启G711_encode线程
                if (VideoInfo.handler != null)
                    VideoInfo.handler.sendEmptyMessage(0x1111);
            }
        }

        String devId1 = SipInfo.paddevId;
//        devId = devId1.substring(0, devId1.length() - 4).concat("0160");//设备id后4位替换成0160
        String devName1 = "pad";
        final String devType2 = "2";
        SipURL sipURL1 = new SipURL(devId1, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
        SipInfo.toDev = new NameAddress(devName1, sipURL1);
        org.zoolu.sip.message.Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                SipInfo.user_from, BodyFactory.createOfflineNotify(SipInfo.userId));
        SipInfo.sipUser.sendMessage(query);
    }

    @Override
    public void onBackPressed() {
        dialog = new AlertDialog.Builder(this)
                .setTitle("是否结束会话?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeChat();
                        finish();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    @OnClick({R.id.bt_guan, R.id.bt_jingyin})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_guan:
                break;
            case R.id.bt_jingyin:

                break;

        }
    }



    private void showPicture() {
        sendRequestWithOkHttp();
    }

    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request1 = new Request.Builder()
                            .url("http://101.69.255.134:8000/tp5/xiaoyupeihu/public/index.php/devs/getDevInfo?devid=" + devId)
                            .build();
                    if (client.newCall(request1).execute().body().string().split("\"groupid\":").length >= 2) {
                        groupid = client.newCall(request1).execute().body().string().split("\"groupid\":")[1].split(",\"password\"")[0];
                    }
                    Request request2 = new Request.Builder()
                            .url("http://101.69.255.134:8000/tp5/xiaoyupeihu/public/index.php/groups/getAllUserFromGroup?groupid=" + groupid)
                            .build();
                    Response response = client.newCall(request2).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void parseJSONWithGSON(String responseData) {
        String jsonData = "[" + responseData.split("\\[")[1].split("\\]")[0] + "]";
        Gson gson = new Gson();
//        devices = gson.fromJson(jsonData, new TypeToken<List<Device>>() {
//        }.getType());
//        Log.i(TAG,devices.toString());
//        int i=0;
//        url=new String[devices.size()];
//        for (Device device : devices) {
//
//            url[i++] = ip + device.getId() + "/" + device.getAvatar();
//
//        }
        devices = gson.fromJson(jsonData, new TypeToken<List<Device>>() {
        }.getType());

//        for (Device device : devices){
//            Log.i(TAG, device.toString());
//            Log.i(TAG,device.getUserId());
////            Log.i(TAG,device.getId()+"aaa"+device.getDevId());
//        }
        if (!devices.isEmpty()) {
            handler.sendEmptyMessage(0X111);
        }

    }

    @Override
    public void stausOnUpdate() {
        Message message = new Message();
        message.what = 0X222;
        handler.sendMessage(message);
    }

    @Override
    public void stausOffUpdate() {
        handler.sendEmptyMessage(0X111);
    }


    private void waitFor() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
