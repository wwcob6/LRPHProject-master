package com.app.groupvoice;


import com.alibaba.fastjson.JSON;
import com.app.sip.SipInfo;

import static com.app.R.id.devid;


/**
 * Created by chenblue23 on 2016/7/6.
 */
public class GroupKeepAlive extends Thread {
    private boolean running = false;
    private String rtpHeartbeat;
    private String heartbeat;

    public GroupKeepAlive() {
        GroupSignaling groupSignaling = new GroupSignaling();
        groupSignaling.setDevid(SipInfo.devId);
        //rtpHeartbeat = JSON.toJSONString(groupSignaling);
        rtpHeartbeat = groupSignaling.toRTPHeartBeatString();
        groupSignaling = new GroupSignaling();
        groupSignaling.setSignal(SipInfo.devId);
        //heartbeat = JSON.toJSONString(groupSignaling);
        heartbeat = groupSignaling.toHeartBeatString();
        groupSignaling = null;
    }

    @Override
    public void run() {
        while (running) {
            try {
                GroupInfo.rtpAudio.sendActiveMsg(rtpHeartbeat.getBytes());
                GroupInfo.groupUdpThread.sendMsg(heartbeat.getBytes());
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startThread() {
        running = true;
        super.start();
    }

    public void stopThread() {
        running = false;
    }
}
