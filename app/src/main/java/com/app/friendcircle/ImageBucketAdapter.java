package com.app.friendcircle;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ImageBucketAdapter extends BaseAdapter {
    private final String TAG = getClass().getSimpleName();

    private Activity mActivity;
    /**
     * 图片集列表
     */
    private List<ImageBucket> dataList;

    public ImageBucketAdapter(Activity act, List<ImageBucket> list) {
        mActivity = act;
        dataList = list;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 :dataList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return dataList == null ? null : dataList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    private static class Holder {
        private ImageView iv;
        private ImageView selected;
        private TextView name;
        private TextView count;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        Holder holder;
        if (arg1 == null) {
            holder = new Holder();
            arg1 = View.inflate(mActivity, R.layout.item_image_bucket, null);
            holder.iv = arg1.findViewById(R.id.image);
            holder.selected = arg1.findViewById(R.id.isselected);
            holder.name = arg1.findViewById(R.id.name);
            holder.count = arg1.findViewById(R.id.count);
            arg1.setTag(holder);
        } else {
            holder = (Holder) arg1.getTag();
        }
        ImageBucket item = dataList.get(arg0);
        holder.count.setText(String.valueOf(item.count));
        holder.name.setText(item.bucketName);
        holder.selected.setVisibility(View.GONE);

        if (item.imageList != null && item.imageList.size() > 0) {
            Glide.with(mActivity).load(item.imageList.get(0).imagePath).into(holder.iv);
        } else {
            holder.iv.setImageBitmap(null);
        }
        return arg1;
    }

}
