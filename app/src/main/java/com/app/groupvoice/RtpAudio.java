package com.app.groupvoice;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import com.app.tools.AECManager;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

import jlibrtp.DataFrame;
import jlibrtp.Participant;
import jlibrtp.RTPAppIntf;
import jlibrtp.RTPSession;

/**
 * Created by chenblue23 on 2016/7/5.
 */
public class RtpAudio implements RTPAppIntf {
    private static final int SAMPLE_RATE = 8000;
    private static final int FRAME_SIZE = 160;
    private RTPSession rtpSession;
    private DatagramSocket rtpSocket;
    public static AudioTrack track;
    private boolean isPttOn = false;
    private boolean isEncoding = false;
    private Participant p;

    public RtpAudio(String networkAddress, int remoteRtpPort) throws SocketException {
        rtpSocket = new DatagramSocket();
        rtpSession = new RTPSession(rtpSocket, null);
        rtpSession.RTPSessionRegister(this, null, null);
        p = new Participant(networkAddress, remoteRtpPort, remoteRtpPort + 1);
        Log.d("111", "RtpAudio: " + remoteRtpPort);
        rtpSession.addParticipant(p);
        rtpSession.naivePktReception(false);
        rtpSession.frameReconstruction(false);
        rtpSession.payloadType(8);
        int min = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                min,
                AudioTrack.MODE_STREAM
        );
        track.play();
    }

    @Override
    public void receiveData(DataFrame frame, Participant participant) {
        if (!isPttOn) {
            short[] audioData = new short[FRAME_SIZE];
            byte[] audioBuffer = frame.getConcatenatedData();
            if (audioBuffer.length == 160) {
                G711.alaw2linear(audioBuffer, audioData, FRAME_SIZE);
                track.write(audioData, 0, FRAME_SIZE);
            }
        }
    }

    @Override
    public void userEvent(int type, Participant[] participant) {

    }

    //移除当前监听的端口
    public void removeParticipant() {
        rtpSession.removeParticipant(p);
    }

    //更改监听的端口
    public void changeParticipant(String networkAddress, int remoteRtpPort) throws SocketException {
        removeParticipant();
        rtpSocket = new DatagramSocket();
        rtpSession = new RTPSession(rtpSocket, null);
        rtpSession.RTPSessionRegister(this, null, null);
        p = new Participant(networkAddress, remoteRtpPort, remoteRtpPort + 1);
        rtpSession.addParticipant(p);
        rtpSession.naivePktReception(false);
        rtpSession.frameReconstruction(false);
        rtpSession.payloadType(8);
    }

    @Override
    public int frameSize(int payloadType) {
        return 1;
    }

    public void pttChanged(boolean isPttOn) {
        this.isPttOn = isPttOn;
        if (isPttOn) {
            track.stop();
        } else {
            track.play();
        }
    }

    private Runnable G711_encode = new Runnable() {
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
            //标准音乐播放使用优先级
            int min = AudioRecord
                    .getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC,//麦克风数据
                    SAMPLE_RATE,//采样频率
                    AudioFormat.CHANNEL_IN_MONO,//单声道
                    AudioFormat.ENCODING_PCM_16BIT,//16bit采样精度
                    min);//内部记录缓冲区大小

            if(AECManager.isDeviceSupport()){
                AECManager.getInstance().initAEC(record.getAudioSessionId());
            }
            record.startRecording();
            short[] audioData = new short[FRAME_SIZE];
            byte[] encodeData = new byte[FRAME_SIZE];
            int numRead = 0;
            long timestamp = new Random().nextInt();
            timestamp = Math.abs(timestamp);
            while (isEncoding) {
                numRead = record.read(audioData, 0, FRAME_SIZE);
                if (numRead <= 0) continue;
                G711.linear2alaw(audioData, 0, encodeData, numRead);
                rtpSession.sendData(encodeData, timestamp);
                timestamp += 160;
            }
            record.stop();
            record.release();
            AECManager.getInstance().release();
        }
    };

    public void setEncoding(boolean encoding) {
        isEncoding = encoding;
        if (encoding) {
            new Thread(G711_encode).start();
        }
    }

    public void sendActiveMsg(byte[] msg) {
        rtpSession.sendData(msg);
    }
}
