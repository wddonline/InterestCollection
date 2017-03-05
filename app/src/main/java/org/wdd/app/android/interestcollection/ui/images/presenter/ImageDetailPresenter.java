package org.wdd.app.android.interestcollection.ui.images.presenter;

import org.wdd.app.android.interestcollection.database.model.ImageFavorite;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.images.activity.ImageDetailActivity;
import org.wdd.app.android.interestcollection.ui.images.data.ImageDetailDateGetter;
import org.wdd.app.android.interestcollection.ui.images.model.ImageDetail;

/**
 * Created by richard on 2/24/17.
 */

public class ImageDetailPresenter implements BasePresenter, ImageDetailDateGetter.DataCallback {

    private ImageDetailActivity mView;
    private ImageDetailDateGetter mGetter;

    public ImageDetailPresenter(ImageDetailActivity view) {
        this.mView = view;
        mGetter = new ImageDetailDateGetter(view.getBaseContext(), this);
    }

    @Override
    public void cancelRequest() {
        mGetter.cancelSession();
    }

    public void getImageDetailData(String url, ActivityFragmentAvaliable host) {
        mGetter.requestImageDetailData(url, host);
    }

    public void getImageCollectStatus(String url, ActivityFragmentAvaliable host) {
        mGetter.queryImageCollectStatus(url, host);
    }

    public void uncollectImage(int id, ActivityFragmentAvaliable host) {
        mGetter.deleteFavoriteById(id, host);
    }

    public void collectImage(String title, String time, String url, String imgUrl, boolean gifFlag, ActivityFragmentAvaliable host) {
        mGetter.insertFavorite(title, time, url, imgUrl, gifFlag ? 1 : 0, host);
    }

    @Override
    public void onRequestOk(ImageDetail data) {
        if (data == null || data.nodes.size() == 0) {
            mView.showNoDataView();
            return;
        }
        mView.showImageDetailViews(data);
    }

    @Override
    public void onRequestError(String error) {
        mView.showErrorView(error);
    }

    @Override
    public void onNetworkError() {
        mView.showNetworkError();
    }

    @Override
    public void onFavoriteQueried(ImageFavorite favorite) {
        mView.showImageCollectViews(favorite);
    }

    @Override
    public void onFavoriteCollected(boolean success, ImageFavorite favorite) {
        if (success) {
            mView.updateImageCollectViews(favorite);
        } else {
            mView.showImageCollectFinishView();
        }
    }

    @Override
    public void onFavoriteUncollected(boolean success) {
        mView.showImageUncollectViews(success);
    }
}
