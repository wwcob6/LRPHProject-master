package com.app.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.app.model.Friend;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.app.R;
import com.app.sip.SipInfo;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import static com.app.sip.SipInfo.friendsList;
//import static com.app.sip.SipInfo.devList1;

/**
 * Created by asus on 2018/1/22.
 */

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {

    private Context mContext;
    Object obj = new Object();
    private Handler handler=new Handler();
    public static String ip = "http://"+SipInfo.serverIp+":8000/static/xiaoyupeihu/";
    String devId;

    private Drawable mDefaultBitmapDrawable;


    public PictureAdapter(Context mContext) {
        this.mContext = mContext;
        mDefaultBitmapDrawable = mContext.getResources().getDrawable(R.drawable.image_default);
    }

//    public void appendData(List<Device> devices) {
//        synchronized (obj) {
//            if (devices.isEmpty()) return;
//            SipInfo.devList.clear();
//            SipInfo.devList.addAll(devices);
//            notifyDataSetChanged();
//        }
    public void appendData(List<Friend> devices) {
        synchronized (obj) {
            if (devices.isEmpty()) return;
            friendsList.clear();
            friendsList.addAll(devices);
            notifyDataSetChanged();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.picture_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position=holder.getAdapterPosition();
                Log.d("111", "onClick: "+position+friendsList);
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Friend device = friendsList.get(position);
        ImageView imageView = holder.icon;
        ImageLoader.getInstance().displayImage(ip + device.getId() + "/" + device.getAvatar(), imageView);
        holder.nickName.setText(device.getNickName());
        if(device.getStaus()){
            holder.staus.setText("聊天中");
            changeLight(holder.icon,40);
        }
       else {
            changeLight(holder.icon,-60);
            holder.staus.setText("尚未加入");
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    //改变图片的亮度方法 0--原样 >0---调亮 <0---调暗
    public void changeLight(ImageView imageView, int brightness) {
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness, // 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});
        imageView.setColorFilter(new ColorMatrixColorFilter(cMatrix));
    }
    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.nickName)
        TextView nickName;
        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.staus)
        TextView staus;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
