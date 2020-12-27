package com.app.request;

import com.app.model.AddressResult;
import com.punuo.sys.app.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019-06-03.
 **/
public class GetAddressListRequest extends BaseRequest<AddressResult> {

    public GetAddressListRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/getAddress");
    }
}
