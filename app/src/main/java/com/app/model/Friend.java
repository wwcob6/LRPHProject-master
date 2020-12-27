package com.app.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class Friend implements Comparable<Friend>, Parcelable {
    private boolean online=false;
    private String avatar;
    private String id;
    private String userId;
    private String nickName;
    private String phoneNum;
    private int newMsgCount = 0;
    private boolean isSelect = false;
    private String sortLetters;  //显示数据拼音的首字母

    public Friend() {
    }

    public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            Friend friend=new Friend();
            friend.avatar = in.readString();
            friend.id = in.readString();
            friend.userId = in.readString();
            friend.nickName = in.readString();
            friend.phoneNum = in.readString();
            friend.newMsgCount = in.readInt();
            friend.isSelect = in.readByte() != 0;
            friend.sortLetters = in.readString();
            return friend;
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(avatar);
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(nickName);
        dest.writeString(phoneNum);
        dest.writeInt(newMsgCount);
        dest.writeByte((byte) (isSelect ? 1 : 0));
        dest.writeString(sortLetters);
    }

    @Override
    public int compareTo(Friend another) {
        if (another != null) {
            Friend dev = another;
            return this.phoneNum.compareTo(dev.getPhoneNum());
        } else {
            throw new NullPointerException("比较对象为空");
        }
    }

    public boolean getStaus(){return online;};
    public void setStaus(boolean staus){this.online=staus;}
    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }


    public String getUserId() {
        return userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public int getNewMsgCount() {
        return newMsgCount;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }


    public void setNewMsgCount(int newMsgCount) {
        this.newMsgCount = newMsgCount;
    }

    public void addMsgCount(int newMsgCount) {
        this.newMsgCount += newMsgCount;
    }


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o.getClass() == Friend.class) {
            Friend friend = (Friend) o;
            if (this.getUserId().equals(friend.getUserId())) {
                return true;
            }
        }
        return false;
    }
}
