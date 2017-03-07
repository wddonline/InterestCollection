package org.wdd.app.android.interestcollection.ui.jokes.presenter;

import org.wdd.app.android.interestcollection.database.model.DirtyJokeFavorite;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.jokes.activity.DirtyJokeDetailActivity;
import org.wdd.app.android.interestcollection.ui.jokes.data.DirtyJokeDetailDateGetter;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJokeDetail;

/**
 * Created by richard on 2/24/17.
 */

public class DirtyJokeDetailPresenter implements BasePresenter, DirtyJokeDetailDateGetter.DataCallback {

    private DirtyJokeDetailActivity mView;
    private DirtyJokeDetailDateGetter mGetter;


    public DirtyJokeDetailPresenter(DirtyJokeDetailActivity view) {
        this.mView = view;
        mGetter = new DirtyJokeDetailDateGetter(view.getBaseContext(), this);
    }

    @Override
    public void cancelRequest() {
        mGetter.cancelSession();
    }

    public void getDirtyJokeDetailData(String url, ActivityFragmentAvaliable host) {
        mGetter.requestDirtyJokeDetailData(url, host);
    }

    public void getDirtyJokeCollectStatus(String url, ActivityFragmentAvaliable host) {
        mGetter.queryGirlCollectStatus(url, host);
    }

    public void uncollectGirl(int id, ActivityFragmentAvaliable host) {
        mGetter.deleteFavoriteById(id, host);
    }

    public void collectGirl(String title, String time, String url, String imgUrl, ActivityFragmentAvaliable host) {
        mGetter.insertFavorite(title, time, url, imgUrl, host);
    }

    @Override
    public void onRequestOk(DirtyJokeDetail data) {
        if (data == null) {
            mView.showNoDataView();
            return;
        }
        mView.showDirtyJokesDetailViews(data);
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
    public void onFavoriteQueried(DirtyJokeFavorite favorite) {
        mView.showDirtyJokeCollectViews(favorite);
    }

    @Override
    public void onFavoriteCollected(boolean success, DirtyJokeFavorite favorite) {
        if (success) {
            mView.updateDirtyJokeCollectViews(favorite);
        }
    }

    @Override
    public void onFavoriteUncollected(boolean success) {
        mView.showDirtyJokeUncollectViews(success);
    }

}
