package com.punuo.sys.app.util;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class ViewUtil {

    public static void setText(TextView view, String text) {
        if (view == null) {
            return;
        }
        if (TextUtils.isEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setText(text);
        }
    }
}
