package com.punuo.sys.app.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by han.chen.
 * Date on 2019-06-05.
 **/
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(T t, int position) {
        if (t == null) {
            return;
        }
        bindData(t, position);
    }

    protected abstract void bindData(T t, int position);
}
