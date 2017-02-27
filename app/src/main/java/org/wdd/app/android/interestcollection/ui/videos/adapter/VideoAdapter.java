package org.wdd.app.android.interestcollection.ui.videos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter;
import org.wdd.app.android.interestcollection.ui.videos.model.Video;
import org.wdd.app.android.interestcollection.utils.AppUtils;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class VideoAdapter extends AbstractCommonAdapter<Video> {

    private LayoutInflater mInflater;
    private OnItemClickedListener mListener;

    private int mItemWidth;
    private int mItemHeight;

    public VideoAdapter(Context context, List<Video> data) {
        super(context, data);
        mInflater = LayoutInflater.from(context);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        mItemWidth = AppUtils.getScreenWidth(context) - padding * 2;
        mItemHeight = Math.round(mItemWidth / 2f);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_videos_list, parent, false);
        VideoViewHolder viewHolder = new VideoViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void onBindDataViewHolder(RecyclerView.ViewHolder holder, final Video item, final int position) {
        VideoViewHolder viewHolder = (VideoViewHolder) holder;
        ViewGroup.LayoutParams lp = viewHolder.imageView.getLayoutParams();
        lp.width = mItemWidth;
        lp.height = mItemHeight;
        viewHolder.titleView.setText(item.title);
        viewHolder.imageView.setImageUrl(item.imgUrl);
        viewHolder.dateView.setText(item.date);
        viewHolder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onItemClicked(position, item);
            }
        });
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.mListener = listener;
    }

    private class VideoViewHolder extends RecyclerView.ViewHolder {

        View clickView;
        TextView titleView;
        NetworkImageView imageView;
        TextView dateView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            clickView = itemView.findViewById(R.id.item_videos_list_click);
            titleView = (TextView) itemView.findViewById(R.id.item_videos_list_title);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_videos_list_img);
            dateView = (TextView) itemView.findViewById(R.id.item_videos_list_date);
        }
    }

    public interface OnItemClickedListener {

        void onItemClicked(int position, Video item);

    }
}
