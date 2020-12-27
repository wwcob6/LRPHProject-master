package com.app.request;

import com.app.model.PNBaseModel;
import com.punuo.sys.app.httplib.BaseRequest;

public class ChangeCallRequest extends BaseRequest<String> {

    public ChangeCallRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/updateServiceNumber");
    }
}
