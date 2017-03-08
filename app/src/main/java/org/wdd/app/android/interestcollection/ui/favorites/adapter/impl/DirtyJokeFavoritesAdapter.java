package org.wdd.app.android.interestcollection.ui.favorites.adapter.impl;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.database.model.DirtyJokeFavorite;
import org.wdd.app.android.interestcollection.ui.favorites.adapter.AbstractFavoritesAdapter;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJoke;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by richard on 11/28/16.
 */

public class DirtyJokeFavoritesAdapter extends AbstractFavoritesAdapter<DirtyJokeFavoritesAdapter.DirtyJokeItem> {

    private Mode mode = Mode.Normal;
    private DirtyJokeFavoritesCallback callback;

    private int selectedCount = 0;

    public DirtyJokeFavoritesAdapter(Context context, List<DirtyJokeItem> data) {
        super(context, data);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dirty_joke_favorites, parent, false);
        RecyclerView.ViewHolder viewHolder = new DirtyJokeVH(view);
        return viewHolder;
    }

    @Override
    protected void onBindDataViewHolder(final RecyclerView.ViewHolder holder, final DirtyJokeItem item, final int position) {
        final DirtyJokeVH dirtyJokeVH = (DirtyJokeVH) holder;
        dirtyJokeVH.titleView.setText(item.favorite.title);
        dirtyJokeVH.dateView.setText(item.favorite.time);
        dirtyJokeVH.imageView.setImageUrl(item.favorite.imgUrl);
        dirtyJokeVH.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mode) {
                    case Normal:
                        if (callback == null) return;
                        DirtyJoke joke = new DirtyJoke();
                        joke.url = item.favorite.url;
                        joke.imgUrl = item.favorite.imgUrl;
                        joke.title = item.favorite.title;
                        joke.date = item.favorite.time;
                        callback.jumpToDetailActivity(item.favorite.id, joke);
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
        dirtyJokeVH.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mode = Mode.Select;
                notifyDataSetChanged();
                callback.switchSelectMode();
                return true;
            }
        });
        dirtyJokeVH.checkBox.setVisibility(mode == Mode.Select ? View.VISIBLE : View.GONE);
        dirtyJokeVH.checkBox.setChecked(item.isSelected);
    }

    public void unselectAll() {
        selectedCount = 0;
        for (DirtyJokeItem favorites : data) {
            favorites.isSelected = false;
        }
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (DirtyJokeItem favorites : data) {
            favorites.isSelected = true;
        }
        selectedCount = getLoadStatus() == LoadStatus.NoMore ? data.size() : data.size() - 1;
        notifyDataSetChanged();
    }

    public void setCallback(DirtyJokeFavoritesCallback callback) {
        this.callback = callback;
    }

    public List<DirtyJokeFavorite> getSelectedItem() {
        List<DirtyJokeFavorite> items = new ArrayList<>();
        for (DirtyJokeItem f : data) {
            if (f.isSelected) {
                items.add(f.favorite);
            }
        }
        return items;
    }

    public List<DirtyJokeItem> getSelectedOriginItem() {
        List<DirtyJokeItem> items = new ArrayList<>();
        for (DirtyJokeItem f : data) {
            if (f.isSelected) {
                items.add(f);
            }
        }
        return items;
    }

    public void removeDataById(int id) {
        Iterator<DirtyJokeItem> iterator = data.iterator();
        DirtyJokeItem item;
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
            for (DirtyJokeItem favorites : data) {
                favorites.isSelected = false;
            }
            selectedCount = 0;
        }
        notifyDataSetChanged();
    }

    public Mode getMode() {
        return mode;
    }

    private class DirtyJokeVH extends RecyclerView.ViewHolder {

        View rootView;
        CheckBox checkBox;
        TextView titleView;
        TextView dateView;
        NetworkImageView imageView;

        public DirtyJokeVH(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.item_dirty_joke_favorites_root);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_dirty_joke_favorites_check);
            titleView = (TextView) itemView.findViewById(R.id.item_dirty_joke_favorites_title);
            dateView = (TextView) itemView.findViewById(R.id.item_dirty_joke_favorites_date);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_dirty_joke_favorites_img);
        }
    }

    public static class DirtyJokeItem {

        boolean isSelected;
        public DirtyJokeFavorite favorite;

        public DirtyJokeItem() {
        }

        public DirtyJokeItem(boolean isSelected, DirtyJokeFavorite favorite) {
            this.isSelected = isSelected;
            this.favorite = favorite;
        }
    }

    public interface DirtyJokeFavoritesCallback {

        void jumpToDetailActivity(int id, DirtyJoke joke);
        void switchSelectMode();
        void onAllSelected();
        void onPartSelected();

    }
}
