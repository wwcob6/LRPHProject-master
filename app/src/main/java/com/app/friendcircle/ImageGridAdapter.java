package com.app.friendcircle;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.bumptech.glide.Glide;
import com.punuo.sys.app.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class ImageGridAdapter extends BaseAdapter {
    private final String TAG = getClass().getSimpleName();
    private TextCallback textcallback = null;
    private Activity mActivity;
    private List<ImageItem> dataList;
    public ArrayList<String> mList = new ArrayList<>();
    private Handler mHandler;
    private int selectTotal = 0;

    public interface TextCallback {
        void onListen(int count);
    }

    public void setTextCallback(TextCallback listener) {
        textcallback = listener;
    }

    public ImageGridAdapter(Activity act, List<ImageItem> list, Handler mHandler) {
        this.mActivity = act;
        dataList = list;
        this.mHandler = mHandler;
    }

    public ImageGridAdapter(Activity act, List<ImageItem> list) {
        this.mActivity = act;
        dataList = list;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList == null ? null : dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class Holder {
        private ImageView mImageView;
        private ImageView selected;
        private TextView text;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_image_grid, parent, false);
            holder.mImageView = convertView.findViewById(R.id.image);
            holder.selected = convertView.findViewById(R.id.isselected);
            holder.text = convertView.findViewById(R.id.item_image_grid_text);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        final ImageItem item = dataList.get(position);

        Glide.with(mActivity).load(item.imagePath).into(holder.mImageView);
        if (item.isSelected) {
            holder.selected.setImageResource(R.drawable.icon_data_select);
            holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
        } else {
            holder.selected.setImageResource(android.R.color.transparent);
            holder.text.setBackgroundColor(0x00000000);
        }
        holder.mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = dataList.get(position).imagePath;

                if (selectTotal < 9) {
                    item.isSelected = !item.isSelected;
                    if (item.isSelected) {
                        holder.selected.setImageResource(R.drawable.icon_data_select);
                        holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
                        selectTotal++;
                        if (textcallback != null)
                            textcallback.onListen(selectTotal);
                        mList.add(path);

                    } else {
                        holder.selected.setImageResource(android.R.color.transparent);
                        holder.text.setBackgroundColor(0x00000000);
                        selectTotal--;
                        if (textcallback != null) {
                            textcallback.onListen(selectTotal);
                        }
                        mList.remove(path);
                    }
                } else {
                    if (item.isSelected) {
                        item.isSelected = false;
                        holder.selected.setImageResource(android.R.color.transparent);
                        selectTotal--;
                        mList.remove(path);
                    } else {
                        ToastUtils.showToast("最多选择九张图片");
                    }
                }
                notifyDataSetChanged();
            }

        });
        return convertView;
    }
}
