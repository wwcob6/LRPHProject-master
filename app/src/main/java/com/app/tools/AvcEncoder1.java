package com.app.tools;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import tech.shutu.jni.YuvUtils;

/**
 * Created by asus on 2017/6/15.
 */

public class AvcEncoder1 {

    //编码类型
    private String mime = "video/avc";
    private static final String TAG = "AvcEncoder";
    private MediaCodec mediaCodec;
    private BufferedOutputStream outputStream;
    private byte[] yuv420 = null;
    public byte[] outPut=null;
    private byte[] rotateYuv420 = null;
    private final int previewWidth = 640;     //水平像素
    private final int previewHeight = 480;     //垂直像素
//    private final int previewWidth = 320;     //水平像素
//    private final int previewHeight = 240;     //垂直像素

    private long BUFFER_TIMEOUT = 0;
    YuvUtils yuvPic=new YuvUtils();
    public AvcEncoder1() {
        //输出到本地
        File f = new File(Environment.getExternalStorageDirectory(), "DCIM/video_encoded.264");
//        yuv420 = new byte[previewWidth*previewHeight*3/2];
        yuv420 = new byte[getYuvBuffer(previewWidth,previewHeight)];

        rotateYuv420 = new byte[getYuvBuffer(previewWidth, previewHeight)];

        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(f));
            Log.i("AvcEncoder", "outputStream initialized");
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            mediaCodec = MediaCodec.createEncoderByType(mime);//创建编码器
        } catch (IOException e) {
            e.printStackTrace();
        }

        //确定当前MediaCodec支持的图像格式
