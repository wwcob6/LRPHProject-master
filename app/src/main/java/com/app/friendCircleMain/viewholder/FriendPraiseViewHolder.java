package com.app.friendCircleMain.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.app.friendCircleMain.domain.FirstMicroListDatasFirendpraise;
import com.punuo.sys.app.recyclerview.BaseViewHolder;
import com.punuo.sys.app.util.ViewUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class FriendPraiseViewHolder extends BaseViewHolder<FirstMicroListDatasFirendpraise> {
    public static final String TYPE_DIANZAN = "1";
    public static final String TYPE_WEIXIAO = "2";
    public static final String TYPE_DAXIAO = "3";
    public static final String TYPE_KUXIAO = "4";

    @Bind(R.id.icon)
    ImageView mIcon;
    @Bind(R.id.nick_name)
    TextView mNickName;
    @Bind(R.id.desc)
    TextView mDesc;

    private int[] drawables = new int[]{
            R.drawable.l_xin,
            R.drawable.d_keai,
            R.drawable.d_xixi,
            R.drawable.d_xiaoku
    };

    private String[] mDescs = new String[]{
            "赞了一个！",
            "真有趣~_~",
            "好开心啊^_^",
            "笑死我了>﹏<"
    };

    public FriendPraiseViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.friend_praise_item, parent, false));
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void bindData(FirstMicroListDatasFirendpraise data, int position) {
        ViewUtil.setText(mNickName, data.getNickname());
        switch (data.getPraisetype()) {
            case TYPE_DIANZAN:
                mIcon.setImageResource(drawables[0]);
                mDesc.setText(mDescs[0]);
                break;
            case TYPE_WEIXIAO:
                mIcon.setImageResource(drawables[1]);
                mDesc.setText(mDescs[1]);
                break;
            case TYPE_DAXIAO:
                mIcon.setImageResource(drawables[2]);
                mDesc.setText(mDescs[2]);
                break;
            case TYPE_KUXIAO:
                mIcon.setImageResource(drawables[3]);
                mDesc.setText(mDescs[3]);
                break;
            default:
                break;
        }
    }
}
