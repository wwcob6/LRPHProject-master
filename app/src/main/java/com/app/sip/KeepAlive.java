package com.app.sip;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.message.Message;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class KeepAlive extends Thread {

    private int type; //0代表用户,1代表设备
private SipDev sipDev;
    private NameAddress dev_from;
    @Override
    public void run() {
        while (SipInfo.running) {
            try {
                if (type==0) {
                    Message heartbeat = SipMessageFactory.createRegisterRequest(
                            SipInfo.sipUser, SipInfo.user_to, SipInfo.user_from,
                            BodyFactory.createHeartbeatBody());
                    SipInfo.sipUser.sendMessage(heartbeat);
                    //延时20s,等于是20s发送一次
                    sleep(20000);
                }else if(type==1){
                    Message heartbeat_dev = SipMessageFactory.createRegisterRequest(
                            sipDev, SipInfo.dev_to, dev_from, BodyFactory.createHeartbeatBody());
                    SipInfo.sipDev.sendMessage(heartbeat_dev);

                    sleep(20000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDev_from(NameAddress dev_from) {
        this.dev_from = dev_from;
    }

    public void setSipDev(SipDev sipDev) {
        this.sipDev = sipDev;
    }

    public void startThread(){
        SipInfo.running = true;
        super.start();
    }

    public void stopThread(){
        SipInfo.running = false;
    }

    public void setType(int type) {
        this.type = type;
    }
}
