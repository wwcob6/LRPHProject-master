package com.app.videoAndPictureUpload;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * Created by Wxcily on 15/11/6.
 */
public class BottomListDialogAdapter extends BaseAdapter {

    private Context context;
    Map<String, List<Video>> AllList;

    private int selected = 0;

    public BottomListDialogAdapter(Context context, Map<String, List<Video>> AllList) {
        this.context = context;
        this.AllList = AllList;

    }

    public Map<String, List<Video>> getAllList() {
        return AllList;
    }

    public void setAllList(Map<String, List<Video>> allList) {
        AllList = allList;
    }

    public void setSelected(int position) {
        this.selected = position;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return AllList.size();
    }

    @Override
    public Object getItem(int position) {
        return AllList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.adapter_select_photo_dialog_item, parent, false);
        }
        ImageView icon = AdapterUtils.getAdapterView(convertView, R.id.img1);
        TextView text1 = AdapterUtils.getAdapterView(convertView, R.id.text1);
        TextView text2 = AdapterUtils.getAdapterView(convertView, R.id.text2);

        String album = (String) AllList.keySet().toArray()[position];
        Glide
                .with(context)
                .asBitmap()
                .load(Uri.fromFile(new File(AllList.get(album).get(0).getPath())))
                .into(icon);

//        icon.setImageBitmap(AllList.get(album).get(0).getThumbnail());
        text1.setText(album);
        text2.setText(AllList.get(album).size() + "");

        return convertView;
    }
}
