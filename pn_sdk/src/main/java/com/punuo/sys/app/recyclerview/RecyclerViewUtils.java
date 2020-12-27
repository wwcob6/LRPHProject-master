package com.punuo.sys.app.recyclerview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by han.chen.
 * Date on 2019-06-05.
 **/
public class RecyclerViewUtils {

    public static int findFirstVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
           return ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] info = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(info);
            return findMin(info);
        }
        return 0;
    }


    public static int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] info = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(info);
            return findMax(info);
        }
        return 0;
    }

    private static int findMin(int[] info) {
        int min = info[0];
        for (int value : info) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    private static int findMax(int[] info) {
        int max = info[0];
        for (int value : info) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
