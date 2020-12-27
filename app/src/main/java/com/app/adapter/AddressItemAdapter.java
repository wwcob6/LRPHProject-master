package com.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.R;
import com.app.model.AddressItem;
import com.app.model.MessageEvent;
import com.app.sip.SipInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.app.sip.SipInfo.addressList;

/**
 * Created by maojianhui on 2019/3/21.
 */

public class AddressItemAdapter extends RecyclerView.Adapter<AddressItemAdapter.ViewHolder> {
    private List<AddressItem> mAddressList;

    public AddressItemAdapter(Context context, List<AddressItem> addressList) {
        mAddressList = addressList;
    }


    public void appendData(List<AddressItem> address) {
        if (address != null) {
            mAddressList.clear();
            mAddressList.addAll(address);
            notifyDataSetChanged();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.addressitem, parent, false);
        return new AddressItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressItemAdapter.ViewHolder holder, int position) {
        AddressItem addressitem = mAddressList.get(position);
        holder.userAddress.setText(addressitem.userAddress);
        holder.detailAddress.setText(addressitem.detailAddress);
        holder.userName.setText(addressitem.userName);
        holder.userPhoneNum.setText(changePhoneNum(addressitem.userPhoneNum));
        holder.isDefault.setText(addressitem.isDefult() ? "默认" : "");
        holder.addressEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SipInfo.listPosition = holder.getAdapterPosition();
                AddressItem addressitem = addressList.get(SipInfo.listPosition);
                SipInfo.addressPosition = addressitem.position;
                SipInfo.isDefault = addressitem.position;
                EventBus.getDefault().post(new MessageEvent("编辑"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAddressList.size();
    }

    public static String changePhoneNum(String mobile) {
        String maskNumber = mobile.substring(0, 3) + "****" + mobile.substring(7, mobile.length());
        return maskNumber;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View addressView;
        RelativeLayout rlUserAddress;
        ImageView addressEdit;
        TextView userAddress;
        TextView detailAddress;
        TextView userName;
        TextView userPhoneNum;
        TextView isDefault;

        public ViewHolder(View v) {
            super(v);
            addressView = v;
            rlUserAddress = (RelativeLayout) v.findViewById(R.id.rl_userAddress);
            addressEdit = (ImageView) v.findViewById(R.id.iv_addressEdit);
            userAddress = (TextView) v.findViewById(R.id.tv_userAddress);
            detailAddress = (TextView) v.findViewById(R.id.tv_detailAddress);
            userName = (TextView) v.findViewById(R.id.tv_userName);
            userPhoneNum = (TextView) v.findViewById(R.id.tv_userPhone);
            isDefault = (TextView) v.findViewById(R.id.tv_isdefault);

        }
    }
}
