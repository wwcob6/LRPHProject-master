package com.app.groupvoice;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by chenblue23 on 2016/7/5.
 */
public class GroupUdpThread extends Thread {
    public static final String TAG = "GroupUdpThread";
    public static final int DATA_LEN = 1500;
    private boolean running = false;
    private byte[] inBuff = new byte[DATA_LEN];
    private DatagramSocket datagramSocket;
    private DatagramPacket outPacket;
    private DatagramPacket inPacket;
    private InetAddress inetAddress;
    private int port;
    public GroupUdpThread(String ip, int port) throws IOException {
        datagramSocket = new DatagramSocket();
        inPacket = new DatagramPacket(inBuff, inBuff.length);
        inetAddress = InetAddress.getByName(ip);
        this.port = port;
    }

    @Override
    public void run() {
        while (running) {
            try {
                datagramSocket.receive(inPacket);
                if (inPacket.getAddress().equals(inetAddress) && inPacket.getLength() > 0) {
                    String msg = new String(inPacket.getData(), 0 ,inPacket.getLength());
                    Log.i(TAG, msg);
                    GroupSignaling groupSignaling = JSON.parseObject(msg, GroupSignaling.class);
                    if (!TextUtils.isEmpty(groupSignaling.getSignal())) {
                        continue;
                    }
                    if (!TextUtils.isEmpty(groupSignaling.getStart())) {
                        if (groupSignaling.getStart().equals("200")) {
                            Log.i(TAG, "组呼请求成功");
                            GroupInfo.isSpeak = true;
                            GroupInfo.rtpAudio.setEncoding(true);
                        } else {
                            Log.i(TAG, "组呼请求失败");
                        }
                        continue;
                    }
                    if (!TextUtils.isEmpty(groupSignaling.getEnd())) {
                        if (groupSignaling.getEnd().equals("200")) {
                            Log.i(TAG, "结束通话");
                            GroupInfo.isSpeak = false;
                            GroupInfo.rtpAudio.setEncoding(false);
                        }
                        continue;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void startThread(){
        running = true;
        super.start();
    }

    public void stopThread(){
        running = false;
    }

    public void sendMsg(byte[] msg) {
        Log.i(TAG, "send:" + new String(msg));
        outPacket = new DatagramPacket(msg, msg.length, inetAddress, port);
        Log.i(TAG, "sendmessage"+port+inetAddress);
        new Thread() {
            @Override
            public void run() {
                try {
                    datagramSocket.send(outPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
