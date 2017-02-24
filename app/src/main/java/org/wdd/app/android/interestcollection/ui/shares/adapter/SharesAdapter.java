package org.wdd.app.android.interestcollection.ui.shares.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.audios.model.Audio;
import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter;
import org.wdd.app.android.interestcollection.ui.shares.model.Share;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class SharesAdapter extends AbstractCommonAdapter<Share> {

    private LayoutInflater mInflater;

    public SharesAdapter(Context context, List<Share> data) {
        super(context, data);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_shares_list, parent, false);
        ShareViewHolder viewHolder = new ShareViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void onBindDataViewHolder(RecyclerView.ViewHolder holder, Share item, int position) {
        ShareViewHolder viewHolder = (ShareViewHolder) holder;
        viewHolder.titleView.setText(item.title);
        viewHolder.dateView.setText(item.date);
        viewHolder.imageView.setImageUrl(item.imgUrl);
    }

    private class ShareViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        TextView dateView;
        NetworkImageView imageView;

        public ShareViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.item_shares_list_title);
            dateView = (TextView) itemView.findViewById(R.id.item_shares_list_date);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_shares_list_img);
        }
    }
}
