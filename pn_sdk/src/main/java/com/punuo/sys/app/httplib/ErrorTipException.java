package com.punuo.sys.app.httplib;

import java.io.IOException;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public class ErrorTipException extends IOException {

    public int mResposeCode = 0;
    public ErrorTipException() {
        super();
    }

    public ErrorTipException(String detailMessage) {
        super(detailMessage);
    }

    public ErrorTipException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorTipException(Throwable cause) {
        super(cause);
    }
}
