package com.app.video;

import java.net.DatagramSocket;

import jlibrtp.Participant;
import jlibrtp.RTPSession;

public class RTPSending {

    public DatagramSocket rtpSocket1 = null;
    public DatagramSocket rtcpSocket1 = null;
    public RTPSession rtpSession1 = null;
    public RTPSession rtpSession2 = null;

    public RTPSending() {
        try {
            //设置RTP会话的两个接口
            rtpSocket1 = new DatagramSocket();
            rtcpSocket1 = new DatagramSocket();
        } catch (Exception e) {
            System.out.println("RTPSession failed to obtain port");
        }

        //新建RTP会话，本地的rtp和rtcp端口号
        rtpSession1 = new RTPSession(rtpSocket1, rtcpSocket1);
        rtpSession2 = new RTPSession(rtpSocket1, rtcpSocket1);
        //建立会话参与者，这里输入的是对方的ip和rtpsession会话端口号
//		Participant p1 = new Participant("192.168.1.7", 5002, 5003);
        Participant p1 = new Participant(VideoInfo.media_info_ip, VideoInfo.media_info_port, VideoInfo.media_info_port + 1);
        //会话中加入参与者
        rtpSession1.addParticipant(p1);
        rtpSession2.addParticipant(p1);
    }
}