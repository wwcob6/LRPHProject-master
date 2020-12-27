package com.punuo.sys.app.httplib;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public interface IHttpConfig {

    String getHost();

    int getPort();

    boolean isUseHttps();

    String getUserAgent();

    String getPrefixPath();
}
