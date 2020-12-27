package com.punuo.sys.app.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.TextView;

import com.punuo.sys.app.activity.ActivityLifeCycle;


/**
 * Created by han.chen.
 * Date on 2019/4/2.
 * debug模式下捕捉未处理的异常
 **/
public class DebugCrashHandler implements Thread.UncaughtExceptionHandler {

    private static DebugCrashHandler sInstance = null;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static final int CAUSE_COUNT = 3;

    public static DebugCrashHandler getInstance() {
        if (sInstance == null) {
            synchronized (DebugCrashHandler.class) {
                if (sInstance == null) {
                    synchronized (DebugCrashHandler.class) {
                        sInstance = new DebugCrashHandler();
                    }
                }
            }
        }
        return sInstance;
    }

    private DebugCrashHandler() {
    }

    /**
     * 初始化默认异常捕获
     */
    public void init() {
        // 获取默认异常处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 将此类设为默认异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Activity activity = ActivityLifeCycle.getInstance().getCurrentActivity();
        try {
            if (activity != null) {
                handleException(t, activity, e);
            } else {
                if (!DeviceHelper.isApkInDebug()) {
                    if (mDefaultHandler != null) {
                        mDefaultHandler.uncaughtException(t, e);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 是否人为捕获异常
     *
     * @param e Throwable
     * @return true:已处理 false:未处理
     */
    private boolean handleException(final Thread thread, final Activity activity, final Throwable
            e) {
        if (e == null) {// 异常是否为空
            return false;
        }
        new Thread() {// 在主线程中弹出提示
            @Override
            public void run() {
                Looper.prepare();
                String stackTrace = DebugCrashHandler.this.getStackTrace(e);
                try {
                    showDialog(stackTrace, activity, thread, e);
                } catch (Exception e) {// 弹对话框失败就不弹
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }.start();
        return true;
    }

    private void showDialog(String message, final Activity activity, final Thread thread, final
    Throwable ex) {
        AlertDialog dialog = new AlertDialog.Builder(activity).setTitle("出错了")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        mDefaultHandler.uncaughtException(thread, ex);
                        dialog.dismiss();
                    }
                }).create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // do nothing
            }
        });
        dialog.show();

        //  activity.getIntent().
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = StatusBarUtil.getWidth(activity);
        lp.height = StatusBarUtil.getHeight(activity);
        dialog.getWindow().setAttributes(lp);
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        if (!DeviceHelper.isApkInDebug()) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    private String getStackTrace(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        int count = 0;

        while (ex != null && count < CAUSE_COUNT) {
            sb.append("app crash Cause by ")
                    .append(ex.getClass().getSimpleName())
                    .append("|")
                    .append(ex.getLocalizedMessage())
                    .append("|");
            for (int i = 0; i < ex.getStackTrace().length; i++) {
                sb.append(ex.getStackTrace()[i].toString())
                        .append("|");
            }
            sb.append("|");
            ex = ex.getCause();
            count++;
        }
        return sb.toString();
    }
}
