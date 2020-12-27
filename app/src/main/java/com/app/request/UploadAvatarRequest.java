package com.app.request;

import com.app.model.UploadAvatarResult;
import com.punuo.sys.app.httplib.BaseRequest;

import okhttp3.MediaType;

/**
 * Created by han.chen.
 * Date on 2019/5/28.
 **/
public class UploadAvatarRequest extends BaseRequest<UploadAvatarResult> {
    public UploadAvatarRequest() {
        setRequestType(RequestType.UPLOAD);
        setRequestPath("/users/updateUserPic");
        contentType(MediaType.parse("image/*"));
    }
}
