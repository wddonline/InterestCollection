package org.wdd.app.android.interestcollection.ui.images.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter;
import org.wdd.app.android.interestcollection.ui.images.model.Image;
import org.wdd.app.android.interestcollection.utils.AppUtils;
import org.wdd.app.android.interestcollection.utils.DensityUtils;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class ImageAdapter extends AbstractCommonAdapter<Image> {

    private int mItemWidth;
    private int mItemHeight;

    public ImageAdapter(Context context, List<Image> data) {
        super(context, data);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        mItemWidth = AppUtils.getScreenWidth(context) - padding * 2;
        mItemHeight = Math.round(mItemWidth / 2f);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_images_list, null);
        ImageViewHolder viewHolder = new ImageViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void onBindDataViewHolder(RecyclerView.ViewHolder holder, Image item, int position) {
        ImageViewHolder viewHolder = (ImageViewHolder) holder;
        viewHolder.imageView.getLayoutParams().width = mItemWidth;
        viewHolder.imageView.getLayoutParams().height = mItemHeight;
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        NetworkImageView imageView;
        TextView countView;
        View signView;
        TextView dateView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.item_images_list_title);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_images_list_img);
            countView = (TextView) itemView.findViewById(R.id.item_images_list_img_count);
            signView = itemView.findViewById(R.id.item_images_list_gif_sign);
            dateView = (TextView) itemView.findViewById(R.id.item_images_list_date);
        }
    }
}
