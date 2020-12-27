package com.app.request;

import com.app.model.PNBaseModel;
import com.punuo.sys.app.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019-06-03.
 **/
public class UpdateSexRequest extends BaseRequest<PNBaseModel> {
    public UpdateSexRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/updateSex");
    }
}
