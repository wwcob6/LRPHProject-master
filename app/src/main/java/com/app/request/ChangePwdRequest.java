package com.app.request;

import com.app.model.PNBaseModel;
import com.punuo.sys.app.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019/5/29.
 **/
public class ChangePwdRequest extends BaseRequest<PNBaseModel> {

    public ChangePwdRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/updateUserPwd");
    }
}
