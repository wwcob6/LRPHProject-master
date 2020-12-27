package com.app.friendCircleMain.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.R;
import com.app.Util;
import com.app.friendCircleMain.adapter.FriendCommentAdapter;
import com.app.friendCircleMain.adapter.FriendPraiseAdapter;
import com.app.friendCircleMain.adapter.NineGridTestLayout;
import com.app.friendCircleMain.domain.FirstMicroListDatasFirendcomment;
import com.app.friendCircleMain.domain.FirstMicroListDatasFirendimage;
import com.app.friendCircleMain.domain.FirstMicroListDatasFirendpraise;
import com.app.friendCircleMain.domain.FriendMicroListDatas;
import com.app.friendCircleMain.util.PopupWindowUtil;
import com.app.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.punuo.sys.app.recyclerview.BaseViewHolder;
import com.punuo.sys.app.util.TimeUtils;
import com.punuo.sys.app.util.ViewUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by han.chen.
 * Date on 2019-06-05.
 **/
public class FriendCircleViewHolder extends BaseViewHolder<FriendMicroListDatas> {
    private NineGridTestLayout layout9;
    private TextView mTime;
    private CircleImageView mAvatar;
    private TextView mName;
    private TextView mContent;
    private Button btnIgnore;
    private Context mContext;

    private RecyclerView mPariseList;
    private FriendPraiseAdapter mFriendPraiseAdapter;

    private RecyclerView mFriendCommentList;
    private FriendCommentAdapter mFriendCommentAdapter;

    private PopupWindowUtil mPopupWindowUtil;

    public FriendCircleViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.micro_list_item, parent, false));
        mContext = context;
        initView(itemView);
    }

    private void initView(View itemView) {
        layout9 = itemView.findViewById(R.id.layout_nine_grid);//九宫格图片
        mTime = itemView.findViewById(R.id.time);
        mAvatar = itemView.findViewById(R.id.avator);
        mName = itemView.findViewById(R.id.name);
        mContent = itemView.findViewById(R.id.content);
        btnIgnore = itemView.findViewById(R.id.btnIgnore);

        mPariseList = itemView.findViewById(R.id.parise_list);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext);
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        mPariseList.setLayoutManager(layoutManager1);
        mFriendPraiseAdapter = new FriendPraiseAdapter(mContext, new ArrayList<>());
        mPariseList.setAdapter(mFriendPraiseAdapter);

        mFriendCommentList = itemView.findViewById(R.id.friend_comment_list);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(mContext);
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        mFriendCommentList.setLayoutManager(layoutManager2);
        mFriendCommentAdapter = new FriendCommentAdapter(mContext, new ArrayList<>());
        mFriendCommentList.setAdapter(mFriendCommentAdapter);

        mPopupWindowUtil = new PopupWindowUtil(mContext);

    }

    @Override
    protected void bindData(FriendMicroListDatas bean, int position) {
        //头像
        String avatarPath = Util.getImageUrl(bean.getId(), bean.getAvatar());
        RequestOptions options = new RequestOptions().error(R.drawable.empty_photo);
        Glide.with(mContext).load(avatarPath).apply(options).into(mAvatar);

        /*
         * 显示时间
         * 服务器返回的时间是：年-月-日 时：分，所以获取的时候应该是yyyy-MM-dd HH:mm
         */
        String strTime = bean.getCreate_time().trim();
        if (TextUtils.isEmpty(strTime)) {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String date = sDateFormat.format(new Date());
            String t = TimeUtils.getTimes(date, strTime);
            mTime.setText(t);
        }
        /*
         * 显示姓名和内容
         */
        ViewUtil.setText(mName, bean.getNickname());
        List<FirstMicroListDatasFirendimage> postPic = bean.getPost_pic();
        List<String> urls = new ArrayList<>();
        if (postPic != null && !postPic.isEmpty()) {
            for (int i = 0; i < postPic.size(); i++) {
                urls.add(Util.getImageUrl(bean.getId(), postPic.get(i).getPic_name()));
            }
        }

        layout9.setIsShowAll(true);
        layout9.setUrlList(urls);

        ViewUtil.setText(mContent, bean.getContent());

        List<FirstMicroListDatasFirendpraise> friendpraise = bean.getAddlike_nickname();
        //显示点赞holder.layoutPraise   friendpraise
        if (friendpraise != null && !friendpraise.isEmpty()) {
            mFriendPraiseAdapter.getData().clear();
            mFriendPraiseAdapter.addAll(friendpraise);
            mPariseList.setVisibility(View.VISIBLE);
        } else {
            mPariseList.setVisibility(View.GONE);
        }

        List<FirstMicroListDatasFirendcomment> friendComment = bean.getFriendComment();
        if (friendComment != null && !friendComment.isEmpty()) {
            mFriendCommentAdapter.getData().clear();
            mFriendCommentAdapter.addAll(friendComment);
            mFriendCommentList.setVisibility(View.VISIBLE);
        } else {
            mFriendCommentList.setVisibility(View.GONE);
        }
        mPopupWindowUtil.setFriendMicroListDatas(bean, position);
        //显示评论、点赞按钮
        btnIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindowUtil.show(v);
            }
        });
    }
}
