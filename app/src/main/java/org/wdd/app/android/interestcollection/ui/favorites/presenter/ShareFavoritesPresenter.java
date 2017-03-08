package org.wdd.app.android.interestcollection.ui.favorites.presenter;

import org.wdd.app.android.interestcollection.database.model.ShareFavorite;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.favorites.data.ShareFavoritesDataGetter;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.impl.ShareFavoritesFragment;

import java.util.List;

/**
 * Created by richard on 1/24/17.
 */

public class ShareFavoritesPresenter implements BasePresenter, ShareFavoritesDataGetter.DataCallback {

    private ShareFavoritesFragment mView;
    private ShareFavoritesDataGetter mDataGetter;

    public ShareFavoritesPresenter(ShareFavoritesFragment view) {
        this.mView = view;
        mDataGetter = new ShareFavoritesDataGetter(view.getContext(), this, view.host);
    }

    public void getShareFavorites() {
        mDataGetter.queryShareFavorites();
    }

    public void deleteSelectedFavorites(List<ShareFavorite> selectedItems) {
        mDataGetter.deleteSelectedFavorites(selectedItems);
    }

    @Override
    public void onDataGetted(List<ShareFavorite> data) {
        mView.bindShareListViews(data);
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
