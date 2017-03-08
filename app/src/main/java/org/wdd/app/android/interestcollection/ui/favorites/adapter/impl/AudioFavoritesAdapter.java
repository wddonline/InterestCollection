package org.wdd.app.android.interestcollection.ui.favorites.adapter.impl;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.database.model.AudioFavorite;
import org.wdd.app.android.interestcollection.ui.audios.model.Audio;
import org.wdd.app.android.interestcollection.ui.favorites.adapter.AbstractFavoritesAdapter;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by richard on 11/28/16.
 */

public class AudioFavoritesAdapter extends AbstractFavoritesAdapter<AudioFavoritesAdapter.AudioItem> {

    private Mode mode = Mode.Normal;
    private AudioFavoritesCallback callback;

    private int selectedCount = 0;

    public AudioFavoritesAdapter(Context context, List<AudioItem> data) {
        super(context, data);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_audio_favorites, parent, false);
        RecyclerView.ViewHolder viewHolder = new AudioVH(view);
        return viewHolder;
    }

    @Override
    protected void onBindDataViewHolder(final RecyclerView.ViewHolder holder, final AudioItem item, final int position) {
        final AudioVH audioVH = (AudioVH) holder;
        audioVH.titleView.setText(item.favorite.title);
        audioVH.dateView.setText(item.favorite.time);
        audioVH.imageView.setImageUrl(item.favorite.imgUrl);
        audioVH.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mode) {
                    case Normal:
                        if (callback == null) return;
                        Audio audio = new Audio();
                        audio.url = item.favorite.url;
                        audio.imgUrl = item.favorite.imgUrl;
                        audio.title = item.favorite.title;
                        audio.date = item.favorite.time;
                        callback.jumpToDetailActivity(item.favorite.id, audio);
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
        audioVH.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mode = Mode.Select;
                notifyDataSetChanged();
                callback.switchSelectMode();
                return true;
            }
        });
        audioVH.checkBox.setVisibility(mode == Mode.Select ? View.VISIBLE : View.GONE);
        audioVH.checkBox.setChecked(item.isSelected);
    }

    public void unselectAll() {
        selectedCount = 0;
        for (AudioItem favorites : data) {
            favorites.isSelected = false;
        }
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (AudioItem favorites : data) {
            favorites.isSelected = true;
        }
        selectedCount = getLoadStatus() == LoadStatus.NoMore ? data.size() : data.size() - 1;
        notifyDataSetChanged();
    }

    public void setCallback(AudioFavoritesCallback callback) {
        this.callback = callback;
    }

    public List<AudioFavorite> getSelectedItem() {
        List<AudioFavorite> items = new ArrayList<>();
        for (AudioItem f : data) {
            if (f.isSelected) {
                items.add(f.favorite);
            }
        }
        return items;
    }

    public List<AudioItem> getSelectedOriginItem() {
        List<AudioItem> items = new ArrayList<>();
        for (AudioItem f : data) {
            if (f.isSelected) {
                items.add(f);
            }
        }
        return items;
    }

    public void removeDataById(int id) {
        Iterator<AudioItem> iterator = data.iterator();
        AudioItem item;
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
            for (AudioItem favorites : data) {
                favorites.isSelected = false;
            }
            selectedCount = 0;
        }
        notifyDataSetChanged();
    }

    public Mode getMode() {
        return mode;
    }

    private class AudioVH extends RecyclerView.ViewHolder {

        View rootView;
        CheckBox checkBox;
        TextView titleView;
        TextView dateView;
        NetworkImageView imageView;

        public AudioVH(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.item_audio_favorites_root);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_audio_favorites_check);
            titleView = (TextView) itemView.findViewById(R.id.item_audio_favorites_title);
            dateView = (TextView) itemView.findViewById(R.id.item_audio_favorites_date);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_audio_favorites_img);
        }
    }

    public static class AudioItem {

        boolean isSelected;
        public AudioFavorite favorite;

        public AudioItem() {
        }

        public AudioItem(boolean isSelected, AudioFavorite favorite) {
            this.isSelected = isSelected;
            this.favorite = favorite;
        }
    }

    public interface AudioFavoritesCallback {

        void jumpToDetailActivity(int id, Audio audio);
        void switchSelectMode();
        void onAllSelected();
        void onPartSelected();

    }
}
