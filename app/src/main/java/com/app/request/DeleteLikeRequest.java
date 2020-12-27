package com.app.request;

import com.punuo.sys.app.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019-06-12.
 **/
public class DeleteLikeRequest extends BaseRequest<String> {
    public DeleteLikeRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/posts/deleteLikes");
    }
}
