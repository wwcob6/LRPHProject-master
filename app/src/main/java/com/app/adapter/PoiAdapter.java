package com.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.app.model.MyPoiItem;
import com.app.R;

import java.util.List;

/**
 * Created by acer on 2016/11/4.
 */

public class PoiAdapter extends BaseAdapter {
    private Context mContext;
    private List<MyPoiItem> poiItems;

    public PoiAdapter(Context mContext, List<MyPoiItem> list) {
        this.mContext = mContext;
        this.poiItems = list;
    }

    @Override
    public int getCount() {
        return poiItems.size();
    }

    @Override
    public Object getItem(int position) {
        return poiItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        PoiItem poiItem=poiItems.get(position).getPoiItem();
        MyPoiItem myPoiItem=poiItems.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.poiloc_item, null);
            holder.addressname = (TextView) convertView.findViewById(R.id.addressname);
            holder.address=(TextView)convertView.findViewById(R.id.address);
            holder.isselect = (ImageView) convertView.findViewById(R.id.isselect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.address.setText(poiItem.getAdName()+poiItem.getSnippet());
        holder.addressname.setText(poiItem.getTitle());
        if (myPoiItem.isselect()){
            holder.isselect.setImageDrawable(mContext.getResources().getDrawable(R.drawable.isselect));
        }else {
            holder.isselect.setImageDrawable(null);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView addressname;
        TextView address;
        ImageView isselect;
    }
}
