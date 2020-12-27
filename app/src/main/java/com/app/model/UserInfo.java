package com.app.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class UserInfo implements Parcelable{
    private String userId;//用户id
    private String userName;//用户账号
    private String userRealName;//用户真实名字

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        userRealName = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(userRealName);
    }
}
