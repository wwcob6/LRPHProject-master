package com.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.R;
import com.app.Util;
import com.app.model.Friend;
import com.app.model.LastestMsg;
import com.app.sip.SipInfo;
import com.app.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by acer on 2016/10/8.
 */

public class MessageAdapter extends BaseAdapter {
    private  Friend f;
    private Context mContext;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public MessageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return SipInfo.lastestMsgs.size();
    }

    @Override
    public Object getItem(int position) {
        return SipInfo.lastestMsgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.messagelistitem, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.icon = (CircleImageView) convertView.findViewById(R.id.icon);
            holder.lastestMsg = (TextView) convertView.findViewById(R.id.lastest_msg);
            holder.lastestTime = (TextView) convertView.findViewById(R.id.lastest_time);
            holder.newMsgConut = (TextView) convertView.findViewById(R.id.newMsgCount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.newMsgConut.setVisibility(View.INVISIBLE);
        holder.lastestMsg.setTextColor(Color.rgb(142, 142, 142));
        LastestMsg lastestMsg = SipInfo.lastestMsgs.get(position);
        if (lastestMsg.getType() == 0) {
            Friend friend = new Friend();
            friend.setUserId(lastestMsg.getId());
            int index = SipInfo.friends.indexOf(friend);
            if (index != -1) {
                f = SipInfo.friends.get(index);
                //holder.icon.setImageResource(R.drawable.icon_online);
                RequestOptions options = new RequestOptions().error(R.drawable.empty_photo);
                Glide.with(mContext).
                        load(Util.getImageUrl(f.getId(), f.getAvatar())).
                        apply(options).
                        into(holder.icon);
                holder.name.setText(f.getNickName());
                holder.lastestMsg.setText(lastestMsg.getLastestmsg());
                Date date = new Date(lastestMsg.getLastesttime() * 1000L);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                holder.lastestTime.setText(format.format(date));
                if (lastestMsg.getNewMsgCount() == 0) {
                    holder.newMsgConut.setVisibility(View.INVISIBLE);
                } else {
                    holder.newMsgConut.setVisibility(View.VISIBLE);
                    holder.newMsgConut.setText("" + lastestMsg.getNewMsgCount());
                }
            }
        }
        return convertView;
    }

    private class ViewHolder {
        CircleImageView icon;
        TextView name;
        TextView lastestMsg;
        TextView lastestTime;
        TextView newMsgConut;
    }
}
