package com.app.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

/**
 * Author chzjy
 * Date 2016/12/19.
 * 实现ViewPager页卡
 */

public class ApplicationAdapter extends PagerAdapter {
    private List<GridView> gridViewList;

    public ApplicationAdapter(List<GridView> gridViewList) {
        this.gridViewList = gridViewList;
    }

    @Override
    public int getCount() {
        return gridViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(gridViewList.get(position));
        return gridViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
