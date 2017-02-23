package org.wdd.app.android.interestcollection.ui.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;

import java.util.List;

/**
 * Created by richard on 12/1/16.
 */

public abstract class AbstractCommonAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_DATA = -1;
    private final int TYPE_MORE = -2;

    public enum LoadStatus {
        Normal, Loading, NoMore
    }

    protected Context context;
    protected List<T> data;
    private AbstractCommonAdapter.OnLoadMoreListener loadMoreListener;
    private AbstractCommonAdapter.LoadStatus status = AbstractCommonAdapter.LoadStatus.Normal;

    public AbstractCommonAdapter(Context context) {
        this.context = context;
    }

    public AbstractCommonAdapter(Context context, List<T> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_MORE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_common_load_more_data, parent, false);
            viewHolder = new AbstractCommonAdapter.MoreDataVH(view);
        } else {
            viewHolder = onCreateDataViewHolder(parent, viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == TYPE_MORE) {
            AbstractCommonAdapter.MoreDataVH moreDataVH = (AbstractCommonAdapter.MoreDataVH) holder;
            switch (status) {
                case Normal:
                    moreDataVH.outerView.setVisibility(View.VISIBLE);
                    moreDataVH.loadBtn.setVisibility(View.VISIBLE);
                    moreDataVH.loadingLayout.setVisibility(View.GONE);
                    break;
                case Loading:
                    moreDataVH.outerView.setVisibility(View.VISIBLE);
                    moreDataVH.loadBtn.setVisibility(View.GONE);
                    moreDataVH.loadingLayout.setVisibility(View.VISIBLE);
                    break;
                case NoMore:
                    moreDataVH.outerView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        } else {
            T item = data.get(position);
            onBindDataViewHolder(holder, item, position);
        }
    }

    @Override
    public int getItemCount() {
        if (status == LoadStatus.NoMore) return data.size();
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (status == LoadStatus.NoMore) return getSubItemViewType(position);
        return position == data.size() ? TYPE_MORE : getSubItemViewType(position);
    }

    protected int getSubItemViewType(int position) {
        return TYPE_DATA;
    }

    public void setOnLoadMoreListener(AbstractCommonAdapter.OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void setLoadStatus(AbstractCommonAdapter.LoadStatus status) {
        this.status = status;
        notifyItemChanged(data.size());
    }

    protected void setStatus(AbstractCommonAdapter.LoadStatus status) {
        this.status = status;
    }

    public LoadStatus getLoadStatus() {
        return status;
    }

    protected abstract RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindDataViewHolder(RecyclerView.ViewHolder holder, T item, int position);

    private class MoreDataVH extends RecyclerView.ViewHolder {

        private View outerView;
        TextView loadBtn;
        View loadingLayout;

        public MoreDataVH(View itemView) {
            super(itemView);
            outerView = itemView.findViewById(R.id.item_common_load_more_data_outer);
            loadBtn = (TextView) itemView.findViewById(R.id.item_common_load_more_data_btn);
            loadingLayout = itemView.findViewById(R.id.item_common_load_more_data_loading);
            loadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = AbstractCommonAdapter.LoadStatus.Loading;
                    notifyItemChanged(data.size());
                    if (loadMoreListener != null) loadMoreListener.onLoadMore();
                }
            });
        }
    }

    public interface OnLoadMoreListener {

        void onLoadMore();

    }

}
