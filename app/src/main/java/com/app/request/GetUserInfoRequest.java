package com.app.request;

import com.app.model.PNUserInfo;
import com.punuo.sys.app.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019/5/28.
 **/
public class GetUserInfoRequest extends BaseRequest<PNUserInfo> {

    public GetUserInfoRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/getUserInfo");
    }
}
