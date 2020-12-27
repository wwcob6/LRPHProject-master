package com.punuo.sys.app.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-06-04.
 **/
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_HEADER = Integer.MIN_VALUE;
    private static final int VIEW_TYPE_FOOTER = Integer.MIN_VALUE + 1;
    protected View mHeaderView;
    protected View mFootView;
    private RecyclerView mRecyclerView;
    protected Context mContext;
    protected List<T> mData;
    private final Object lock = new Object();

    public BaseRecyclerViewAdapter(Context context, List<T> data) {
        mContext = context;
        mData = data;
        if (mData == null) {
            mData = new ArrayList<>();
        }
    }

    public List<T> getData() {
        return mData;
    }

    public void setHeaderView(View mHeadView) {
        this.mHeaderView = mHeadView;
    }

    public void setFooterView(View mFooterView) {
        this.mFootView = mFooterView;
    }

    public boolean hasFooterView() {
        return mFootView != null;
    }

    public boolean hasHeaderView() {
        return mHeaderView != null;
    }

    public boolean isHeaderView(int position) {
        return mHeaderView != null && position == 0;
    }

    public boolean isFooterView(int position) {
        return mFootView != null && position == getItemCount() - 1;
    }

    public void add(int location, T object) {
        synchronized (lock) {
            mData.add(location, object);
            notifyItemInserted(location + (hasHeaderView() ? 1 : 0));
        }
    }

    public boolean add(T object) {
        synchronized (lock) {
            int lastIndex = mData.size();
            if (mData.add(object)) {
                notifyItemInserted(lastIndex + (hasHeaderView() ? 1 : 0));
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean addAll(int location, Collection<? extends T> collection) {
        synchronized (lock) {
            if (mData.addAll(location, collection)) {
                notifyItemRangeInserted(location + (hasHeaderView() ? 1 : 0), collection.size());
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean addAll(Collection<? extends T> collection) {
        synchronized (lock) {
            int lastIndex = mData.size();
            if (mData.addAll(collection)) {
                notifyItemRangeInserted(lastIndex + (hasHeaderView() ? 1 : 0), collection.size());
                return true;
            } else {
                return false;
            }
        }
    }

    public void move(int from, int to) {
        synchronized (lock) {
            Collections.swap(mData, from, to);
            notifyItemMoved(from + (hasHeaderView() ? 1 : 0), to + (hasHeaderView() ? 1 : 0));
        }
    }

    public void remove(int position) {
        synchronized (lock) {
            mData.remove(position);
            notifyItemRemoved(position + (hasHeaderView() ? 1 : 0));
        }
    }

    public void clear() {
        synchronized (lock) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

    private ViewGroup getFootViewGroup() {
        ViewGroup footViewGroup = new LinearLayout(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        footViewGroup.setLayoutParams(params);

        return footViewGroup;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(mHeaderView);
        } else if (viewType == VIEW_TYPE_FOOTER) {
            return new FooterViewHolder(getFootViewGroup());
        }

        return onCreateBasicItemViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0 && holder.getItemViewType() == VIEW_TYPE_HEADER) {
            setFullSpanIfNeed(holder);
        } else if (position == getItemCount() - 1 && holder.getItemViewType() == VIEW_TYPE_FOOTER) {
            setFullSpanIfNeed(holder);
            ((ViewGroup) holder.itemView).removeAllViews();
            if (mFootView.getParent() != null)
                ((ViewGroup) mFootView.getParent()).removeAllViews();

            ((ViewGroup) holder.itemView).addView(mFootView);
        } else {
            int position1 = position - (mHeaderView != null ? 1 : 0);
            onBindBasicItemView(holder, position1);
        }
    }

    private void setFullSpanIfNeed(RecyclerView.ViewHolder holder) {
        if (mRecyclerView != null) {
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (layoutManager != null && layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(holder.itemView.getLayoutParams());
                params.setFullSpan(true);
                holder.itemView.setLayoutParams(params);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mHeaderView != null) {
            return VIEW_TYPE_HEADER;
        }
        if (position == getItemCount() - 1 && mFootView != null) {
            return VIEW_TYPE_FOOTER;
        }
        return getBasicItemType(position - (mHeaderView != null ? 1 : 0));
    }

    @Override
    public int getItemCount() {
        int itemCount = getBasicItemCount();
        if (mHeaderView != null) {
            itemCount += 1;
        }
        if (mFootView != null) {
            itemCount += 1;
        }
        return itemCount;
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    public int getPositionForItem(T item) {
        return mData.indexOf(item);
    }

    public boolean isEmpty() {
        return mData == null || mData.isEmpty();
    }

    public abstract RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindBasicItemView(RecyclerView.ViewHolder baseViewHolder, int position);

    public abstract int getBasicItemType(int position);

    public abstract int getBasicItemCount();

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }
}
