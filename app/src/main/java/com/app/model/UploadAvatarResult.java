package com.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2019/5/28.
 **/
public class UploadAvatarResult extends PNBaseModel {

    @SerializedName("avatar")
    public String avatar;
}
