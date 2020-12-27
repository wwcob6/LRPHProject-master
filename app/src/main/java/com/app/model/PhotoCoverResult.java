package com.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoCoverResult extends PNBaseModel {
    @SerializedName("picInfo")
    public List<CloudPhotoCover> mCloudPhotoCover;
}
