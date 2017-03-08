package org.wdd.app.android.interestcollection.ui.favorites.presenter;

import org.wdd.app.android.interestcollection.database.model.ImageFavorite;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.favorites.data.ImageFavoritesDataGetter;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.impl.ImageFavoritesFragment;

import java.util.List;

/**
 * Created by richard on 1/24/17.
 */

public class ImageFavoritesPresenter implements BasePresenter, ImageFavoritesDataGetter.DataCallback {

    private ImageFavoritesFragment mView;
    private ImageFavoritesDataGetter mDataGetter;

    public ImageFavoritesPresenter(ImageFavoritesFragment view) {
        this.mView = view;
        mDataGetter = new ImageFavoritesDataGetter(view.getContext(), this, view.host);
    }

    public void getImageFavorites() {
        mDataGetter.queryImageFavorites();
    }

    public void deleteSelectedFavorites(List<ImageFavorite> selectedItems) {
        mDataGetter.deleteSelectedFavorites(selectedItems);
    }

    @Override
    public void onDataGetted(List<ImageFavorite> data) {
        mView.bindImageListViews(data);
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
