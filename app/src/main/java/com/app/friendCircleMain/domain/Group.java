package com.app.friendCircleMain.domain;

import java.util.List;

/**
 * Created by 林逸磊 on 2017/12/1.
 */

public class Group extends MyBaseBean{
    public List<GroupList> groupList;

    public List<GroupList> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupList> groupList) {
        this.groupList = groupList;
    }
}
