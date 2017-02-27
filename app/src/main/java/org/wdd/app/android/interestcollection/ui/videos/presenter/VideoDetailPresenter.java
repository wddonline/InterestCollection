package org.wdd.app.android.interestcollection.ui.videos.presenter;

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

    @Override
    public void onRequestOk(VideoDetail detail) {
        if (detail == null) {
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
}
