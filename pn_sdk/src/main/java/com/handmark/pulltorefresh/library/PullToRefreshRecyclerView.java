package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.punuo.sys.app.R;

/**
 * Created by han.chen.
 * Date on 2019-06-04.
 **/
public class PullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {
    public PullToRefreshRecyclerView(Context context) {
        super(context);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView recyclerView;
        recyclerView = new RecyclerView(context, attrs);
        recyclerView.setId(R.id.recyclerview);
        return recyclerView;
    }

    /**
     * 这段代码可能有问题，没用到先不改
     *
     * @return
     */
    @Override
    protected boolean isReadyForPullEnd() {
        int lastVisiblePosition = mRefreshableView.getChildPosition(mRefreshableView.getChildAt(mRefreshableView.getChildCount() - 1));
        if (lastVisiblePosition >= mRefreshableView.getAdapter().getItemCount() - 1) {
            return mRefreshableView.getChildAt(mRefreshableView.getChildCount() - 1).getBottom() <= mRefreshableView.getBottom();
        }
        return false;
    }

    @Override
    protected boolean isReadyForPullStart() {
        final RecyclerView.Adapter adapter = mRefreshableView.getAdapter();
        if (null == adapter || adapter.getItemCount() == 0) {
            return true;
        } else {
            final View firstVisibleChild = mRefreshableView.getChildAt(0);
            if (mRefreshableView.getChildPosition(firstVisibleChild) == 0) {
                return firstVisibleChild.getTop() >= mRefreshableView.getTop();
            }
        }
        return false;
    }
}
