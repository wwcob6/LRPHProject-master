package com.punuo.sys.app.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by han.chen.
 * Date on 2019-06-05.
 **/
public class CompletedFooter {

    public static final String TAG = "CompletedFooter";

    private Context mContext;
    private Fragment mFragment;
    private CompletedFooterListener mCompletedFooterListener;
    private static Map<String, CompletedFooterListener> sCompletedFooterMap = new HashMap<>();

    public CompletedFooter(Context context) {
        mContext = context;
        mCompletedFooterListener = getCompletedFooterListener();
    }

    public CompletedFooter(Fragment fragment) {
        mFragment = fragment;
        mCompletedFooterListener = getCompletedFooterListener();
    }

    public boolean isCompletedFooterEnabled() {
        return mCompletedFooterListener!= null && mCompletedFooterListener.enableFooter();
    }

    public View generateCompletedFooterView(Context context, ViewGroup parent) {
        if (mCompletedFooterListener == null) {
            return null;
        }
        return mCompletedFooterListener.generateCompletedFooterView(context, parent);
    }

    public void setCompletedFooterListener(CompletedFooterListener listener) {

        mCompletedFooterListener = listener;
    }

    /*
        每条业务线的CompletedFooterListener都放到了sCompletedFooterMap,
        可以在BeibeiApp中将业务线listener加入到map中
        加入之后，其他地方就不会再覆盖它了，防止被别人改错
    */
    public static void addBusinessCompletedFooter(String name, CompletedFooterListener listener) {
        if (!TextUtils.isEmpty(name) && sCompletedFooterMap != null && !sCompletedFooterMap.containsKey(name)) {
            sCompletedFooterMap.put(name, listener);
        }
    }

    private CompletedFooterListener getCompletedFooterListener() {

        // 如果还没设，则尝试取业务线统一的CompletedFooterListener
        if (mCompletedFooterListener == null) {
            return getBusinessCompletedFooter();
        } else {
            return mCompletedFooterListener;
        }
    }

    private CompletedFooterListener getBusinessCompletedFooter() {
        String name;

        if (mFragment != null) {
            name = mFragment.getClass().getPackage().getName();
        } else if (mContext instanceof Activity) {
            name = mContext.getClass().getPackage().getName();
        } else {
            return null;
        }

        // 业务线名称key必须包含在Activity的全路径名称中，我们可以用包名来做key，如c2c，pintuan。注意大小写
        // 此判断逻辑可以进一步优化
        if (TextUtils.isEmpty(name)) {
            Log.d(TAG, "activity class name get failed!");
            return null;
        }

        for (String key : sCompletedFooterMap.keySet()) {
            if (name.contains(key)) {
                return sCompletedFooterMap.get(key);
            }
        }

        return null;
    }

    public interface CompletedFooterListener {

        boolean enableFooter();

        View generateCompletedFooterView(Context context, ViewGroup parent);
    }
}
