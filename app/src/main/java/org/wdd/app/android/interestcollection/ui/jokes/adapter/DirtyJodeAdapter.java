package org.wdd.app.android.interestcollection.ui.jokes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

    public DirtyJodeAdapter(Context context, List<DirtyJoke> data) {
        super(context, data);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_dirty_jokes_list, null);
        JokeViewHolder viewHolder = new JokeViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void onBindDataViewHolder(RecyclerView.ViewHolder holder, DirtyJoke item, int position) {
        JokeViewHolder viewHolder = (JokeViewHolder) holder;
        viewHolder.titleView.setText(item.title);
        viewHolder.dateView.setText(item.date);
        viewHolder.imageView.setImageUrl(item.imgUrl);
    }

    private class JokeViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        TextView dateView;
        NetworkImageView imageView;

        public JokeViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.item_dirty_jokes_list_title);
            dateView = (TextView) itemView.findViewById(R.id.item_dirty_jokes_list_date);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_dirty_jokes_list_img);
        }
    }
}
