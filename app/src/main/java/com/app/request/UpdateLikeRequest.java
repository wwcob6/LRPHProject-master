package com.app.request;

import com.punuo.sys.app.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019-06-12.
 **/
public class UpdateLikeRequest extends BaseRequest<String> {

    public UpdateLikeRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/posts/updateLikes");
    }
}
