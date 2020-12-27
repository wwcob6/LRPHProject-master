package com.app.friendCircleMain.domain;

/**
 * Created by 林逸磊 on 2017/12/1.
 */

public class GroupList extends MyBaseBean {
    private String groupid;
    private String id;
    private String group_name;
    private String avatar;
   private  String total;
    private String create_time;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCreate_time() {
        return create_time;
    }

    public String getGroup_name() {
        return group_name;
    }

    public String getGroupid() {
        return groupid;
    }

    public String getTotal() {
        return total;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
