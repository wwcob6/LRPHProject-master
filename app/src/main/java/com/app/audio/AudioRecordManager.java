package com.app.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.app.tools.AECManager;

/**
 * Created by han.chen.
 * Date on 2019-07-22.
 **/
public class AudioRecordManager {
    private static AudioRecordManager sAudioRecordManager;
    public static AudioRecordManager getInstance() {
        synchronized (AudioRecordManager.class) {
            if (sAudioRecordManager == null) {
                sAudioRecordManager = new AudioRecordManager();
            }
        }
        return sAudioRecordManager;
    }
    private int mRecorderBufferSize;
    private AudioRecord mAudioRecord;
    private AudioTrack mAudioTrack;
    private int mAudioSessionId = -1;
    private boolean isRecordInit = false;
    private boolean isTrackInit = false;

    AudioRecordManager() {
        mRecorderBufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        initAudioRecord();
        initAudioTrack();
        initAEC();
    }

    private void initAudioRecord() {
        mAudioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,//the recording source
                8000, //采样频率，一般为8000hz/s
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                mRecorderBufferSize);
        mAudioSessionId = mAudioRecord.getAudioSessionId();
        isRecordInit = true;
    }

    private void initAudioTrack() {
        if (mAudioSessionId == -1) {
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    mRecorderBufferSize * 2
                    , AudioTrack.MODE_STREAM);
        } else {
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    mRecorderBufferSize * 2
                    , AudioTrack.MODE_STREAM, mAudioSessionId);
        }
    }

    private void initAEC() {
        if (AECManager.isDeviceSupport()) {
            AECManager.getInstance().initAEC(mAudioSessionId);
        }
    }

    public void startRecording() {
        if (!isRecordInit) {
            initAudioRecord();
        }
        mAudioRecord.startRecording();
    }

    public int read(short[] audioData, int i, int size) {
        return mAudioRecord.read(audioData, i, size);
    }

    public void stopRecording() {
        mAudioRecord.stop();
    }

    public void release() {
        mAudioRecord.release();
        isRecordInit = false;
    }

    public void write(short[] audioData, int i, int size) {
        mAudioTrack.write(audioData, i, size);
    }

    public void play() {
        if (!isTrackInit) {
           initAudioTrack();
        }
        mAudioTrack.play();
    }

    public void stop() {
        mAudioTrack.stop();
        isTrackInit = false;
    }
}
