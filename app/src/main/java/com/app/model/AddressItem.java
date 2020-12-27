package com.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by maojianhui on 2019/3/20.
 */

public class AddressItem {
    @SerializedName("address_id")
    public String addressId;
    @SerializedName("userAddress")
    public String userAddress;
    @SerializedName("detailAddress")
    public String detailAddress;
    @SerializedName("userName")
    public String userName;
    @SerializedName("userPhoneNum")
    public String userPhoneNum;
    @SerializedName("position")
    public int position;
    @SerializedName("isDefault")
    private int isDefault;

    public boolean isDefult() {
        return isDefault == 1;
    }
}