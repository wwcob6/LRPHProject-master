package com.punuo.sys.app.httplib.upload;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2019/5/27.
 **/
public class UploadResult {

    @SerializedName("msg")
    public String msg;

    @SerializedName("tip")
    public String tip;

    @SerializedName("avatar")
    public String avatar;

    public boolean isSuccess() {
        return TextUtils.equals("success", msg);
    }
}
