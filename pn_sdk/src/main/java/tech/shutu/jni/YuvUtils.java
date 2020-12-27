package tech.shutu.jni;

import android.graphics.Bitmap;

/**
 * Created by raomengyang on 24/12/2016.
 */

public class YuvUtils {
    static {
        System.loadLibrary("yuv_utils");
        System.loadLibrary("yuv");
    }
    public static native void allocateMemo(int src_yuv_length, int src_argb_length, int dst_length);

    public static native void scaleAndRotateYV12ToI420(byte[] src_data, byte[] dst_data, int src_width, int src_height, int rotation, int dst_width, int dst_height);

    public static native void releaseMemo();

    public static native void NV12ToI420AndRotate(byte[] src_data, byte[] dst_data, int src_width, int src_height, int rotation, int dst_width, int dst_height);



}
