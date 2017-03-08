package org.wdd.app.android.interestcollection.ui.favorites.adapter.impl;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.database.model.ImageFavorite;
import org.wdd.app.android.interestcollection.ui.favorites.adapter.AbstractFavoritesAdapter;
import org.wdd.app.android.interestcollection.ui.images.model.Image;
import org.wdd.app.android.interestcollection.utils.AppUtils;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by richard on 11/28/16.
 */

public class ImageFavoritesAdapter extends AbstractFavoritesAdapter<ImageFavoritesAdapter.ImageItem> {

    private Mode mode = Mode.Normal;
    private ImageFavoritesCallback callback;

    private int selectedCount = 0;
    private int mItemWidth;
    private int mItemHeight;

    public ImageFavoritesAdapter(Context context, List<ImageItem> data) {
        super(context, data);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        mItemWidth = AppUtils.getScreenWidth(context) - padding * 2;
        mItemHeight = Math.round(mItemWidth / 2f);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_favorites, parent, false);
        RecyclerView.ViewHolder viewHolder = new ImageVH(view);
        return viewHolder;
    }

    @Override
    protected void onBindDataViewHolder(final RecyclerView.ViewHolder holder, final ImageItem item, final int position) {
        final ImageVH imageVH = (ImageVH) holder;
        ViewGroup.LayoutParams lp = imageVH.imageView.getLayoutParams();
        lp.width = mItemWidth;
        lp.height = mItemHeight;
        imageVH.titleView.setText(item.favorite.title);
        imageVH.imageView.setImageUrl(item.favorite.imgUrl);
        imageVH.countView.setText(item.favorite.imageCount);
        imageVH.gifView.setVisibility(item.favorite.gifFlag == 1 ? View.VISIBLE : View.GONE);
        imageVH.dateView.setText(item.favorite.time);
        imageVH.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mode) {
                    case Normal:
                        if (callback == null) return;
                        Image image = new Image();
                        image.url = item.favorite.url;
                        image.imgUrl = item.favorite.imgUrl;
                        image.title = item.favorite.title;
                        image.date = item.favorite.time;
                        image.imgCount = item.favorite.imageCount;
                        image.isGif = item.favorite.gifFlag == 1;
                        image.imgCount = item.favorite.imageCount;
                        callback.jumpToDetailActivity(item.favorite.id, image);
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
        imageVH.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mode = Mode.Select;
                notifyDataSetChanged();
                callback.switchSelectMode();
                return true;
            }
        });
        imageVH.checkBox.setVisibility(mode == Mode.Select ? View.VISIBLE : View.GONE);
        imageVH.checkBox.setChecked(item.isSelected);
    }

    public void unselectAll() {
        selectedCount = 0;
        for (ImageItem favorites : data) {
            favorites.isSelected = false;
        }
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (ImageItem favorites : data) {
            favorites.isSelected = true;
        }
        selectedCount = getLoadStatus() == LoadStatus.NoMore ? data.size() : data.size() - 1;
        notifyDataSetChanged();
    }

    public void setCallback(ImageFavoritesCallback callback) {
        this.callback = callback;
    }

    public List<ImageFavorite> getSelectedItem() {
        List<ImageFavorite> items = new ArrayList<>();
        for (ImageItem f : data) {
            if (f.isSelected) {
                items.add(f.favorite);
            }
        }
        return items;
    }

    public List<ImageItem> getSelectedOriginItem() {
        List<ImageItem> items = new ArrayList<>();
        for (ImageItem f : data) {
            if (f.isSelected) {
                items.add(f);
            }
        }
        return items;
    }

    public void removeDataById(int id) {
        Iterator<ImageItem> iterator = data.iterator();
        ImageItem item;
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
            for (ImageItem favorites : data) {
                favorites.isSelected = false;
            }
            selectedCount = 0;
        }
        notifyDataSetChanged();
    }

    public Mode getMode() {
        return mode;
    }

    private class ImageVH extends RecyclerView.ViewHolder {

        View rootView;
        CheckBox checkBox;
        TextView titleView;
        NetworkImageView imageView;
        TextView countView;
        View gifView;
        TextView dateView;

        public ImageVH(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.item_image_favorites_root);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_image_favorites_check);
            titleView = (TextView) itemView.findViewById(R.id.item_image_favorites_title);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_image_favorites_img);
            countView = (TextView) itemView.findViewById(R.id.item_image_favorites_img_count);
            gifView = itemView.findViewById(R.id.item_image_favorites_gif_sign);
            dateView = (TextView) itemView.findViewById(R.id.item_image_favorites_date);
        }
    }

    public static class ImageItem {

        boolean isSelected;
        public ImageFavorite favorite;

        public ImageItem() {
        }

        public ImageItem(boolean isSelected, ImageFavorite favorite) {
            this.isSelected = isSelected;
            this.favorite = favorite;
        }
    }

    public interface ImageFavoritesCallback {

        void jumpToDetailActivity(int id, Image image);
        void switchSelectMode();
        void onAllSelected();
        void onPartSelected();

    }
}
