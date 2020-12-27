package com.punuo.sys.app.httplib;

import java.io.UnsupportedEncodingException;

/**
 * Created by xiaolinzi on 15/11/4.
 */
public class StringRequest extends BaseRequest<String> {

    private String mUrl;
    private String mStringBody;

    public StringRequest(String url) {
        mUrl = url;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    public StringRequest setStringBody(String body) {
        mStringBody = body;
        return this;
    }

    @Override
    protected void buildPostBody() {
        if (mStringBody == null) {
            super.buildPostBody();
        } else {
            try {
                body(mStringBody.getBytes(RequestParams.UTF_8));
            } catch (UnsupportedEncodingException e) {

            }
        }
    }

}
