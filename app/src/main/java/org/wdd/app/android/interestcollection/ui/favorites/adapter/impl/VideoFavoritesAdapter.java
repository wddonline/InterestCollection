package org.wdd.app.android.interestcollection.ui.favorites.adapter.impl;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.database.model.VideoFavorite;
import org.wdd.app.android.interestcollection.ui.favorites.adapter.AbstractFavoritesAdapter;
import org.wdd.app.android.interestcollection.ui.videos.model.Video;
import org.wdd.app.android.interestcollection.utils.AppUtils;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by richard on 11/28/16.
 */

public class VideoFavoritesAdapter extends AbstractFavoritesAdapter<VideoFavoritesAdapter.VideoItem> {

    private Mode mode = Mode.Normal;
    private VideoFavoritesCallback callback;

    private int selectedCount = 0;
    private int mItemWidth;
    private int mItemHeight;

    public VideoFavoritesAdapter(Context context, List<VideoItem> data) {
        super(context, data);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        mItemWidth = AppUtils.getScreenWidth(context) - padding * 2;
        mItemHeight = Math.round(mItemWidth / 2f);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_favorites, parent, false);
        RecyclerView.ViewHolder viewHolder = new VideoVH(view);
        return viewHolder;
    }

    @Override
    protected void onBindDataViewHolder(final RecyclerView.ViewHolder holder, final VideoItem item, final int position) {
        final VideoVH videoVH = (VideoVH) holder;
        ViewGroup.LayoutParams lp = videoVH.imageView.getLayoutParams();
        lp.width = mItemWidth;
        lp.height = mItemHeight;
        videoVH.titleView.setText(item.favorite.title);
        videoVH.imageView.setImageUrl(item.favorite.imgUrl);
        videoVH.dateView.setText(item.favorite.time);
        videoVH.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mode) {
                    case Normal:
                        if (callback == null) return;
                        Video video = new Video();
                        video.url = item.favorite.url;
                        video.imgUrl = item.favorite.imgUrl;
                        video.title = item.favorite.title;
                        video.date = item.favorite.time;
                        callback.jumpToDetailActivity(item.favorite.id, video);
                        break;
                    case Select:
                        item.isSelected = !item.isSelected;
                        if (item.isSelected) {
                            selectedCount++;
                        } else {
                            selectedCount--;
                        }
                        int size = getLoadStatus() == LoadStatus.NoMore ? data.size() : data.size() - 1;
                        if (selectedCount == size) {
                            if (callback != null) callback.onAllSelected();
                        } else {
                            if (callback != null) callback.onPartSelected();
                        }
                        notifyItemChanged(position);
                        break;
                }
            }
        });
        videoVH.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mode = Mode.Select;
                notifyDataSetChanged();
                callback.switchSelectMode();
                return true;
            }
        });
        videoVH.checkBox.setVisibility(mode == Mode.Select ? View.VISIBLE : View.GONE);
        videoVH.checkBox.setChecked(item.isSelected);
    }

    public void unselectAll() {
        selectedCount = 0;
        for (VideoItem favorites : data) {
            favorites.isSelected = false;
        }
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (VideoItem favorites : data) {
            favorites.isSelected = true;
        }
        selectedCount = getLoadStatus() == LoadStatus.NoMore ? data.size() : data.size() - 1;
        notifyDataSetChanged();
    }

    public void setCallback(VideoFavoritesCallback callback) {
        this.callback = callback;
    }

    public List<VideoFavorite> getSelectedItem() {
        List<VideoFavorite> items = new ArrayList<>();
        for (VideoItem f : data) {
            if (f.isSelected) {
                items.add(f.favorite);
            }
        }
        return items;
    }

    public List<VideoItem> getSelectedOriginItem() {
        List<VideoItem> items = new ArrayList<>();
        for (VideoItem f : data) {
            if (f.isSelected) {
                items.add(f);
            }
        }
        return items;
    }

    public void removeDataById(int id) {
        Iterator<VideoItem> iterator = data.iterator();
        VideoItem item;
        int position = 0;
        while (iterator.hasNext()) {
            item = iterator.next();
            if (item.favorite.id == id) {
                iterator.remove();
                notifyItemRemoved(position);
                break;
            }
            position++;
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        if (mode == Mode.Normal) {
            for (VideoItem favorites : data) {
                favorites.isSelected = false;
            }
            selectedCount = 0;
        }
        notifyDataSetChanged();
    }

    public Mode getMode() {
        return mode;
    }

    private class VideoVH extends RecyclerView.ViewHolder {

        View rootView;
        CheckBox checkBox;
        TextView titleView;
        NetworkImageView imageView;
        TextView dateView;

        public VideoVH(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.item_video_favorites_root);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_video_favorites_check);
            titleView = (TextView) itemView.findViewById(R.id.item_video_favorites_title);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_video_favorites_img);
            dateView = (TextView) itemView.findViewById(R.id.item_video_favorites_date);
        }
    }

    public static class VideoItem {

        boolean isSelected;
        public VideoFavorite favorite;

        public VideoItem() {
        }

        public VideoItem(boolean isSelected, VideoFavorite favorite) {
            this.isSelected = isSelected;
            this.favorite = favorite;
        }
    }

    public interface VideoFavoritesCallback {

        void jumpToDetailActivity(int id, Video video);
        void switchSelectMode();
        void onAllSelected();
        void onPartSelected();

    }
}
