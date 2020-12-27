package com.app.request;

import com.app.friendCircleMain.domain.Alldevid;
import com.punuo.sys.app.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019/5/28.
 **/
public class GetDevIdFromIdRequest extends BaseRequest<Alldevid> {

    public GetDevIdFromIdRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/devs/getDevIdFromId");
    }
}
