package com.app.friendCircleMain.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.app.friendCircleMain.domain.FirstMicroListDatasFirendcomment;
import com.app.friendCircleMain.viewholder.FriendCommentViewHolder;
import com.punuo.sys.app.recyclerview.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class FriendCommentAdapter extends BaseRecyclerViewAdapter<FirstMicroListDatasFirendcomment> {
    public FriendCommentAdapter(Context context, List<FirstMicroListDatasFirendcomment> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        return new FriendCommentViewHolder(mContext, parent);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof FriendCommentViewHolder) {
            ((FriendCommentViewHolder) baseViewHolder).bind(getItem(position), position);
        }
    }

    @Override
    public int getBasicItemType(int position) {
        return 0;
    }

    @Override
    public int getBasicItemCount() {
        return mData == null ? 0 : mData.size();
    }
}
