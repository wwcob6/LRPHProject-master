package com.app.adapter;


import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.R;
import com.app.model.CloudPhotoCover;
import com.app.model.MessageEvent;
import com.app.sip.SipInfo;
import com.nostra13.universalimageloader.core.ImageLoader;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.app.sip.SipInfo.serverIp;

public class CloudAlbumAdapter extends RecyclerView.Adapter<CloudAlbumAdapter.ViewHolder> {
    private List<CloudPhotoCover> mCloudPhotoCoverList;



    public void appendData(List<CloudPhotoCover> CloudPhotoCover) {
        if (CloudPhotoCover != null) {
            mCloudPhotoCoverList.clear();
            mCloudPhotoCoverList.addAll(CloudPhotoCover);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_photoDate;
        private ImageView iv_photoCover;
        private RelativeLayout rl_cloudPhoto;
        public ViewHolder(View v) {
            super(v);
            tv_photoDate=(TextView)v.findViewById(R.id.tv_photoDate);
            iv_photoCover=(ImageView)v.findViewById(R.id.iv_photoCover);
            rl_cloudPhoto=(RelativeLayout)v.findViewById(R.id.rl_cloudPhoto);
        }
    }

    public CloudAlbumAdapter(List<CloudPhotoCover> mCloudPhotoCoverList) {
        this.mCloudPhotoCoverList = mCloudPhotoCoverList;
    }

    @Override
    public CloudAlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cloud_album_adapter
        ,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CloudAlbumAdapter.ViewHolder holder, int position) {
        CloudPhotoCover cloudPhotoCover=mCloudPhotoCoverList.get(position);
        holder.tv_photoDate.setText(cloudPhotoCover.month+"月");
        ImageLoader.getInstance().displayImage("http://" + serverIp +
                        ":8000/static/ftp/" + mCloudPhotoCoverList.get(position).pic,
                holder.iv_photoCover);
        holder.rl_cloudPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SipInfo.month=cloudPhotoCover.month;
                EventBus.getDefault().post(new MessageEvent("照片"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCloudPhotoCoverList.size();
    }
}
