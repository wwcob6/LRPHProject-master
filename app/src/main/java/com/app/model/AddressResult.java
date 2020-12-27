package com.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-06-03.
 **/
public class AddressResult extends PNBaseModel {

    @SerializedName("address")
    public List<AddressItem> mAddressItems;
}
