package com.punuo.sys.app.util;

import android.content.Context;

/**
 * Created by maojianhui on 2018/12/13.
 */

public class ProviderUtil {
    public static String getFileProviderName(Context context){
        return context.getPackageName()+".provider";
    }
}
