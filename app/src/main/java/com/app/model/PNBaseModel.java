package com.app.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2019/5/28.
 **/
public class PNBaseModel {
    @SerializedName("msg")
    public String msg;

    public boolean isSuccess() {
        return TextUtils.equals("success", msg);
    }

    @SerializedName("tip")
    public String tip;
}
