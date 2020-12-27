package com.app.request;

import com.app.friendCircleMain.domain.Group;
import com.punuo.sys.app.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019/5/28.
 **/
public class GetAllGroupFromUserRequest extends BaseRequest<Group> {

    public GetAllGroupFromUserRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/getAllGroupFromUser");
    }
}
