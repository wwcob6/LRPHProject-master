package com.app.ui.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.R;
import com.app.UserInfoManager;
import com.app.Util;
import com.app.model.PNUserInfo;
import com.app.sip.SipInfo;
import com.app.ui.CloudAlbum;
import com.app.ui.FamilyCircleActivity;
import com.app.ui.PrivateActivity;
import com.app.ui.SaomaActivity;
import com.app.ui.ServiceCallSet;
import com.app.ui.SettingActivity;
import com.app.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.punuo.sys.app.fragment.BaseFragment;
import com.punuo.sys.app.util.StatusBarUtil;
import com.punuo.sys.app.util.ToastUtils;
import com.punuo.sys.app.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.app.camera.FileOperateUtil.TAG;

public class PersonFragment extends BaseFragment implements View.OnClickListener {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private View mView;
    private TextView tv_name;
    private TextView tv_fxid;
    private CircleImageView iv_avatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_person, container, false);
        initView();
        return mView;
    }

    private void initView() {
        TextView title = mView.findViewById(R.id.title);
        title.setText("个人中心");
        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
        RelativeLayout re_myinfo = mView.findViewById(R.id.re_myinfo);
        re_myinfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),
                        FamilyCircleActivity.class));
            }

        });
        iv_avatar = mView.findViewById(R.id.iv_avatar);
        tv_name = mView.findViewById(R.id.tv_name);
        tv_fxid = mView.findViewById(R.id.tv_fxid);
        String avatar = UserInfoManager.getUserInfo().avatar;
        Glide.with(getActivity()).load(Util.getImageUrl(avatar)).into(iv_avatar);
        RelativeLayout re_xaingce = mView.findViewById(R.id.re_xiangce);
        RelativeLayout re_addev = mView.findViewById(R.id.re_adddev);
        RelativeLayout re_servicecall = mView.findViewById(R.id.re_servicecall);
//        RelativeLayout re_order = mView.findViewById(R.id.re_order);
//        RelativeLayout re_coupon = mView.findViewById(R.id.re_coupon);
//        RelativeLayout re_shoppingcart = mView.findViewById(R.id.re_shoppingcart);
//        RelativeLayout re_collection = mView.findViewById(R.id.re_collection);
        RelativeLayout re_settings = mView.findViewById(R.id.re_settings);
        RelativeLayout re_private = mView.findViewById(R.id.re_private);
        re_xaingce.setOnClickListener(this);
        re_addev.setOnClickListener(this);
        re_servicecall.setOnClickListener(this);
//        re_order.setOnClickListener(this);
//        re_coupon.setOnClickListener(this);
//        re_shoppingcart.setOnClickListener(this);
//        re_collection.setOnClickListener(this);
        re_settings.setOnClickListener(this);
        re_private.setOnClickListener(this);
        mView.findViewById(R.id.back).setVisibility(View.GONE); // 隐藏返回按钮

        Glide.with(getActivity()).load(Util.getImageUrl(UserInfoManager.getUserInfo().avatar)).into(iv_avatar);
        ViewUtil.setText(tv_name, "昵称：" + UserInfoManager.getUserInfo().nickname);
        ViewUtil.setText(tv_fxid, "手机号：  " + SipInfo.userAccount);

        View statusBar = mView.findViewById(R.id.status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBar.setVisibility(View.VISIBLE);
            statusBar.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(getActivity());
            statusBar.requestLayout();
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write PermissionUtils
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {// We don't have PermissionUtils so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.re_xiangce:
//                verifyStoragePermissions(getActivity());
                /**
                 * Checks if the app has PermissionUtils to write to device storage
                 * If the app does not has PermissionUtils then the user will be prompted to
                 * grant permissions
                 * @param activity
                 */
                startActivity(new Intent(getActivity(), CloudAlbum.class));
//                ToastUtils.showToastShort("该功能即将上线");
//                showPhotoDialog();
                break;
//            case R.id.re_psd:
//                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
//                break;
            case R.id.re_adddev:
                startActivity(new Intent(getActivity(), SaomaActivity.class));
                break;
            case R.id.re_servicecall:
                startActivity(new Intent(getActivity(), ServiceCallSet.class));
                break;
//            case R.id.re_order:
//                ToastUtils.showToastShort("该功能即将上线");
//                break;
//            case R.id.re_coupon:
//                startActivity(new Intent(getActivity(),MyCouponActivity.class));
//                break;
//            case R.id.re_shoppingcart:
//                ToastUtils.showToastShort("该功能即将上线");
//                break;
//            case R.id.re_collection:
//                ToastUtils.showToastShort("该功能即将上线");
//                break;
//            case R.id.re_instruction:
//                startActivity(new Intent(getActivity(),SoftwareInstructActivity.class));
//                break;
            case R.id.re_settings:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.re_private:
                startActivity(new Intent(getActivity(), PrivateActivity.class));
                break;
        }
    }

    @SuppressLint("NewApi")
    private void requestReadExternalPermission() {

        if (ContextCompat.checkSelfPermission(getContext()
                , Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "READ PermissionUtils IS NOT granted...");

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Log.d(TAG, "11111111111111");
            } else {
                // 0 是自己定义的请求coude
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                Log.d(TAG, "222222222222");
            }
        } else {
            Log.d(TAG, "READ PermissionUtils is granted...");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "requestCode=" + requestCode + "; --->" + permissions.toString()
                + "; grantResult=" + grantResults.toString());
        switch (requestCode) {
            case 0: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // PermissionUtils was granted
                    // request successfully, handle you transactions

                } else {

                    // PermissionUtils denied
                    // request failed
                }

                return;
            }
            default:
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PNUserInfo.UserInfo userInfo) {
        Glide.with(getActivity()).load(Util.getImageUrl(UserInfoManager.getUserInfo().avatar)).into(iv_avatar);
        ViewUtil.setText(tv_name, "昵称：" + UserInfoManager.getUserInfo().nickname);
        ViewUtil.setText(tv_fxid, "手机号：  " + SipInfo.userAccount);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
