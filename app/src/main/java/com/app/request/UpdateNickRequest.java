package com.app.request;

import com.app.model.PNBaseModel;
import com.punuo.sys.app.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019-06-04.
 **/
public class UpdateNickRequest extends BaseRequest<PNBaseModel> {

    public UpdateNickRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/updateUserName");
    }
}
