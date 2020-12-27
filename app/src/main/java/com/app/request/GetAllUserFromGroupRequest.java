package com.app.request;

import com.app.friendCircleMain.domain.UserFromGroup;
import com.punuo.sys.app.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019/5/28.
 **/
public class GetAllUserFromGroupRequest extends BaseRequest<UserFromGroup> {
    public GetAllUserFromGroupRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/groups/getAllUserFromGroup");
    }
}
