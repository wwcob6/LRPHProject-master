package com.app.friendCircleMain.domain;

/**
 * Created by 林逸磊 on 2018/1/9.
 */

public class UserList extends MyBaseBean {
    private String name;
    private String id;
    private String userid;
    private String nickname;
    private String avatar;

    public String getNickname() {
        return nickname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getId() {
        return id;
    }

    public String getUserid() {
        return userid;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