//        int colorFormat = selectColorFormat(selectCodec(mime), mime);

        YuvUtils.allocateMemo(getYuvBuffer(previewWidth,previewHeight),0,getYuvBuffer(previewWidth,previewHeight));
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(mime,previewWidth, previewHeight);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,previewWidth*previewHeight*8);//设置比特率
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);//设置帧率
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar  );//设置颜色格式
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, chooseColorFormat());
//        Log.d(TAG,"colorFormat"+chooseColorFormat());
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
        //COLOR_FormatYUV420SemiPlanar  s9
        //COLOR_FormatYUV420Planar      s6
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);//设置关键帧
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        //四个参数，第一个是media格式，// 第二个是解码器播放的surfaceview，
        // 第三个是MediaCrypto，第四个是编码解码的标识
        mediaCodec.start();
}



    //选择颜色格式
    // choose the right supported color format. @see below:
    // https://developer.android.com/reference/android/media/MediaCodecInfo.html
    // https://developer.android.com/reference/android/media/MediaCodecInfo.CodecCapabilities.html
    private int chooseColorFormat() {
        MediaCodecInfo ci = null;

        int nbCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < nbCodecs; i++) {
            MediaCodecInfo mci = MediaCodecList.getCodecInfoAt(i);
            if (!mci.isEncoder()) {
                continue;
            }

            String[] types = mci.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mime)) {
                    //Log.i(TAG, String.format("encoder %s types: %s", mci.getName(), types[j]));
                    ci = mci;
                    break;
                }
            }
        }

        int matchedColorFormat = 0;
        MediaCodecInfo.CodecCapabilities cc = ci.getCapabilitiesForType(mime);
        for (int i = 0; i < cc.colorFormats.length; i++) {
            int cf = cc.colorFormats[i];
            //Log.i(TAG, String.format("encoder %s supports color fomart %d", ci.getName(), cf));

            // choose YUV for h.264, prefer the bigger one.
            if (cf >= cc.COLOR_FormatYUV411Planar && cf <= cc.COLOR_FormatYUV422SemiPlanar) {
                if (cf > matchedColorFormat) {
                    matchedColorFormat = cf;
                }
            }
        }

        Log.i(TAG, String.format("encoder %s choose color format %d", ci.getName(), matchedColorFormat));
        return matchedColorFormat;
    }

    //通过mimeType确定支持的格式
    private int selectColorFormat(MediaCodecInfo codecInfo, String mimeType) {
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            int colorFormat = capabilities.colorFormats[i];
            if (isRecognizedFormat(colorFormat)) {
//                return colorFormat;
                int j=colorFormat;
            }
        }
        Log.e(TAG, "couldn't find a good color format for " + codecInfo.getName() + " / " + mimeType);
        return 0;   // not reached
    }
    private boolean isRecognizedFormat(int colorFormat) {
        switch (colorFormat) {
            // these are the formats we know how to handle for this test
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                return true;
            default:
                return false;
        }
    }
    private MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }

            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }



    public void close() {
        try {
            mediaCodec.stop();
            mediaCodec.release();
            yuvPic.releaseMemo();
//            outputStream.flush();
//            outputStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    int mCount=0;
    //计算YUV的buffer的函数，得根据文档计算，而不是简单的3/2
    // for the buffer for YV12(android YUV), @see below:
    // https://developer.android.com/reference/android/hardware/Camera.Parameters.html#setPreviewFormat(int)
    // https://developer.android.com/reference/android/graphics/ImageFormat.html#YV12
    private int getYuvBuffer(int width, int height) {
        // stride = ALIGN(width, 16)
        int stride = (int)Math.ceil(width / 16.0) * 16;
        // y_size = stride * height
        int y_size = stride * height;
        // c_stride = ALIGN(stride/2, 16)
        int c_stride = (int)Math.ceil(width / 32.0) * 16;
        // c_size = c_stride * height/2
        int c_size = c_stride * height / 2;
        // size = y_size + c_size * 2
        return y_size + c_size * 2;
    }

    private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight)
    {
        byte [] yuv = new byte[imageWidth*imageHeight*3/2];
        // Rotate the Y luma
        int i = 0;
        for(int x = 0;x < imageWidth;x++)
        {
            for(int y = imageHeight-1;y >= 0;y--)
            {
                yuv[i] = data[y*imageWidth+x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth*imageHeight*3/2-1;
        for(int x = imageWidth-1;x > 0;x=x-2)
        {
            for(int y = 0;y < imageHeight/2;y++)
            {
                yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+x];
                i--;
                yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+(x-1)];
                i--;
            }
        }
        return yuv;
    }

    public byte[] cropYUV420(byte[] data,int imageW,int imageH,int newImageH){
        int cropH;
        int i,j,count,tmp;
        byte[] yuv =new byte[imageW*newImageH*3/2];
        cropH =(imageH - newImageH)/2;
        count =0;for(j=cropH;j<cropH+newImageH;j++){for(i=0;i<imageW;i++){
            yuv[count++]= data[j*imageW+i];}}
        //Cr Cb
        tmp = imageH+cropH/2;for(j=tmp;j<tmp + newImageH/2;j++){for(i=0;i<imageW;i++){
            yuv[count++]= data[j*imageW+i];}}
        return yuv;}




    // called from Camera.setPreviewCallbackWithBuffer(...) in other class
    public byte[] offerEncoder(byte[] input) {
        Log.i("AvcEncoder1", "offerEncoder1: ");
        byte[] dstYuv = new byte[getYuvBuffer(previewWidth,previewHeight)];
        byte[] dstYuv1 = new byte[previewWidth*previewHeight* 3 / 2];
//        //s9设备
//        yuv420=rotateYUVDegree270AndMirror(input,previewWidth,previewHeight);//湖面旋转镜像处理
//        NV21ToNV12(yuv420, rotateYuv420, previewWidth, previewHeight);
//        input=rotateYuv420;
//        s6设备
        swapYV12toI420(input,previewWidth,previewHeight);
        yuvPic.scaleAndRotateYV12ToI420(i420bytes,dstYuv,previewWidth,previewHeight,270,previewWidth,previewHeight);
        input=dstYuv;

        try {
            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(BUFFER_TIMEOUT);
//            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);//获取可使用缓冲区位置，得到索引
            //传入原始数据
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();//清除原来的内容以接收新内容
                inputBuffer.put(input);
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length,  mCount * 1000000 / 15, 0);
//                long timestamp = System.nanoTime();
//                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length,  timestamp, 0);
                mCount++;
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,0);
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,BUFFER_TIMEOUT);
            //获取可用输出缓冲区
            System.out.println("outputBufferIndex = " + outputBufferIndex);
            Log.i(TAG, "outputFirst");

            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
            outPut= new byte[bufferInfo.size];
            System.out.println("outData = " + outPut.length);

            outputBuffer.get(outPut);
            //输出到文件
            outputStream.write(outPut, 0, outPut.length);
            Log.i("AvcEncoder", outPut.length + " bytes written");
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);//释放缓冲区
            System.out.println("outputEnd");
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return outPut;
    }

    private void NV21ToNV12(byte[] nv21,byte[] nv12,int width,int height){
        if(nv21 == null || nv12 == null)return;
        int framesize = width*height;
        int i = 0,j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for(i = 0; i < framesize; i++){
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j-1] = nv21[j+framesize];
        }
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j] = nv21[j+framesize-1];
        }
    }

    private byte[] i420bytes = null;
    private byte[] swapYV12toI420(byte[] yv12bytes, int width, int height) {
        if (i420bytes == null)
            i420bytes = new byte[yv12bytes.length];
        for (int i = 0; i < width*height; i++)
            i420bytes[i] = yv12bytes[i];
        for (int i = width*height; i < width*height + (width/2*height/2); i++)
            i420bytes[i] = yv12bytes[i + (width/2*height/2)];
        for (int i = width*height + (width/2*height/2); i < width*height + 2*(width/2*height/2); i++)
            i420bytes[i] = yv12bytes[i - (width/2*height/2)];
        return i420bytes;
    }

}
