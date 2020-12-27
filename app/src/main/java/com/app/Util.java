package com.app;

import com.punuo.sys.app.httplib.HttpConfig;

/**
 * Created by han.chen.
 * Date on 2019-06-13.
 **/
public class Util {

    public static String getImageUrl(String id, String path) {
        return "http://" + HttpConfig.getHost() + ":" + HttpConfig.getPort() + "/static/xiaoyupeihu/"
                + id + "/" + path;
    }

    public static String getImageUrl(String path) {
        return "http://" + HttpConfig.getHost() + ":" + HttpConfig.getPort() + "/static/xiaoyupeihu/"
                + UserInfoManager.getUserInfo().id + "/" + path;
    }
}
