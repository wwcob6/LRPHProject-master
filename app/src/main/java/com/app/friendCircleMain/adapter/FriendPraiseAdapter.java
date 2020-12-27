package com.app.friendCircleMain.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.app.friendCircleMain.domain.FirstMicroListDatasFirendpraise;
import com.app.friendCircleMain.viewholder.FriendPraiseViewHolder;
import com.punuo.sys.app.recyclerview.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class FriendPraiseAdapter extends BaseRecyclerViewAdapter<FirstMicroListDatasFirendpraise> {

    public FriendPraiseAdapter(Context context, List<FirstMicroListDatasFirendpraise> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        return new FriendPraiseViewHolder(mContext, parent);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof FriendPraiseViewHolder) {
            ((FriendPraiseViewHolder) baseViewHolder).bind(getItem(position), position);
        }
    }

    @Override
    public int getBasicItemType(int position) {
        return 0;
    }

    @Override
    public int getBasicItemCount() {
        return mData == null ? 0 : mData.size() ;
    }
}
