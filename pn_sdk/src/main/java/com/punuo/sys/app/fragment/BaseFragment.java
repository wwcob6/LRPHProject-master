package com.punuo.sys.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.punuo.sys.app.activity.BaseActivity;
import com.punuo.sys.app.view.PNLoadingDialog;

/**
 * Created by han.chen.
 * Date on 2019-06-03.
 **/
public class BaseFragment extends Fragment {
    private Activity mActivity;
    private PNLoadingDialog mLoadingDialog;
    protected View mFragmentView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = super.onCreateView(inflater, container, savedInstanceState);
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        initLoadingDialog();
    }
    private void initLoadingDialog() {
        mLoadingDialog = new PNLoadingDialog(mActivity);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setCanceledOnTouchOutside(false);
    }

    public void showLoadingDialog() {
        if (mActivity instanceof BaseActivity) {
            ((BaseActivity) mActivity).showLoadingDialog();
        } else {
            if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
                mLoadingDialog.show();
            }
        }
    }

    public void showLoadingDialog(String msg) {
        if (mActivity instanceof BaseActivity) {
            ((BaseActivity) mActivity).showLoadingDialog(msg);
        } else {
            if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
                mLoadingDialog.setLoadingMsg(msg);
                showLoadingDialog();
            }
        }
    }

    public void dismissLoadingDialog() {
        if (mActivity instanceof BaseActivity) {
            ((BaseActivity) mActivity).dismissLoadingDialog();
        } else {
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }
    }
}
