package com.app.model;

import android.graphics.Bitmap;

/**
 * Created by maojianhui on 2018/7/31.
 */

public class Device1 {
    private String id;
    private String userid;
    private String nickname;
    private String name;
    private String avatar;
    private String auth;
    private String devId;
    private String devType;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        if(bitmap==null){
            this.bitmap=null;
        }else {
            this.bitmap = bitmap;
        }
    }

    private Bitmap bitmap;

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        if (devType.isEmpty()){
            this.devType="0";
        }
        this.devType = devType;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        if (devId.isEmpty()){
            this.devId="0";
        }
        this.devId = devId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id.isEmpty()){
            this.id="0";
        }
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        if (userid.isEmpty()){
            this.userid="0";
        }
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        if (nickname.isEmpty()){
            this.nickname="0";
        }
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.isEmpty()){
            this.name="0";
        }
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        if (avatar.isEmpty()){
            this.avatar="0";
        }
        this.avatar = avatar;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        if (auth.isEmpty()){
            this.auth="0";
        }
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "id:"+id+" userid:"+userid +" nickname:"+nickname+" name:"+name+" avatar:"+avatar+" auth:"+auth;
    }

}
