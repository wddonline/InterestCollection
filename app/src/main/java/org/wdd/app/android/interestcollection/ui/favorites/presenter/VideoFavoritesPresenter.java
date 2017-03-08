package org.wdd.app.android.interestcollection.ui.favorites.presenter;

import org.wdd.app.android.interestcollection.database.model.VideoFavorite;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.favorites.data.VideoFavoritesDataGetter;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.impl.VideoFavoritesFragment;

import java.util.List;

/**
 * Created by richard on 1/24/17.
 */

public class VideoFavoritesPresenter implements BasePresenter, VideoFavoritesDataGetter.DataCallback {

    private VideoFavoritesFragment mView;
    private VideoFavoritesDataGetter mDataGetter;

    public VideoFavoritesPresenter(VideoFavoritesFragment view) {
        this.mView = view;
        mDataGetter = new VideoFavoritesDataGetter(view.getContext(), this, view.host);
    }

    public void getVideoFavorites() {
        mDataGetter.queryVideoFavorites();
    }

    public void deleteSelectedFavorites(List<VideoFavorite> selectedItems) {
        mDataGetter.deleteSelectedFavorites(selectedItems);
    }

    @Override
    public void onDataGetted(List<VideoFavorite> data) {
        mView.bindVideoListViews(data);
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
