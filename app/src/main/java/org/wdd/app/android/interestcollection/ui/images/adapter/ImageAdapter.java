package org.wdd.app.android.interestcollection.ui.images.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private OnItemClickedListener mListener;

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
    protected void onBindDataViewHolder(RecyclerView.ViewHolder holder, final Image item, final int position) {
        ImageViewHolder viewHolder = (ImageViewHolder) holder;
        ViewGroup.LayoutParams lp = viewHolder.imageView.getLayoutParams();
        lp.width = mItemWidth;
        lp.height = mItemHeight;
        viewHolder.titleView.setText(item.title);
        viewHolder.imageView.setImageUrl(item.imgUrl);
        viewHolder.countView.setText(item.imgCount);
        viewHolder.gifView.setVisibility(item.isGif ? View.VISIBLE : View.GONE);
        viewHolder.dateView.setText(item.date);
        viewHolder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener == null) return;
                mListener.onItemClicked(position, item);
            }
        });
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {

        View clickView;
        TextView titleView;
        NetworkImageView imageView;
        TextView countView;
        View gifView;
        TextView dateView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            clickView = itemView.findViewById(R.id.item_images_list_click);
            titleView = (TextView) itemView.findViewById(R.id.item_images_list_title);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_images_list_img);
            countView = (TextView) itemView.findViewById(R.id.item_images_list_img_count);
            gifView = itemView.findViewById(R.id.item_images_list_gif_sign);
            dateView = (TextView) itemView.findViewById(R.id.item_images_list_date);
        }
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickedListener {

        void onItemClicked(int position, Image item);

    }
}
