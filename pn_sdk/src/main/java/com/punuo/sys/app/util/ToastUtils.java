package com.punuo.sys.app.util;

import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.punuo.sys.app.PnApplication;

public class ToastUtils {
	private static Toast mToast;
	public static void showToast(CharSequence text) {
		if (TextUtils.isEmpty(text)) return;

		try {
			if (mToast == null) {
				mToast = Toast.makeText(PnApplication.getInstance(), null, Toast.LENGTH_LONG);
				mToast.setGravity(Gravity.CENTER, 0, 0);
				mToast.setText(text);
			} else {
				mToast.setDuration(Toast.LENGTH_LONG);
				mToast.setText(text);
			}
			mToast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showToastShort(CharSequence text) {
		if (TextUtils.isEmpty(text)) return;

		try {
			if (mToast == null) {
				mToast = Toast.makeText(PnApplication.getInstance(), null, Toast.LENGTH_SHORT);
				mToast.setGravity(Gravity.CENTER, 0, 0);
				mToast.setText(text);
			} else {
				mToast.setDuration(Toast.LENGTH_SHORT);
				mToast.setText(text);
			}
			mToast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showToast(int resId) {
		showToast(PnApplication.getInstance().getResources().getText(resId));
	}

	public static void closeToast() {
		if (mToast != null) {
			mToast.cancel();
			mToast = null;
		}
	}
}
