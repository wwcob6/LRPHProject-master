package com.app.request;

import com.app.model.PNBaseModel;
import com.punuo.sys.app.httplib.BaseRequest;

import okhttp3.MediaType;

/**
 * Created by han.chen.
 * Date on 2019/5/29.
 **/
public class UploadPostRequest extends BaseRequest<PNBaseModel> {
    public UploadPostRequest() {
        setRequestType(RequestType.UPLOAD);
        setRequestPath("/posts/insertPost");
        contentType(MediaType.parse("multipart/form-data; charset=utf-8"));
    }
}
