package com.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CloudPhoto {
    @SerializedName("picInfo")
    public List<String> imageList;
}
