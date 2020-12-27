package com.app.request;

import com.app.model.PhotoCoverResult;
import com.punuo.sys.app.httplib.BaseRequest;

public class GetPhotoCoverRequest extends BaseRequest<PhotoCoverResult> {
    public GetPhotoCoverRequest(){
        setRequestType(RequestType.GET);
        setRequestPath("/getFirstPic");
    }
}
