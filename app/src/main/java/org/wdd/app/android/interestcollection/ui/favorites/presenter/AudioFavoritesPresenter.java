package org.wdd.app.android.interestcollection.ui.favorites.presenter;

import org.wdd.app.android.interestcollection.database.model.AudioFavorite;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.favorites.data.AudioFavoritesDataGetter;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.impl.AudioFavoritesFragment;

import java.util.List;

/**
 * Created by richard on 1/24/17.
 */

public class AudioFavoritesPresenter implements BasePresenter, AudioFavoritesDataGetter.DataCallback {

    private AudioFavoritesFragment mView;
    private AudioFavoritesDataGetter mDataGetter;

    public AudioFavoritesPresenter(AudioFavoritesFragment view) {
        this.mView = view;
        mDataGetter = new AudioFavoritesDataGetter(view.getContext(), this, view.host);
    }

    public void getAudioFavorites() {
        mDataGetter.queryAudioFavorites();
    }

    public void deleteSelectedFavorites(List<AudioFavorite> selectedItems) {
        mDataGetter.deleteSelectedFavorites(selectedItems);
    }

    @Override
    public void onDataGetted(List<AudioFavorite> data) {
        mView.bindAudioListViews(data);
    }

    @Override
    public void onNoDataGetted() {
        mView.showNoDataViews();
    }

    @Override
    public void onDeleteSelectedData() {
        mView.showDeleteOverViews();
    }

    @Override
    public void cancelRequest() {

    }
}
