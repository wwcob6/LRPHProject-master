package com.app.model;

import java.io.Serializable;

/**
 * Created by acer on 2016/10/8.
 */

public class LastestMsg implements Serializable {
    private static final long serialVersionUID = 2L;
    private int type;
    private String id;
    private int lastesttime;
    private String lastestmsg;
    private int groupmsgtype;
    private int newMsgCount;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLastesttime() {
        return lastesttime;
    }

    public void setLastesttime(int lastesttime) {
        this.lastesttime = lastesttime;
    }

    public String getLastestmsg() {
        return lastestmsg;
    }

    public void setLastestmsg(String lastestmsg) {
        this.lastestmsg = lastestmsg;
    }

    public int getGroupmsgtype() {
        return groupmsgtype;
    }

    public void setGroupmsgtype(int groupmsgtype) {
        this.groupmsgtype = groupmsgtype;
    }

    public int getNewMsgCount() {
        return newMsgCount;
    }

    public void setNewMsgCount(int newMsgCount) {
        this.newMsgCount = newMsgCount;
    }
}
