package org.wdd.app.android.interestcollection.ui.audios.presenter;

import org.wdd.app.android.interestcollection.database.model.AudioFavorite;
import org.wdd.app.android.interestcollection.ui.audios.activity.AudioDetailActivity;
import org.wdd.app.android.interestcollection.ui.audios.data.AudioDetailDataGetter;
import org.wdd.app.android.interestcollection.ui.audios.model.AudioDetail;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;

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

    public void getAudioCollectStatus(String url, ActivityFragmentAvaliable host) {
        mGetter.queryAudioCollectStatus(url, host);
    }

    public void uncollectAudio(int id, ActivityFragmentAvaliable host) {
        mGetter.deleteFavoriteById(id, host);
    }

    public void collectAudio(String title, String time, String url, String imgUrl, ActivityFragmentAvaliable host) {
        mGetter.insertFavorite(title, time, url, imgUrl, host);
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

    @Override
    public void onFavoriteQueried(AudioFavorite favorite) {
        mView.showAudioCollectViews(favorite);
    }

    @Override
    public void onFavoriteCollected(boolean success, AudioFavorite favorite) {
        if (success) {
            mView.updateAudioCollectViews(favorite);
        }
    }

    @Override
    public void onFavoriteUncollected(boolean success) {
        mView.showAudioUncollectViews(success);
    }
}
