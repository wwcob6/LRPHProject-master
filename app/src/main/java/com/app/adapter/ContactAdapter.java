package com.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.app.model.Friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class ContactAdapter extends BaseExpandableListAdapter {

    private List<String> groupName = new ArrayList<>();
    private HashMap<String, List<Friend>> friendList;
    private Context mContext;

    public ContactAdapter(Context mContext, HashMap<String, List<Friend>> friendList) {
        this.mContext = mContext;
        this.friendList = friendList;
        Set keyname = friendList.keySet();
        for (Object keyName : keyname) {
            groupName.add((String) keyName);
//            Collections.sort(friendList.get(keyName));
        }
    }

    @Override
    public int getGroupCount() {
        Set keyname = friendList.keySet();
        return keyname.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String groupname = (String) groupName.get(groupPosition);
        return friendList.get(groupname).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupName.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String groupname = (String) groupName.get(groupPosition);
        return friendList.get(groupname).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_group_item, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.aliveNum = (TextView) convertView.findViewById(R.id.alive_num);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        List<Friend> friendlist = friendList.get(getGroup(groupPosition).toString());
        int aliveNum = 0;
        for (Friend friend : friendlist) {
//            if (friend.isLive()) {
//                aliveNum++;
//            }
        }
        holder.aliveNum.setText(aliveNum + "/" + friendlist.size());
        holder.name.setText(getGroup(groupPosition).toString());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childHolder;
        if (convertView == null) {
            childHolder = new ChildViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_group_child_item, null);
            childHolder.childname = (TextView) convertView.findViewById(R.id.childName);
            childHolder.childicon = (ImageView) convertView.findViewById(R.id.childIcon);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildViewHolder) convertView.getTag();
        }

        Friend friend = (Friend) getChild(groupPosition, childPosition);
//        if (friend.isLive()) {
//            childHolder.childicon.setImageResource(R.drawable.icon_online);
//        } else {
//            childHolder.childicon.setImageResource(R.drawable.icon_offline);
//        }
//        childHolder.childname.setText(friend.getRealName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ViewHolder {
        TextView name;
        TextView aliveNum;
    }

    private class ChildViewHolder {
        TextView childname;
        ImageView childicon;
    }
}
