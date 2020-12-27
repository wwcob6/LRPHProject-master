package com.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.util.Log;

import com.app.service.SipService;
import com.app.sip.SipInfo;
import com.app.ui.PhoneCall;
import com.app.ui.VideoDial;


public class IncomingCallReceiver extends BroadcastReceiver {
    public IncomingCallReceiver() {
    }

    String TAG = "IncomingCallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        SipAudioCall incomingCall = null;
        SipInfo.sipService = (SipService) context;
        SipInfo.lastCall = SipInfo.sipService.getCall();
        SipAudioCall.Listener listener = new SipAudioCall.Listener() {
            @Override
            public void onCallEnded(SipAudioCall call) {
                super.onCallEnded(call);
                Log.i(TAG, "电话挂断");
                if (call.equals(SipInfo.sipService.getCall())) {
                    if (SipInfo.sipService.getAudioCallListener() != null) {
                        SipInfo.sipService.getAudioCallListener().endCall();

                    }
                    //发送消息通知H264Sending重新开启G711_encode线程
//                    if (VideoInfo.handler != null) {
//                        VideoInfo.handler.sendEmptyMessage(0x1111);
//                    }
                }
            }
        };
        try {
            SipInfo.lastCall = SipInfo.sipService.getCall();
            incomingCall = SipInfo.sipService.getManager().takeAudioCall(intent, listener);
            if (SipInfo.lastCall!=null&& SipInfo.lastCall.isInCall()) {
                    incomingCall.endCall();
            } else {
                SipInfo.sipService.setCall(incomingCall);
                PhoneCall.actionStart(context, incomingCall.getPeerProfile().getUserName(), 2);
            }
        } catch (SipException e) {
            e.printStackTrace();
        }
    }
}
