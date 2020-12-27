package com.punuo.sys.app.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019/4/2.
 **/
public class StatusBarUtil {
    public static void setStatusBarColor(Activity activity, int color) {
        if (activity == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //设置状态栏颜色
                window.setStatusBarColor(color);
            }
        } catch (Exception pE) {
            pE.printStackTrace();
        }
    }

    /**
     * 设置状态栏颜色
     *
     * @param colorString 类似 #RRGGBB 或者 #AARRGGBB.
     */
    public static void setStatusBarColor(Activity activity, String colorString) {
        try {
            setStatusBarColor(activity, Color.parseColor(colorString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getDefaultStatusBarColor(Activity pActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return pActivity.getWindow().getStatusBarColor();
        }
        return Color.WHITE;
    }

    public static void translucentStatusBar(Activity activity, int color, boolean dark) {
        if (activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //乐视手机 View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 设置不生效，导致状态栏背景与文本颜色一样
            if ("Letv".equalsIgnoreCase(Build.MANUFACTURER) && color == Color.WHITE) {
                color = Color.parseColor("#e4e4e4");
            }
            window.setStatusBarColor(color);
            if (dark) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            setMiuiStatusBarDarkMode(activity, dark);

            ViewGroup contentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
            View childView = contentView.getChildAt(0);
            if (childView != null) {
                ViewCompat.setFitsSystemWindows(childView, false);
                ViewCompat.requestApplyInsets(childView);
            }
        }
    }

    /**
     * 修改MIUI系统状态栏的模式，在状态栏颜色为深色时，设置isDarkMode为false，使状态栏的文字和图标都为浅色，
     * 设置isDarkMode为true时，使状态栏的文字和图标都为深色
     *
     * @param activity   当前的activity
     * @param isDarkMode 是否为深色
     * @return 是否设置成功
     */
    private static boolean setMiuiStatusBarDarkMode(Activity activity, boolean isDarkMode) {

        if (!isMIUIOS() || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        boolean result = false;
        if (activity != null) {
            Class<? extends Window> clazz = activity.getWindow().getClass();
            try {
                int darkModeFlag;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(activity.getWindow(), isDarkMode ? darkModeFlag : 0, darkModeFlag);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static boolean isMIUIOS() {
        List<String> resultList = CommandUtil.execute("getprop ro.miui.ui.version.name");
        if (resultList != null && resultList.size() > 0 && !TextUtils.isEmpty(resultList.get(0))) {
            return true;
        }
        return false;
    }

    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    public static int getWidth(Context context) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getHeight(Context context) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
}
