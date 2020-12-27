package com.app;

import android.content.Context;
import android.text.TextUtils;

import com.app.model.PNUserInfo;
import com.app.request.GetUserInfoRequest;
import com.app.sip.SipInfo;
import com.punuo.sys.app.PnApplication;
import com.punuo.sys.app.httplib.HttpManager;
import com.punuo.sys.app.httplib.JsonUtil;
import com.punuo.sys.app.httplib.RequestListener;
import com.punuo.sys.app.util.PreferenceUtils;

import org.greenrobot.eventbus.EventBus;

public class UserInfoManager {

    /**
     * 保存Preference的name
     */
    private static final String PREF_KEY_USER = "pn_pref_user";
    private static final String PREF_KEY_SESSION = "pn_pref_session";
    private static UserInfoManager sUserInfoManager;
    private static String sSession = "";
    private static PNUserInfo.UserInfo sUserInfo;

    private UserInfoManager() {
    }

    /**
     * 单例模式，获取instance实例
     *
     * @return
     */
    public static UserInfoManager getInstance() {
        if (sUserInfoManager == null) {
            sUserInfoManager = new UserInfoManager();
        }
        return sUserInfoManager;
    }

    public static void setUserInfo(PNUserInfo.UserInfo userInfo) {
        final Context context = PnApplication.getInstance();
        if (userInfo != null) {
            sUserInfo = userInfo;
            PreferenceUtils.setString(context, PREF_KEY_USER, JsonUtil.toJson(userInfo));
            EventBus.getDefault().post(userInfo);
        }
    }

    public static PNUserInfo.UserInfo getUserInfo() {
        if (sUserInfo == null) {
            final Context context = PnApplication.getInstance();
            sUserInfo = JsonUtil.fromJson(PreferenceUtils.getString(context, PREF_KEY_USER), PNUserInfo.UserInfo.class);
            sUserInfo = sUserInfo == null? new PNUserInfo.UserInfo() : sUserInfo;
        }
        return sUserInfo;
    }

    public static void setSession(String session) {
        sSession = session;
        PreferenceUtils.setString(PnApplication.getInstance(), PREF_KEY_SESSION, session);
    }

    public static String getSession() {
        if (TextUtils.isEmpty(sSession)) {
            sSession = PreferenceUtils.getString(PnApplication.getInstance(), PREF_KEY_SESSION);
        }
        return sSession;
    }

    /**
     * 退出登录时清空用户信息
     */
    public static void clearUserData() {
        sSession = "";
        sUserInfo = null;
        PreferenceUtils.removeData(PnApplication.getInstance(), PREF_KEY_SESSION);
        PreferenceUtils.removeData(PnApplication.getInstance(), PREF_KEY_USER);
    }

    public GetUserInfoRequest mGetUserInfoRequest;
    public void refreshUserInfo(RequestListener listener) {
        if (mGetUserInfoRequest != null && !mGetUserInfoRequest.isFinish()) {
            return;
        }
        mGetUserInfoRequest = new GetUserInfoRequest();
        mGetUserInfoRequest.addUrlParam("userid", SipInfo.userId);
        mGetUserInfoRequest.setRequestListener(listener);
        HttpManager.addRequest(mGetUserInfoRequest);
    }

    public void refreshUserInfo() {
        refreshUserInfo(new RequestListener<PNUserInfo>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNUserInfo result) {
                if (result == null) {
                    return;
                }
                setUserInfo(result.userInfo);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

}
