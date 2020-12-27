package com.app.friendCircleMain.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 林逸磊 on 2018/1/31.
 */

public class Alldevid extends MyBaseBean{
    public List<String> devid = new ArrayList<String>();

    public List<String> getDevid() {
        return devid;
    }

    public void setDevid(List<String> devid) {
        this.devid = devid;
    }
}
