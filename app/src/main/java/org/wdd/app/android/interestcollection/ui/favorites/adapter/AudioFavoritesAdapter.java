package org.wdd.app.android.interestcollection.ui.favorites.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.database.model.AudioFavorite;
import org.wdd.app.android.interestcollection.ui.audios.model.Audio;
import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 11/28/16.
 */

public class AudioFavoritesAdapter extends AbstractCommonAdapter<AudioFavoritesAdapter.AudioItem> {

    public enum Mode {
        Normal,
        Select
    }

    private Mode mode = Mode.Normal;
    private AudioFavoritesCallback callback;

    private int selectedCount = 0;

    public AudioFavoritesAdapter(Context context, List<AudioItem> data) {
        super(context, data);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_audio_favorites, parent, false);
        RecyclerView.ViewHolder viewHolder = new GirlVH(view);
        return viewHolder;
    }

    @Override
    protected void onBindDataViewHolder(final RecyclerView.ViewHolder holder, final AudioItem item, final int position) {
        final GirlVH hospitalVH = (GirlVH) holder;
        hospitalVH.titleView.setText(item.favorite.title);
        hospitalVH.dateView.setText(item.favorite.time);
        hospitalVH.imageView.setImageUrl(item.favorite.imgUrl);
        hospitalVH.rootView.setOnClickListener(new View.OnClickListener() {
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
                        callback.jumpToDetailActivity(position, audio);
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
        hospitalVH.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mode = Mode.Select;
                notifyDataSetChanged();
                callback.switchSelectMode();
                return true;
            }
        });
        hospitalVH.checkBox.setVisibility(mode == Mode.Select ? View.VISIBLE : View.GONE);
        hospitalVH.checkBox.setChecked(item.isSelected);
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

    private class GirlVH extends RecyclerView.ViewHolder {

        View rootView;
        CheckBox checkBox;
        TextView titleView;
        TextView dateView;
        NetworkImageView imageView;

        public GirlVH(View itemView) {
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

        void jumpToDetailActivity(int position, Audio audio);
        void switchSelectMode();
        void onAllSelected();
        void onPartSelected();

    }
}
