package com.app.model;

/**
 * Created by 23578 on 2018/7/26.
 */

public class Familymember {
    private String name;
    private String phonenumber;
    private String avatorurl;

    public Familymember(String name, String phonenumber) {
        this.name = name;
        this.phonenumber = phonenumber;
    }

    public Familymember(String name, String phonenumber, String avatorurl) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.avatorurl=avatorurl;
    }

    public String getAvatorurl() {
        return avatorurl;
    }

    public void setAvatorurl(String avatorurl) {
        this.avatorurl = avatorurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
