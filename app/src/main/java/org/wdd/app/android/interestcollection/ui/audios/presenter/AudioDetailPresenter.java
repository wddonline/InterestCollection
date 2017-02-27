package org.wdd.app.android.interestcollection.ui.audios.presenter;

import org.wdd.app.android.interestcollection.ui.audios.activity.AudioDetailActivity;
import org.wdd.app.android.interestcollection.ui.audios.data.AudioDetailDataGetter;
import org.wdd.app.android.interestcollection.ui.audios.model.AudioDetail;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.videos.activity.VideoDetailActivity;
import org.wdd.app.android.interestcollection.ui.videos.data.VideosDetailDataGetter;
import org.wdd.app.android.interestcollection.ui.videos.model.VideoDetail;

/**
 * Created by richard on 2/23/17.
 */

public class AudioDetailPresenter implements BasePresenter, AudioDetailDataGetter.DataCallback {

    private AudioDetailActivity mView;
    private AudioDetailDataGetter mGetter;


    public AudioDetailPresenter(AudioDetailActivity view) {
        this.mView = view;
        mGetter = new AudioDetailDataGetter(view, this);
    }

    @Override
    public void cancelRequest() {
        mGetter.cancelSession();
    }

    public void getAudioDetailData(String url, ActivityFragmentAvaliable host) {
        mGetter.requestAudioDetailData(url, host);
    }

    @Override
    public void onRequestOk(AudioDetail detail) {
        if (detail == null) {
            mView.showNoDataView();
            return;
        }
        mView.showAudioDetailViews(detail);
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
