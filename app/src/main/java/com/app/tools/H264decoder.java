package com.app.tools;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by asus on 2017/6/16.
 */
//硬编码
public class H264decoder {
    private static final String TAG = "H264decoder";
    private MediaCodec mCodec;
    private final static String MIME_TYPE = "video/avc"; // H.264 Advanced Video
//    private final static int VIDEO_WIDTH = 1280;
//    private final static int VIDEO_HEIGHT = 720;
        private final static int VIDEO_WIDTH = 352;
    private final static int VIDEO_HEIGHT = 288;
    private final static int TIME_INTERNAL = 10;
    private final static int HEAD_OFFSET=512;
    private long BUFFER_TIMEOUT = 0;
    int mCount = 0;

    public void initDecoder(Surface surface) {

        try {
            mCodec = MediaCodec.createDecoderByType(MIME_TYPE);
            Log.d("陈工","1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE,
                VIDEO_WIDTH, VIDEO_HEIGHT);
        Log.d("陈工","2");
//        byte[] header_sps = new byte[]{0x00, 0x00, 0x00, 0x01, 0x67, 0x42, (byte) 0x80, 0x1F, (byte) 0xDA, 0x01, 0x40, 0x16, (byte) 0xE8, (byte) 0x06, (byte) 0xD0, (byte) 0xA1, (byte) 0x35};
//        byte[] header_pps = new byte[]{0x00, 0x00, 0x00, 0x01, 0x68, (byte) 0xCE, 0x06, (byte) 0xE2};
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        Log.d("陈工","3");
//        mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
//        mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
        mCodec.configure(mediaFormat, surface, null, 0);
        Log.d("陈工","4");
        mCodec.start();
        Log.d("陈工","5");
    }

    public boolean onFrame(byte[] buf, int offset, int length) {
        // Get input buffer index
        ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
        int inputBufferIndex = mCodec.dequeueInputBuffer(-1);
//        int inputBufferIndex = mCodec.dequeueInputBuffer(BUFFER_TIMEOUT);

        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(buf, offset, length);
            mCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount
                    * TIME_INTERNAL, 0);
            mCount++;
        } else {
            return false;
        }

        // Get output buffer index
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
//        int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, BUFFER_TIMEOUT);
        //循环解码，知道数据全部解码完成
        while (outputBufferIndex >= 0) {
            mCodec.releaseOutputBuffer(outputBufferIndex, true);
            //释放缓冲区的时候需要注意第二个参数设置为true，表示解码显示在Surface上
            outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
        Log.e("Media", "onFrame end");
        return true;
    }
}
