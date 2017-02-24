package org.wdd.app.android.interestcollection.ui.jokes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJoke;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class DirtyJodeAdapter extends AbstractCommonAdapter<DirtyJoke> {

    private LayoutInflater mInflater;
    private OnItemClickedListener mListener;

    public DirtyJodeAdapter(Context context, List<DirtyJoke> data) {
        super(context, data);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_dirty_jokes_list, parent, false);
        JokeViewHolder viewHolder = new JokeViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void onBindDataViewHolder(RecyclerView.ViewHolder holder, final DirtyJoke item, final int position) {
        JokeViewHolder viewHolder = (JokeViewHolder) holder;
        viewHolder.titleView.setText(item.title);
        viewHolder.dateView.setText(item.date);
        viewHolder.imageView.setImageUrl(item.imgUrl);
        viewHolder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener == null) return;
                mListener.onItemClicked(position, item);
            }
        });
    }

    private class JokeViewHolder extends RecyclerView.ViewHolder {

        View clickView;
        TextView titleView;
        TextView dateView;
        NetworkImageView imageView;

        public JokeViewHolder(View itemView) {
            super(itemView);
            clickView = itemView.findViewById(R.id.item_dirty_jokes_list_click);
            titleView = (TextView) itemView.findViewById(R.id.item_dirty_jokes_list_title);
            dateView = (TextView) itemView.findViewById(R.id.item_dirty_jokes_list_date);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_dirty_jokes_list_img);
        }
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickedListener {

        void onItemClicked(int position, DirtyJoke item);

    }
}
