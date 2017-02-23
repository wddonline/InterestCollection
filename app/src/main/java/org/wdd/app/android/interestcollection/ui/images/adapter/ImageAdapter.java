package org.wdd.app.android.interestcollection.ui.images.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter;
import org.wdd.app.android.interestcollection.ui.images.model.Image;
import org.wdd.app.android.interestcollection.utils.AppUtils;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class ImageAdapter extends AbstractCommonAdapter<Image> {

    private LayoutInflater mInflater;

    private int mItemWidth;
    private int mItemHeight;

    public ImageAdapter(Context context, List<Image> data) {
        super(context, data);
        mInflater = LayoutInflater.from(context);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        mItemWidth = AppUtils.getScreenWidth(context) - padding * 2;
        mItemHeight = Math.round(mItemWidth / 2f);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_images_list, parent, false);
        ImageViewHolder viewHolder = new ImageViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void onBindDataViewHolder(RecyclerView.ViewHolder holder, Image item, int position) {
        ImageViewHolder viewHolder = (ImageViewHolder) holder;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewHolder.imageView.getLayoutParams();
        lp.width = mItemWidth;
        lp.height = mItemHeight;
        viewHolder.titleView.setText(item.title);
        viewHolder.imageView.setImageUrl(item.imgUrl);
        viewHolder.countView.setText(item.imgCount);
        viewHolder.gifView.setVisibility(item.isGif ? View.VISIBLE : View.GONE);
        viewHolder.dateView.setText(item.date);
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        NetworkImageView imageView;
        TextView countView;
        View gifView;
        TextView dateView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.item_images_list_title);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_images_list_img);
            countView = (TextView) itemView.findViewById(R.id.item_images_list_img_count);
            gifView = itemView.findViewById(R.id.item_images_list_gif_sign);
            dateView = (TextView) itemView.findViewById(R.id.item_images_list_date);
        }
    }
}
