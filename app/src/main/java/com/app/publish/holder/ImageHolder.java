package com.app.publish.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.R;
import com.app.publish.ICallBack;
import com.bumptech.glide.Glide;
import com.punuo.sys.app.recyclerview.BaseViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by han.chen.
 * Date on 2019-06-21.
 **/
public class ImageHolder extends BaseViewHolder<String> {

    @Bind(R.id.image)
    ImageView mImage;
    private ICallBack mCallBack;

    public ImageHolder(Context context, ViewGroup parent, ICallBack callBack) {
        super(LayoutInflater.from(context).inflate(R.layout.publish_item_image, parent, false));
        ButterKnife.bind(this, itemView);
        mCallBack = callBack;
    }

    @Override
    protected void bindData(final String path, int position) {
        if (!TextUtils.equals("add", path)) {
            if (path != null) {
                Glide.with(itemView.getContext()).load(path).into(mImage);
                mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        } else {
            mImage.setImageResource(R.drawable.icon_addpic_unfocused);
        }
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.itemClick(path, position);
                }
            }
        });

    }
}
