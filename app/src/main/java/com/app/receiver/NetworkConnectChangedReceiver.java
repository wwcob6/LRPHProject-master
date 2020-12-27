package com.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.punuo.sys.app.util.LogUtil;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;


/**
 * Created by acer on 2016/7/22.
 */
public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private String TAG = "NetworkConnectChangedReceiver";
    public static NetworkLister networkLister;

    private String getConnectionType(int type) {
        String connType = "";
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "数据流量";
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI网络";
        }
        return connType;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        /** 监听wifi的打开与关闭，与wifi的连接无关*/
        /**监听wifi的连接状态即是否连上了一个有效无线路由*/
//        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
//            Parcelable parcelableExtra = intent
//                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//            if (null != parcelableExtra) {
//                /** 获取联网状态的NetWorkInfo对象*/
//                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
//                /**获取的State对象则代表着连接成功与否等状态*/
//                NetworkInfo.State state = networkInfo.getState();
//                /**判断网络是否已经连接*/
//                boolean isConnected = state == NetworkInfo.State.CONNECTED;
//                SipInfo.isConnected = isConnected;
//                LogUtil.e(TAG, "isConnected:" + isConnected);
//                if (isConnected) {
//                    MyToast.show(context, "网络已连接", Toast.LENGTH_LONG);
//                } else {
//                    MyToast.show(context, "网络不给力,请检查网络", Toast.LENGTH_LONG);
//                }
//            } else {
//                SipInfo.isConnected = false;
//            }
//        }
        /** 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听*/
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            /**获取联网状态的NetworkInfo对象*/
            NetworkInfo info = intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            LogUtil.e(TAG, "" + info);
            if (info != null) {
                /**如果当前的网络连接成功并且网络连接可用*/
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI
                            || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        LogUtil.i(TAG, getConnectionType(info.getType()) + "连上");
                        SipInfo.isNetworkConnected = true;
                        if (SipInfo.userLogined) {
                            SipInfo.userLogined = false;
                            SipURL local = new SipURL(SipInfo.REGISTER_ID, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                            NameAddress from = new NameAddress(SipInfo.userAccount, local);
                            Message register = SipMessageFactory.createRegisterRequest(
                                    SipInfo.sipUser, SipInfo.user_to, from);
                            SipInfo.sipUser.sendMessage(register);
                        }
//                        if (SipInfo.devLogined){
//                            GroupInfo.groupUdpThread.stopThread();
//                            GroupInfo.groupKeepAlive.stopThread();
//                            context.stopService(new Intent(context, PTTService.class));
//                            SipInfo.devLogined=false;
//                            Message register = SipMessageFactory.createRegisterRequest(
//                                    SipInfo.sipDev, SipInfo.dev_to, SipInfo.dev_from,
//                                    BodyFactory.createRegisterBody(/*随便输*/"123456"));
//                            SipInfo.sipDev.sendMessage(register);
//                        }
                    }
                } else {
                    LogUtil.i(TAG, getConnectionType(info.getType()) + "断开");
                    SipInfo.isNetworkConnected = false;
                }
            }
        }
    }

    public interface NetworkLister {
        void onNetworkChanged();
    }

    public static void setNetworkLister(NetworkLister networkLister) {
        NetworkConnectChangedReceiver.networkLister = networkLister;
    }
}