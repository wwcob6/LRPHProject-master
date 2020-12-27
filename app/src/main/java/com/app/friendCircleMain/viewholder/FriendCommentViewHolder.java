package com.app.friendCircleMain.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.R;
import com.app.friendCircleMain.domain.FirstMicroListDatasFirendcomment;
import com.punuo.sys.app.recyclerview.BaseViewHolder;
import com.punuo.sys.app.util.ViewUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class FriendCommentViewHolder extends BaseViewHolder<FirstMicroListDatasFirendcomment> {
    @Bind(R.id.friend_name)
    TextView mFriendName;
    @Bind(R.id.friend_comment)
    TextView mFriendComment;

    public FriendCommentViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.friend_commit_item, parent, false));
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void bindData(FirstMicroListDatasFirendcomment data, int position) {
        ViewUtil.setText(mFriendName, data.getReplyName() + ":");
        ViewUtil.setText(mFriendComment, data.getComment());
    }
}
