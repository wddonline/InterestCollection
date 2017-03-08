package org.wdd.app.android.interestcollection.ui.favorites.presenter;

import org.wdd.app.android.interestcollection.database.model.AudioFavorite;
import org.wdd.app.android.interestcollection.database.model.DirtyJokeFavorite;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.favorites.data.AudioFavoritesDataGetter;
import org.wdd.app.android.interestcollection.ui.favorites.data.DirtyJokeFavoritesDataGetter;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.impl.AudioFavoritesFragment;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.impl.DirtyJokeFavoritesFragment;

import java.util.List;

/**
 * Created by richard on 1/24/17.
 */

public class DirtyJokeFavoritesPresenter implements BasePresenter, DirtyJokeFavoritesDataGetter.DataCallback {

    private DirtyJokeFavoritesFragment mView;
    private DirtyJokeFavoritesDataGetter mDataGetter;

    public DirtyJokeFavoritesPresenter(DirtyJokeFavoritesFragment view) {
        this.mView = view;
        mDataGetter = new DirtyJokeFavoritesDataGetter(view.getContext(), this, view.host);
    }

    public void getDirtyJokeFavorites() {
        mDataGetter.queryDirtyJokeFavorites();
    }

    public void deleteSelectedFavorites(List<DirtyJokeFavorite> selectedItems) {
        mDataGetter.deleteSelectedFavorites(selectedItems);
    }

    @Override
    public void onDataGetted(List<DirtyJokeFavorite> data) {
        mView.bindDirtyJokeListViews(data);
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
