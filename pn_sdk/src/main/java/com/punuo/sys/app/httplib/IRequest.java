package com.punuo.sys.app.httplib;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public interface IRequest<T> {

    IRequest setRequestListener(RequestListener<T> listener);

    RequestListener<T> getRequestListener();

    boolean isFinish();

    void finish();

    int TYPE_DIALOG = 1;
    int TYPE_PAGE = 2;
    int TYPE_EMPTY = 3;
}
