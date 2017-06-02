package org.wdd.app.android.interestcollection.ui.videos.presenter;

import android.text.TextUtils;

import org.wdd.app.android.interestcollection.database.model.VideoFavorite;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.videos.activity.VideoDetailActivity;
import org.wdd.app.android.interestcollection.ui.videos.data.VideosDetailDataGetter;
import org.wdd.app.android.interestcollection.ui.videos.model.VideoDetail;

/**
 * Created by richard on 2/23/17.
 */

public class VideoDetailPresenter implements BasePresenter, VideosDetailDataGetter.DataCallback {

    private VideoDetailActivity mView;
    private VideosDetailDataGetter mGetter;


    public VideoDetailPresenter(VideoDetailActivity view) {
        this.mView = view;
        mGetter = new VideosDetailDataGetter(view, this);
    }

    @Override
    public void cancelRequest() {
        mGetter.cancelSession();
    }

    public void getVideoDetailData(String url, ActivityFragmentAvaliable host) {
        mGetter.requestVideoDetailData(url, host);
    }

    public void getVideoCollectStatus(String url, ActivityFragmentAvaliable host) {
        mGetter.queryVideoCollectStatus(url, host);
    }

    public void uncollectVideo(int id, ActivityFragmentAvaliable host) {
        mGetter.deleteFavoriteById(id, host);
    }

    public void collectVideo(String title, String time, String url, String imgUrl, ActivityFragmentAvaliable host) {
        mGetter.insertFavorite(title, time, url, imgUrl, host);
    }

    @Override
    public void onRequestOk(VideoDetail detail) {
        if (TextUtils.isEmpty(detail.vid)) {
            mView.showNoDataView();
            return;
        }
        mView.showVideoDetailViews(detail);
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
    public void onFavoriteQueried(VideoFavorite favorite) {
        mView.showVideoCollectViews(favorite);
    }

    @Override
    public void onFavoriteCollected(boolean success, VideoFavorite favorite) {
        if (success) {
            mView.updateVideoCollectViews(favorite);
        }
    }

    @Override
    public void onFavoriteUncollected(boolean success) {
        mView.showVideoUncollectViews(success);
    }
}
