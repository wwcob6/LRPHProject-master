package com.app.request;

import com.app.model.CallModel;
import com.app.model.PNBaseModel;
import com.punuo.sys.app.httplib.BaseRequest;

public class GetCallRequest extends BaseRequest<CallModel> {

        public GetCallRequest() {
            setRequestType(RequestType.GET);
            setRequestPath("/users/getServiceNumber");
        }
    }
