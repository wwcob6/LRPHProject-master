package com.app.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author chzjy
 * Date 2016/12/20.
 */

public class MailInfo implements Parcelable {

    private String mailId;
    private String fromUserId;
    private String toUserId;
    private int time;
    private String content;
    private String theme;
    private int isRead;

    public MailInfo() {
    }


    protected MailInfo(Parcel in) {
        mailId = in.readString();
        fromUserId = in.readString();
        toUserId = in.readString();
        time = in.readInt();
        content = in.readString();
        isRead = in.readInt();
        theme = in.readString();
    }

    public static final Creator<MailInfo> CREATOR = new Creator<MailInfo>() {
        @Override
        public MailInfo createFromParcel(Parcel in) {
            return new MailInfo(in);
        }

        @Override
        public MailInfo[] newArray(int size) {
            return new MailInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mailId);
        dest.writeString(fromUserId);
        dest.writeString(toUserId);
        dest.writeInt(time);
        dest.writeString(content);
        dest.writeInt(isRead);
        dest.writeString(theme);
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
