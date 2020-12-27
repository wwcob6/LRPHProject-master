package com.app.video;

import android.util.Log;

import com.app.audio.AudioRecordManager;
import com.app.groupvoice.G711;
import com.app.sip.SipInfo;

import java.net.DatagramSocket;
import java.net.SocketException;

import jlibrtp.DataFrame;
import jlibrtp.Participant;
import jlibrtp.RTPAppIntf;
import jlibrtp.RTPSession;



/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class RtpVideo implements RTPAppIntf {
    private final byte H264_STREAM_HEAD[] = {0x00, 0x00, 0x00, 0x01};
    private RTPSession rtpSession;
    private DatagramSocket rtpSocket;
    private StreamBuf streamBuf;
    private byte tempNal[] = new byte[200000];
    private int tempNalLen = 0;
    private int putNum;
    private int preSeq=-1;
    private boolean isPacketLost = true;
    private int frameSizeG711 = 160;
    private int samp_rate = 8000;
    private Participant p;
    String index_RTP;
    String info_RTP;
    String info_NALU;
    private int state=0;
    static int flag=1;
    int headseqnum=0;
    int tailseqnum=0;
    int subduction=0;
    boolean isI=false;
    private  int num=0;

    public RtpVideo(String networkAddress, int remoteRtpPort) throws SocketException {
        rtpSocket = new DatagramSocket();
        rtpSession = new RTPSession(rtpSocket, null);
        rtpSession.RTPSessionRegister(this, null, null);
        p = new Participant(networkAddress, remoteRtpPort, remoteRtpPort + 1);
        rtpSession.addParticipant(p);
        rtpSession.naivePktReception(false);
        rtpSession.frameReconstruction(false);
        streamBuf = new StreamBuf(100, 5);
        for (int i = 0; i < VideoInfo.nalBuffers.length; i++) {
            VideoInfo.nalBuffers[i] = new NalBuffer();
        }
        putNum = 0;
        AudioRecordManager.getInstance().play();
    }

    @Override
    public void receiveData(DataFrame frame, Participant participant) {
        if (frame.payloadType() == 98) {
            StreamBufNode rtpFrameNode = new StreamBufNode(frame);
            streamBuf.addToBufBySeq(rtpFrameNode);
            if (streamBuf.isReady()) {
                StreamBufNode streamBufNode = streamBuf.getFromBuf();
                int seqNum = streamBufNode.getSeqNum();
                byte[] data = streamBufNode.getDataFrame().getConcatenatedData();
                int len = streamBufNode.getDataFrame().getTotalLength();

                this.index_RTP=Integer.toString(seqNum);
                this.info_RTP="got RTP  "+this.index_RTP;
                Log.d("RTPSockets ",this.info_RTP);

                Log.d("Rtp", "getNalDm365");
                Log.d("Rtp", "len:" + len + "  seqNum:" + seqNum);

                try {
                    if(isSpsAndPps(data,seqNum,len)){
                        handleConpletePacket(data,seqNum,len);
                    }
                    if (((data[6]&31)==5)&&((data[2]&0xff)==0)&&((data[3]&0xff)==0)&&((data[4]&0xff)==0)&&((data[5]&0xff)==1)){
                        flag=0;
                    }
                } catch (Exception e) {
                    Log.d("data","数据长度过短");
                }
                if (flag==0){
                    getNalDm365f1(data, seqNum, len);
                    Log.i("丢帧数","lostpacket  "+num);
                }

//                getNalDm365(data, seqNum, len);
//                getNalDm365f1(data, seqNum, len);
                VideoInfo.isrec = 2;
            }
        } else if (frame.payloadType() == 69) {
            byte[] audioBuffer = new byte[frameSizeG711];
            short[] audioData = new short[frameSizeG711];
            audioBuffer = frame.getConcatenatedData();
            G711.ulaw2linear(audioBuffer, audioData, frameSizeG711);
            AudioRecordManager.getInstance().write(audioData, 0, frameSizeG711);
        }
        else if(frame.payloadType()==70){
            byte data1[]=frame.getConcatenatedData();
            if(data1.length>0){
                for (int i=0;i<data1.length;i++){
                    SipInfo.bitErrorRate=data1[0];
                    Log.i("误码率", "receiveData: "+data1[i]);
                }
            }
        }
    }

    public void endSession() {
        rtpSession.endSession();
    }

    @Override
    public void userEvent(int type, Participant[] participant) {

    }

    //移除当前监听的端口
    public void removeParticipant() {
        rtpSession.removeParticipant(p);
    }

    @Override
    public int frameSize(int payloadType) {
        return 1;
    }

    public void sendActivePacket(byte[] msg) {
        rtpSession.payloadType(0x7a);
        for (int i = 0; i < 2; i++) {
            rtpSession.sendData(msg);
        }
    }
    public void getNalDm365f1(byte[] data,int seqNum,int len){
        int e;
        if(len>6) {
                if(((data[6] & 31) == 5) && ((data[2] & 0xff) == 0)
                        && ((data[3] & 0xff) == 0) && ((data[4] & 0xff) == 0)
                        && ((data[5] & 0xff) == 1)) {
                    isI=true;
                }

        if(isI){
            if((data[0]&31)==28){
                if((data[1]&0xe0)==0x80){
                    this.tempNal=new byte[200000];
                    tempNal[0] = H264_STREAM_HEAD[0];
                    tempNal[1] = H264_STREAM_HEAD[1];
                    tempNal[2] = H264_STREAM_HEAD[2];
                    tempNal[3] = H264_STREAM_HEAD[3];
                    try {
                        System.arraycopy(data,2,this.tempNal,4,len-2);
                    } catch (Exception e1) {
                        Log.d("RTPPackets", "System.arraycopy failed!");
                    }

                    VideoInfo.nalBuffers[this.putNum].setNalLen(len+2);
                    this.preSeq=seqNum;
                    this.headseqnum=seqNum;
                    this.subduction=this.headseqnum-this.tailseqnum;
                    this.state=1;
                    Log.i("GetNal", "@@@MAIN START@@@         now the Nalseqnum is =" + seqNum);
                    Log.i("GetNal", "^^^^^head - tail=" + this.subduction);
                }else if(this.state==1){
                    if (this.preSeq+1==seqNum){
                        if((data[1]&0xe0)==0x40){
                            try {
                                System.arraycopy(data,2,this.tempNal,
                                        VideoInfo.nalBuffers[this.putNum].getNalLen(),len-2);
                            } catch (Exception e1) {
                                Log.d("RTPPackets", "System.arraycopy failed!");
                            }

                            VideoInfo.nalBuffers[this.putNum].addNalLen(len-2);
                            this.preSeq=seqNum;
                            VideoInfo.nalBuffers[this.putNum].isWriteable();
                            System.arraycopy(this.tempNal,0,VideoInfo.nalBuffers[this.putNum].getWriteable_Nalbuf(),
                                    0,VideoInfo.nalBuffers[this.putNum].getNalLen());
//                            copyFromTempToNal();
                            this.info_NALU=Integer.toString(this.putNum);
                            this.info_NALU=this.info_NALU+"Write Nalu done!";
                            Log.d("NALUWrite", this.info_NALU);
                            VideoInfo.nalBuffers[this.putNum].writeLock();
                            ++this.putNum;
                            if (this.putNum==200){
                                this.putNum=0;
                            }
                            this.tailseqnum=seqNum;
                            Log.i("GetNal", "###MAIN LAST###         now the Nalseqnum is =" + seqNum);
                            this.state = 0;
                            this.isI=false;
                        }else{
                            try {
                                System.arraycopy(data, 2, this.tempNal, VideoInfo.nalBuffers[this.putNum].getNalLen(), len - 2);
                            } catch (Exception e1) {
                                Log.d("RTPPackets", "System.arraycopy failed!");
                            }
                            VideoInfo.nalBuffers[this.putNum].addNalLen(len-2);
                            this.preSeq=seqNum;
                        }
                    }else{
                        e=seqNum=this.preSeq;
                        flag=1;
                        num++;
                        this.tempNal=new byte[200000];
                        VideoInfo.nalBuffers[this.putNum].setNalLen(0);
                        this.preSeq=seqNum;
                        Log.i("GetNal", "!!!!!!!!!MAIN FRAME !!!Lost Num = " + e + "*****" + "this Packet is" + seqNum);
                        this.state = 0;
                        Log.i("RTPlost", "******* flag chaged !!!!!****=" + flag);
                    }
                }else {
                    e = seqNum - this.preSeq;
                    flag = 1;
                    num++;
                    this.tempNal = new byte[200000];
                    VideoInfo.nalBuffers[this.putNum].setNalLen(0);
                    this.preSeq = seqNum;
                    Log.i("GetNal", "!!!!!!!!!MAIN FRAME !!!Lost Num = " + e + "*****" + "this Packet is" + seqNum);
                    this.state = 0;
                    Log.i("RTPlost", "******* flag chaged !!!!!****=" + flag);
                }
            }else {
                this.tempNal=new byte[200000];
                tempNal[0] = H264_STREAM_HEAD[0];
                tempNal[1] = H264_STREAM_HEAD[1];
                tempNal[2] = H264_STREAM_HEAD[2];
                tempNal[3] = H264_STREAM_HEAD[3];
                try {
                    System.arraycopy(data,0,this.tempNal,4,len);
                } catch (Exception e1) {
                    Log.d("RTPPackets", "System.arraycopy failed!");
                }

                VideoInfo.nalBuffers[this.putNum].setNalLen(len+4);
                this.preSeq=seqNum;
                VideoInfo.nalBuffers[this.putNum].isWriteable();
                System.arraycopy(this.tempNal, 0, VideoInfo.nalBuffers[this.putNum].
                        getWriteable_Nalbuf(), 0, VideoInfo.nalBuffers[this.putNum].getNalLen());
//                copyFromTempToNal();
                this.info_NALU = Integer.toString(this.putNum);
                this.info_NALU = this.info_NALU + "Write Nalu done!";
                Log.i("NALUWrite", this.info_NALU);
                VideoInfo.nalBuffers[this.putNum].writeLock();
                ++this.putNum;
                if(this.putNum == 200) {
                    this.putNum = 0;
                }
                this.state = 0;
                this.isI=false;
                this.headseqnum = seqNum;
                this.subduction = this.headseqnum - this.tailseqnum;
                Log.i("GetNal", "@@@START............Single packet@@@now the Nalseqnum is =" + seqNum);
                Log.i("GetNal", "^^^^^head - tail=" + this.subduction);
                this.tailseqnum = seqNum;
            }
        }else if((data[0]&31)==28){
            if((data[1]&0xe0)==0x80){
                this.tempNal=new byte[200000];
                tempNal[0] = H264_STREAM_HEAD[0];
                tempNal[1] = H264_STREAM_HEAD[1];
                tempNal[2] = H264_STREAM_HEAD[2];
                tempNal[3] = H264_STREAM_HEAD[3];
                try {
                    System.arraycopy(data,2,this.tempNal,4,len-2);
                } catch (Exception e1) {
                    Log.d("RTPPackets", "System.arraycopy failed!");
                }

                VideoInfo.nalBuffers[this.putNum].setNalLen(len+2);
                this.preSeq=seqNum;
                this.headseqnum=seqNum;
                this.subduction=this.headseqnum-this.tailseqnum;
                Log.i("GetNal", "@@@MAIN START@@@         now the Nalseqnum is =" + seqNum);
                Log.i("GetNal", "^^^^^head - tail=" + this.subduction);
            } else if (this.preSeq + 1 == seqNum) {
                    if ((data[1] & 0xe0) == 0x40) {
                        try {
                            System.arraycopy(data, 2, this.tempNal,
                                    VideoInfo.nalBuffers[this.putNum].getNalLen(), len - 2);
                        } catch (Exception e1) {
                            Log.d("RTPPackets", "System.arraycopy failed!");
                        }

                        VideoInfo.nalBuffers[this.putNum].addNalLen(len - 2);
                        this.preSeq = seqNum;
                        VideoInfo.nalBuffers[this.putNum].isWriteable();
                        System.arraycopy(this.tempNal, 0, VideoInfo.nalBuffers[this.putNum].getWriteable_Nalbuf(),
                                0, VideoInfo.nalBuffers[this.putNum].getNalLen());
                        this.info_NALU = Integer.toString(this.putNum);
                        this.info_NALU = this.info_NALU + "Write Nalu done!";
                        Log.d("NALUWrite", this.info_NALU);
                        VideoInfo.nalBuffers[this.putNum].writeLock();
                        ++this.putNum;
                        if (this.putNum == 200) {
                            this.putNum = 0;
                        }
                        this.tailseqnum = seqNum;
                        Log.i("GetNal", "###MAIN LAST###         now the Nalseqnum is =" + seqNum);
                    } else {
                        try {
                            System.arraycopy(data, 2, this.tempNal, VideoInfo.nalBuffers[this.putNum].getNalLen(), len - 2);
                        } catch (Exception e1) {
                            Log.d("RTPPackets", "System.arraycopy failed!");
                        }
                        VideoInfo.nalBuffers[this.putNum].addNalLen(len - 2);
                        this.preSeq = seqNum;
                    }
                }else {
                    e=seqNum=this.preSeq;
                    flag=1;
                    num++;
                    this.tempNal=new byte[200000];
                    VideoInfo.nalBuffers[this.putNum].setNalLen(0);
                    this.preSeq=seqNum;
                    Log.i("GetNal", "!!!!!!!!!MAIN FRAME !!!Lost Num = " + e + "*****" + "this Packet is" + seqNum);
                }
            } else {
            if(this.preSeq==seqNum+1){
                handleConpletePacket(data, seqNum, len);
            }else{
                e=seqNum-this.preSeq;
                flag = 1;
                num++;
                this.tempNal = new byte[200000];
                VideoInfo.nalBuffers[this.putNum].setNalLen(0);
                this.preSeq = seqNum;
                Log.i("GetNal", "!!!!!!!!!MAIN FRAME !!!Lost Num = " + e + "*****" + "this Packet is" + seqNum);
                this.state = 0;
                Log.i("RTPlost", "******* flag chaged !!!!!****=" + flag);
            }

//            this.tempNal=new byte[50000];
//            tempNal[0] = H264_STREAM_HEAD[0];
//            tempNal[1] = H264_STREAM_HEAD[1];
//            tempNal[2] = H264_STREAM_HEAD[2];
//            tempNal[3] = H264_STREAM_HEAD[3];
//            try {
//                System.arraycopy(data,0,this.tempNal,4,len);
//            } catch (Exception e1) {
//                Log.d("RTPPackets", "System.arraycopy failed!");
//            }
//
//            VideoInfo.nalBuffers[this.putNum].setNalLen(len+4);
//            this.preSeq=seqNum;
//            VideoInfo.nalBuffers[this.putNum].isWriteable();
//            System.arraycopy(this.tempNal, 0, VideoInfo.nalBuffers[this.putNum].
//                    getWriteable_Nalbuf(), 0, VideoInfo.nalBuffers[this.putNum].getNalLen());
//            this.info_NALU = Integer.toString(this.putNum);
//            this.info_NALU = this.info_NALU + "Write Nalu done!";
//            Log.d("NALUWrite", this.info_NALU);
//            VideoInfo.nalBuffers[this.putNum].writeLock();
//            ++this.putNum;
//            if(this.putNum == 200) {
//                this.putNum = 0;
//            }
//            this.state = 0;
//            this.headseqnum = seqNum;
//            this.subduction = this.headseqnum - this.tailseqnum;
//            Log.d("GetNal", "@@@START............Single packet@@@now the Nalseqnum is =" + seqNum);
//            Log.d("GetNal", "^^^^^head - tail=" + this.subduction);
//            this.tailseqnum = seqNum;
        }
        }else{
            if(this.preSeq==seqNum+1){
                if((data[1]&0xe0)==0x40){
                    handleLastPacket(data,seqNum,len);
                }
            }
        }
    }

    public void getNalDm365f(byte[] data,int seqNum,int len){
        int e;
//        if ((data[2]&31)==5){
        boolean isI= false;
        try {
            isI = ((data[6]&31)==5)&&((data[2]&0xff)==0)&&((data[3]&0xff)==0)&&((data[4]&0xff)==0)&&((data[5]&0xff)==1);
        } catch (Exception e1) {
            Log.e("RTPVideo","数据长度过短");
        }
        if(isI){
            if((data[0]&31)==28){
                if((data[1]&0xe0)==0x80){
                    this.tempNal=new byte[100000];
                    tempNal[0] = H264_STREAM_HEAD[0];
                    tempNal[1] = H264_STREAM_HEAD[1];
                    tempNal[2] = H264_STREAM_HEAD[2];
                    tempNal[3] = H264_STREAM_HEAD[3];
                    try {
                        System.arraycopy(data,2,this.tempNal,4,len-2);
                    } catch (Exception e1) {
                        Log.d("RTPPackets", "System.arraycopy failed!");
                    }

                    VideoInfo.nalBuffers[this.putNum].setNalLen(len+2);
                    this.preSeq=seqNum;
                    this.headseqnum=seqNum;
                    this.subduction=this.headseqnum-this.tailseqnum;
                    this.state=1;
                    Log.i("GetNal", "@@@MAIN START@@@         now the Nalseqnum is =" + seqNum);
                    Log.i("GetNal", "^^^^^head - tail=" + this.subduction);
                }else if(this.state==1){
                    if (this.preSeq+1==seqNum){
                        if((data[1]&0xe0)==0x40){
                            try {
                                System.arraycopy(data,2,this.tempNal,
                                        VideoInfo.nalBuffers[this.putNum].getNalLen(),len-2);
                            } catch (Exception e1) {
                                Log.d("RTPPackets", "System.arraycopy failed!");
                            }

                            VideoInfo.nalBuffers[this.putNum].addNalLen(len-2);
                            this.preSeq=seqNum;
                            VideoInfo.nalBuffers[this.putNum].isWriteable();
                            System.arraycopy(this.tempNal,0,VideoInfo.nalBuffers[this.putNum].getWriteable_Nalbuf(),
                                    0,VideoInfo.nalBuffers[this.putNum].getNalLen());
//                            copyFromTempToNal();
                            this.info_NALU=Integer.toString(this.putNum);
                            this.info_NALU=this.info_NALU+"Write Nalu done!";
                            Log.d("NALUWrite", this.info_NALU);
                            VideoInfo.nalBuffers[this.putNum].writeLock();
                            ++this.putNum;
                            if (this.putNum==200){
                                this.putNum=0;
                            }
                            this.tailseqnum=seqNum;
                            Log.i("GetNal", "###MAIN LAST###         now the Nalseqnum is =" + seqNum);
                            this.state = 0;
                        }else{
                            try {
                                System.arraycopy(data, 2, this.tempNal, VideoInfo.nalBuffers[this.putNum].getNalLen(), len - 2);
                            } catch (Exception e1) {
                                Log.d("RTPPackets", "System.arraycopy failed!");
                            }
                            VideoInfo.nalBuffers[this.putNum].addNalLen(len-2);
                            this.preSeq=seqNum;
                        }
                    }else{
                        e=seqNum=this.preSeq;
                        flag=1;
                        this.tempNal=new byte[100000];
                        VideoInfo.nalBuffers[this.putNum].setNalLen(0);
                        this.preSeq=seqNum;
                        Log.i("GetNal", "!!!!!!!!!MAIN FRAME !!!Lost Num = " + e + "*****" + "this Packet is" + seqNum);
                        this.state = 0;
                        Log.i("RTPlost", "******* flag chaged !!!!!****=" + flag);
                    }
                }
            }else {
                this.tempNal=new byte[100000];
                tempNal[0] = H264_STREAM_HEAD[0];
                tempNal[1] = H264_STREAM_HEAD[1];
                tempNal[2] = H264_STREAM_HEAD[2];
                tempNal[3] = H264_STREAM_HEAD[3];
                try {
                    System.arraycopy(data,0,this.tempNal,4,len);
                } catch (Exception e1) {
                    Log.d("RTPPackets", "System.arraycopy failed!");
                }

                VideoInfo.nalBuffers[this.putNum].setNalLen(len+4);
                this.preSeq=seqNum;
                VideoInfo.nalBuffers[this.putNum].isWriteable();
                System.arraycopy(this.tempNal, 0, VideoInfo.nalBuffers[this.putNum].
                        getWriteable_Nalbuf(), 0, VideoInfo.nalBuffers[this.putNum].getNalLen());
//                copyFromTempToNal();
                this.info_NALU = Integer.toString(this.putNum);
                this.info_NALU = this.info_NALU + "Write Nalu done!";
                Log.i("NALUWrite", this.info_NALU);
                VideoInfo.nalBuffers[this.putNum].writeLock();
                ++this.putNum;
                if(this.putNum == 200) {
                    this.putNum = 0;
                }
                this.state = 0;
                this.headseqnum = seqNum;
                this.subduction = this.headseqnum - this.tailseqnum;
                Log.i("GetNal", "@@@START............Single packet@@@now the Nalseqnum is =" + seqNum);
                Log.i("GetNal", "^^^^^head - tail=" + this.subduction);
                this.tailseqnum = seqNum;
            }
        }else if((data[0]&31)==28){
            if((data[1]&0xe0)==0x80){
                this.tempNal=new byte[100000];
                tempNal[0] = H264_STREAM_HEAD[0];
                tempNal[1] = H264_STREAM_HEAD[1];
                tempNal[2] = H264_STREAM_HEAD[2];
                tempNal[3] = H264_STREAM_HEAD[3];
                try {
                    System.arraycopy(data,2,this.tempNal,4,len-2);
                } catch (Exception e1) {
                    Log.d("RTPPackets", "System.arraycopy failed!");
                }

                VideoInfo.nalBuffers[this.putNum].setNalLen(len+2);
                this.preSeq=seqNum;
                this.headseqnum=seqNum;
                this.subduction=this.headseqnum-this.tailseqnum;
                Log.i("GetNal", "@@@MAIN START@@@         now the Nalseqnum is =" + seqNum);
                Log.i("GetNal", "^^^^^head - tail=" + this.subduction);
            }
            else if(state==1) {
                if (this.preSeq + 1 == seqNum) {
                    if ((data[1] & 0xe0) == 0x40) {
                        try {
                            System.arraycopy(data, 2, this.tempNal,
                                    VideoInfo.nalBuffers[this.putNum].getNalLen(), len - 2);
                        } catch (Exception e1) {
                            Log.d("RTPPackets", "System.arraycopy failed!");
                        }

                        VideoInfo.nalBuffers[this.putNum].addNalLen(len - 2);
                        this.preSeq = seqNum;
                        VideoInfo.nalBuffers[this.putNum].isWriteable();
                        System.arraycopy(this.tempNal, 0, VideoInfo.nalBuffers[this.putNum].getWriteable_Nalbuf(),
                                0, VideoInfo.nalBuffers[this.putNum].getNalLen());
                        this.info_NALU = Integer.toString(this.putNum);
                        this.info_NALU = this.info_NALU + "Write Nalu done!";
                        Log.d("NALUWrite", this.info_NALU);
                        VideoInfo.nalBuffers[this.putNum].writeLock();
                        ++this.putNum;
                        if (this.putNum == 200) {
                            this.putNum = 0;
                        }
                        this.tailseqnum = seqNum;
                        Log.i("GetNal", "###MAIN LAST###         now the Nalseqnum is =" + seqNum);
                    } else {
                        try {
                            System.arraycopy(data, 2, this.tempNal, VideoInfo.nalBuffers[this.putNum].getNalLen(), len - 2);
                        } catch (Exception e1) {
                            Log.d("RTPPackets", "System.arraycopy failed!");
                        }
                        VideoInfo.nalBuffers[this.putNum].addNalLen(len - 2);
                        this.preSeq = seqNum;
                    }
                }else {
                    e=seqNum=this.preSeq;
                    flag=1;
                    this.tempNal=new byte[100000];
                    VideoInfo.nalBuffers[this.putNum].setNalLen(0);
                    this.preSeq=seqNum;
                    Log.i("GetNal", "!!!!!!!!!MAIN FRAME !!!Lost Num = " + e + "*****" + "this Packet is" + seqNum);
                }
            }else {
                e = seqNum - this.preSeq;
                flag = 1;
                this.tempNal = new byte[100000];
                VideoInfo.nalBuffers[this.putNum].setNalLen(0);
                this.preSeq = seqNum;
                Log.i("GetNal", "!!!!!!!!!MAIN FRAME !!!Lost Num = " + e + "*****" + "this Packet is" + seqNum);
                this.state = 0;
                Log.i("RTPlost", "******* flag chaged !!!!!****=" + flag);
            }
        }else {
            this.tempNal=new byte[100000];
            tempNal[0] = H264_STREAM_HEAD[0];
            tempNal[1] = H264_STREAM_HEAD[1];
            tempNal[2] = H264_STREAM_HEAD[2];
            tempNal[3] = H264_STREAM_HEAD[3];
            try {
                System.arraycopy(data,0,this.tempNal,4,len);
            } catch (Exception e1) {
                Log.d("RTPPackets", "System.arraycopy failed!");
            }

            VideoInfo.nalBuffers[this.putNum].setNalLen(len+4);
            this.preSeq=seqNum;
            VideoInfo.nalBuffers[this.putNum].isWriteable();
            System.arraycopy(this.tempNal, 0, VideoInfo.nalBuffers[this.putNum].
                    getWriteable_Nalbuf(), 0, VideoInfo.nalBuffers[this.putNum].getNalLen());
            this.info_NALU = Integer.toString(this.putNum);
            this.info_NALU = this.info_NALU + "Write Nalu done!";
            Log.d("NALUWrite", this.info_NALU);
            VideoInfo.nalBuffers[this.putNum].writeLock();
            ++this.putNum;
            if(this.putNum == 200) {
                this.putNum = 0;
            }
            this.state = 0;
            this.headseqnum = seqNum;
            this.subduction = this.headseqnum - this.tailseqnum;
            Log.d("GetNal", "@@@START............Single packet@@@now the Nalseqnum is =" + seqNum);
            Log.d("GetNal", "^^^^^head - tail=" + this.subduction);
            this.tailseqnum = seqNum;
        }
    }

    public void getNalDm365(byte[] data, int seqNum, int len) {
        switch (frameParseDm365(data)) {
            case 0://单包
                addCompleteRtpPacketToTemp(data, seqNum, len);
                copyFromTempToNal();
                isPacketLost = false;
                break;
            case 1://首包
                Log.d("收到首包","hahaha 1111");
                addFirstRtpPacketToTemp(data, seqNum, len);
                isPacketLost = false;
                break;
            case 2://中包
                if (!isPacketLost) {
                    if (preSeq + 1 == seqNum) {
                        addMiddleRtpPacketToTemp(data, seqNum, len);
                    } else {
                        Log.e("RtpVideo", "数据丢包");
                        jumpNal(seqNum);
                    }
                }
                break;
            case 3://尾包
                if (!isPacketLost) {
                    if (preSeq + 1 == seqNum) {
                        addLastRtpPacketToTemp(data, seqNum, len);
                        copyFromTempToNal();
                    } else {
                        jumpNal(seqNum);
                    }
                } else {
                    Log.e("RtpVideo", "数据丢包");
                    isPacketLost = false;
                }
                break;
        }
    }

    public int frameParseDm365(byte[] data) {
        if ((data[0] & 0x1f) == 28 || (data[0] & 0x1f) == 29) {//先判断是否是分片Type=28：FU-A
            int i=data[1]&0xe0;
            if ((data[1] & 0xe0) == 0x80) {
                Log.d("收到首包","hahaha 111");
                return 1;//分片首包S = 1；E = 0；R = 0；
            } else if ((data[1] & 0xe0) == 0x00) {
                return 2;//分片中包：S = 0；E = 0；R = 0；
            } else {
                return 3;//分片末包S = 0；E = 1；R = 0；
            }
        } else {//不是分片
            return 0;//单包
        }
    }

    private void addCompleteRtpPacketToTemp(byte[] data, int seqNum, int len) {
        tempNal = new byte[1000000];
        tempNal[0] = H264_STREAM_HEAD[0];
        tempNal[1] = H264_STREAM_HEAD[1];
        tempNal[2] = H264_STREAM_HEAD[2];
        tempNal[3] = H264_STREAM_HEAD[3];
        System.arraycopy(data, 0, tempNal, 4, len);
        tempNalLen = len + 4;
        preSeq = seqNum;
    }

    private void addFirstRtpPacketToTemp(byte[] data, int seqNum, int len) {
        tempNal = new byte[1000000];
        tempNal[0] = H264_STREAM_HEAD[0];
        tempNal[1] = H264_STREAM_HEAD[1];
        tempNal[2] = H264_STREAM_HEAD[2];
        tempNal[3] = H264_STREAM_HEAD[3];
        System.arraycopy(data, 2, tempNal, 4, len - 2);
        tempNalLen = len + 2;
        preSeq = seqNum;
    }

    private void addMiddleRtpPacketToTemp(byte[] data, int seqNum, int len) {
        System.arraycopy(data, 2, tempNal, tempNalLen, len - 2);
        tempNalLen += len - 2;
        preSeq = seqNum;
    }

    private void addLastRtpPacketToTemp(byte[] data, int seqNum, int len) {
        System.arraycopy(data, 2, tempNal, tempNalLen, len - 2);
        tempNalLen += len - 2;
        preSeq = seqNum;
    }

    private void copyFromTempToNal() {
        VideoInfo.nalBuffers[putNum].setNalBuf(tempNal, tempNalLen);
        VideoInfo.nalBuffers[putNum].writeLock();
        Log.d("rtp", "nalLen:" + tempNalLen);
        putNum++;
        if (putNum == 200) {
            putNum = 0;
        }
    }

    private void jumpNal(int seqNum) {
        VideoInfo.nalBuffers[putNum].writeLock();
        preSeq = seqNum;
        isPacketLost = true;
        putNum++;
        if (putNum == 200) {
            putNum = 0;
        }
    }
    private void handleLastPacket(byte data[],int seqNum,int len){
        try {
            System.arraycopy(data, 2, this.tempNal,
                    VideoInfo.nalBuffers[this.putNum].getNalLen(), len - 2);
        } catch (Exception e1) {
            Log.d("RTPPackets", "System.arraycopy failed!");
        }

        VideoInfo.nalBuffers[this.putNum].addNalLen(len - 2);
        this.preSeq = seqNum;
        VideoInfo.nalBuffers[this.putNum].isWriteable();
        System.arraycopy(this.tempNal, 0, VideoInfo.nalBuffers[this.putNum].getWriteable_Nalbuf(),
                0, VideoInfo.nalBuffers[this.putNum].getNalLen());
        this.info_NALU = Integer.toString(this.putNum);
        this.info_NALU = this.info_NALU + "Write Nalu done!";
        Log.d("NALUWrite", this.info_NALU);
        VideoInfo.nalBuffers[this.putNum].writeLock();
        ++this.putNum;
        if (this.putNum == 200) {
            this.putNum = 0;
        }
        this.tailseqnum = seqNum;
        Log.i("GetNal", "###MAIN LAST###         now the Nalseqnum is =" + seqNum);
        this.state=0;
    }
    private void handleConpletePacket(byte data[],int seqNum,int len){
        this.tempNal=new byte[100000];
        tempNal[0] = H264_STREAM_HEAD[0];
        tempNal[1] = H264_STREAM_HEAD[1];
        tempNal[2] = H264_STREAM_HEAD[2];
        tempNal[3] = H264_STREAM_HEAD[3];
        try {
            System.arraycopy(data,0,this.tempNal,4,len);
        } catch (Exception e1) {
            Log.d("RTPPackets", "System.arraycopy failed!");
        }

        VideoInfo.nalBuffers[this.putNum].setNalLen(len+4);
        this.preSeq=seqNum;
        VideoInfo.nalBuffers[this.putNum].isWriteable();
        System.arraycopy(this.tempNal, 0, VideoInfo.nalBuffers[this.putNum].
                getWriteable_Nalbuf(), 0, VideoInfo.nalBuffers[this.putNum].getNalLen());
        this.info_NALU = Integer.toString(this.putNum);
        this.info_NALU = this.info_NALU + "Write Nalu done!";
        Log.d("NALUWrite", this.info_NALU);
        VideoInfo.nalBuffers[this.putNum].writeLock();
        ++this.putNum;
        if(this.putNum == 200) {
            this.putNum = 0;
        }
        this.state = 0;
        this.headseqnum = seqNum;
        this.subduction = this.headseqnum - this.tailseqnum;
        Log.d("GetNal", "@@@START............Single packet@@@now the Nalseqnum is =" + seqNum);
        Log.d("GetNal", "^^^^^head - tail=" + this.subduction);
        this.tailseqnum = seqNum;
    }
    private Boolean isSpsAndPps(byte[] data,int seqNum,int len){
        boolean isSpsAndPps=false;
        if(((data[4]&31)==7)&&((data[0]&0xff)==0)&&((data[1]&0xff)==0)
                &&((data[2]&0xff)==0)&&((data[3]&0xff)==1)){
            isSpsAndPps=true;
        }
        return isSpsAndPps;
    }
}
