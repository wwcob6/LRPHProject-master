package com.app.friendCircleMain.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.app.friendCircleMain.domain.FriendMicroListDatas;
import com.app.friendCircleMain.viewholder.FriendCircleViewHolder;
import com.punuo.sys.app.recyclerview.PageRecyclerViewAdapter;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-06-05.
 **/
public class FriendCircleAdapter extends PageRecyclerViewAdapter<FriendMicroListDatas> {
    public FriendCircleAdapter(Context context, List<FriendMicroListDatas> data) {
        super(context, data);
    }

    public void resetData(List<FriendMicroListDatas> list) {
        manuaRemoveFooterView();
        mData.clear();
        if (list != null) {
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void manuaRemoveFooterView() {
        mFootView = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        return new FriendCircleViewHolder(mContext, parent);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof FriendCircleViewHolder) {
            ((FriendCircleViewHolder) baseViewHolder).bind(getItem(position), position);
        }
    }
}
