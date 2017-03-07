package org.wdd.app.android.interestcollection.ui.shares.presenter;

import org.wdd.app.android.interestcollection.database.model.ShareFavorite;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.shares.activity.ShareDetailActivity;
import org.wdd.app.android.interestcollection.ui.shares.data.ShareDetailDataGetter;
import org.wdd.app.android.interestcollection.ui.shares.model.ShareDetail;

/**
 * Created by richard on 2/23/17.
 */

public class ShareDetailPresenter implements BasePresenter, ShareDetailDataGetter.DataCallback {

    private ShareDetailActivity mView;
    private ShareDetailDataGetter mGetter;


    public ShareDetailPresenter(ShareDetailActivity view) {
        this.mView = view;
        mGetter = new ShareDetailDataGetter(view, this);
    }

    @Override
    public void cancelRequest() {
        mGetter.cancelSession();
    }

    public void getShareDetailData(String url, ActivityFragmentAvaliable host) {
        mGetter.requestShareDetailData(url, host);
    }

    public void getShareCollectStatus(String url, ActivityFragmentAvaliable host) {
        mGetter.queryShareCollectStatus(url, host);
    }

    public void uncollectShare(int id, ActivityFragmentAvaliable host) {
        mGetter.deleteFavoriteById(id, host);
    }

    public void collectShare(String title, String time, String url, String imgUrl, ActivityFragmentAvaliable host) {
        mGetter.insertFavorite(title, time, url, imgUrl, host);
    }

    @Override
    public void onRequestOk(ShareDetail detail) {
        if (detail == null) {
            mView.showNoDataView();
            return;
        }
        mView.showShareDetailViews(detail);
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
    public void onFavoriteQueried(ShareFavorite favorite) {
        mView.showShareCollectViews(favorite);
    }

    @Override
    public void onFavoriteCollected(boolean success, ShareFavorite favorite) {
        if (success) {
            mView.updateShareCollectViews(favorite);
        }
    }

    @Override
    public void onFavoriteUncollected(boolean success) {
        mView.showShareUncollectViews(success);
    }
}
