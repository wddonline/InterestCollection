package org.wdd.app.android.interestcollection.ui.favorites.adapter.impl;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.database.model.ShareFavorite;
import org.wdd.app.android.interestcollection.ui.favorites.adapter.AbstractFavoritesAdapter;
import org.wdd.app.android.interestcollection.ui.shares.model.Share;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by richard on 11/28/16.
 */

public class ShareFavoritesAdapter extends AbstractFavoritesAdapter<ShareFavoritesAdapter.ShareItem> {

    private Mode mode = Mode.Normal;
    private ShareFavoritesCallback callback;

    private int selectedCount = 0;

    public ShareFavoritesAdapter(Context context, List<ShareItem> data) {
        super(context, data);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_share_favorites, parent, false);
        RecyclerView.ViewHolder viewHolder = new ShareVH(view);
        return viewHolder;
    }

    @Override
    protected void onBindDataViewHolder(final RecyclerView.ViewHolder holder, final ShareItem item, final int position) {
        final ShareVH shareVH = (ShareVH) holder;
        shareVH.titleView.setText(item.favorite.title);
        shareVH.dateView.setText(item.favorite.time);
        shareVH.imageView.setImageUrl(item.favorite.imgUrl);
        shareVH.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mode) {
                    case Normal:
                        if (callback == null) return;
                        Share share = new Share();
                        share.url = item.favorite.url;
                        share.imgUrl = item.favorite.imgUrl;
                        share.title = item.favorite.title;
                        share.date = item.favorite.time;
                        callback.jumpToDetailActivity(item.favorite.id, share);
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
        shareVH.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mode = Mode.Select;
                notifyDataSetChanged();
                callback.switchSelectMode();
                return true;
            }
        });
        shareVH.checkBox.setVisibility(mode == Mode.Select ? View.VISIBLE : View.GONE);
        shareVH.checkBox.setChecked(item.isSelected);
    }

    public void unselectAll() {
        selectedCount = 0;
        for (ShareItem favorites : data) {
            favorites.isSelected = false;
        }
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (ShareItem favorites : data) {
            favorites.isSelected = true;
        }
        selectedCount = getLoadStatus() == LoadStatus.NoMore ? data.size() : data.size() - 1;
        notifyDataSetChanged();
    }

    public void setCallback(ShareFavoritesCallback callback) {
        this.callback = callback;
    }

    public List<ShareFavorite> getSelectedItem() {
        List<ShareFavorite> items = new ArrayList<>();
        for (ShareItem f : data) {
            if (f.isSelected) {
                items.add(f.favorite);
            }
        }
        return items;
    }

    public List<ShareItem> getSelectedOriginItem() {
        List<ShareItem> items = new ArrayList<>();
        for (ShareItem f : data) {
            if (f.isSelected) {
                items.add(f);
            }
        }
        return items;
    }

    public void removeDataById(int id) {
        Iterator<ShareItem> iterator = data.iterator();
        ShareItem item;
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
            for (ShareItem favorites : data) {
                favorites.isSelected = false;
            }
            selectedCount = 0;
        }
        notifyDataSetChanged();
    }

    public Mode getMode() {
        return mode;
    }

    private class ShareVH extends RecyclerView.ViewHolder {

        View rootView;
        CheckBox checkBox;
        TextView titleView;
        TextView dateView;
        NetworkImageView imageView;

        public ShareVH(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.item_share_favorites_root);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_share_favorites_check);
            titleView = (TextView) itemView.findViewById(R.id.item_share_favorites_title);
            dateView = (TextView) itemView.findViewById(R.id.item_share_favorites_date);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_share_favorites_img);
        }
    }

    public static class ShareItem {

        boolean isSelected;
        public ShareFavorite favorite;

        public ShareItem() {
        }

        public ShareItem(boolean isSelected, ShareFavorite favorite) {
            this.isSelected = isSelected;
            this.favorite = favorite;
        }
    }

    public interface ShareFavoritesCallback {

        void jumpToDetailActivity(int id, Share share);
        void switchSelectMode();
        void onAllSelected();
        void onPartSelected();

    }
}
