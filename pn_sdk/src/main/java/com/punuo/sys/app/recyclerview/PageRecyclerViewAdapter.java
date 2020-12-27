package com.punuo.sys.app.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.punuo.sys.app.R;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-06-05.
 **/
public abstract class PageRecyclerViewAdapter<T> extends BaseRecyclerViewAdapter<T> implements OnLoadMoreListener {
    private static final int DEFAULT_PRELOAD_NUM = 1;
    private int mPreLoadNum = DEFAULT_PRELOAD_NUM;

    private boolean isLoading;
    private boolean isError;
    private View mMoreFooterView;
    private View mErrorFooterView;
    private RecyclerView mRecyclerView;
    private OnLoadMoreHelper mOnLoadMoreHelper;

    private CompletedFooter mCompletedFooter;
    public PageRecyclerViewAdapter(Context context, List<T> data) {
        super(context, data);
        mCompletedFooter = new CompletedFooter(context);
    }

    public void setOnLoadMoreHelper(OnLoadMoreHelper onLoadMoreHelper) {
        mOnLoadMoreHelper = onLoadMoreHelper;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (mOnLoadMoreHelper == null || recyclerView.getLayoutManager() == null) {
            return;
        }
        enableLoadMore(recyclerView);
    }

    public void enableLoadMore(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx == 0 && dy == 0) {
                    return;
                }
                super.onScrolled(recyclerView, dx, dy);
                if (mOnLoadMoreHelper != null) {
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItem = RecyclerViewUtils.findLastVisibleItemPosition(layoutManager);
                    if (!isLoading && !isError && totalItemCount <= lastVisibleItem + mPreLoadNum) {
                        if (mOnLoadMoreHelper.canLoadMore()) {
                            if (mFootView == null) {
                                addFooterView(false);
                            }
                            isLoading = true;
                            mOnLoadMoreHelper.onLoadMore();
                        }
                    }
                }
            }
        });
    }

    public void setMoreFooterView(View view) {
        mMoreFooterView = view;
    }

    public void setErrorFooterView(View view) {
        mErrorFooterView = view;
    }

    protected View generateErrorFooterView() {
        if (mErrorFooterView == null) {
            mErrorFooterView = LayoutInflater.from(mContext).inflate(R.layout.manual_loading_view, mRecyclerView, false);
            mErrorFooterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PageRecyclerViewAdapter.super.setFooterView(generateMoreFooterView());
                    notifyItemChanged(getItemCount() - 1);
                    mOnLoadMoreHelper.onLoadMore();
                }
            });
        }
        return mErrorFooterView;
    }

    protected View generateMoreFooterView() {
        if (mMoreFooterView == null) {
            mMoreFooterView = LayoutInflater.from(mContext).inflate(R.layout.loading_view, mRecyclerView, false);
        }
        return mMoreFooterView;
    }


    private void removeFooterView() {
        boolean hasFooter = mFootView != null;
        if (hasFooter) {
            notifyItemRemoved(getItemCount() - 1);
            super.setFooterView(null);
        }
    }

    private void addCompletedFooterView() {
        View view;

        view = mCompletedFooter.generateCompletedFooterView(mContext, mRecyclerView);
        if (mFootView == null) {
            super.setFooterView(view);
            notifyItemInserted(getItemCount());
        } else {
            super.setFooterView(view);
            notifyItemChanged(getItemCount() - 1);
        }
    }

    private void addFooterView(boolean error) {
        View view;
        if (error) {
            view = generateErrorFooterView();
        } else {
            view = generateMoreFooterView();
        }
        if (mFootView == null) {
            super.setFooterView(view);
            notifyItemInserted(getItemCount());
        } else {
            super.setFooterView(view);
            notifyItemChanged(getItemCount() - 1);
        }
    }

    @Override
    public void onLoadMoreCompleted() {
        isLoading = false;
        isError = false;
        if (mOnLoadMoreHelper == null || !mOnLoadMoreHelper.canLoadMore()) {
            removeFooterView();
            if (mCompletedFooter != null && mCompletedFooter.isCompletedFooterEnabled()) {
                addCompletedFooterView();
            }
        }
    }

    @Override
    public void onLoadMoreFailed() {
        isLoading = false;
        isError = true;
        if (mOnLoadMoreHelper != null && mOnLoadMoreHelper.canLoadMore()) {
            addFooterView(true);
        } else {
            removeFooterView();
        }
    }

    public void setCompletedFooterListener(CompletedFooter.CompletedFooterListener listener) {
        if (mCompletedFooter != null) {
            mCompletedFooter.setCompletedFooterListener(listener);
        }
    }

    @Override
    public int getBasicItemType(int position) {
        return 0;
    }

    @Override
    public int getBasicItemCount() {
        return mData == null ? 0 : mData.size();
    }
}
