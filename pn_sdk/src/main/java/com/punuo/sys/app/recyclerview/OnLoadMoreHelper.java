package com.punuo.sys.app.recyclerview;

import android.view.View;

/**
 * Created by han.chen.
 * Date on 2019-06-05.
 **/
public abstract class OnLoadMoreHelper {
    public abstract boolean canLoadMore();

    public abstract void onLoadMore();

    public View createCustomLoadingFooter() {
        return null;
    }

    public View createCustomManualLoadingFooter() {
        return null;
    }
}
