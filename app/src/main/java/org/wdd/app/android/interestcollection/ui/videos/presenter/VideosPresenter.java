package org.wdd.app.android.interestcollection.ui.videos.presenter;

import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.videos.data.VideosDataGetter;
import org.wdd.app.android.interestcollection.ui.videos.fragment.VideosFragment;
import org.wdd.app.android.interestcollection.ui.videos.model.Video;

import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class VideosPresenter implements BasePresenter, VideosDataGetter.DataCallback {

    private VideosFragment mView;
    private VideosDataGetter mGetter;


    public VideosPresenter(VideosFragment view) {
        this.mView = view;
        mGetter = new VideosDataGetter(view.getContext(), this);
    }

    @Override
    public void cancelRequest() {
        mGetter.cancelSession();
    }

    public void getVideosListData(boolean isAppend, ActivityFragmentAvaliable host) {
        mGetter.requestVideosListData(isAppend, host);
    }

    @Override
    public void onRequestOk(List<Video> data, boolean isAppend, boolean isLastPage) {
        if (data == null || data.size() == 0) {
            mView.showNoDataView(isAppend);
            return;
        }
        mView.showVideosListView(data, isAppend, isLastPage);
    }

    @Override
    public void onRequestError(String error, boolean isAppend) {
        mView.showErrorView(error, isAppend);
    }

    @Override
    public void onNetworkError(boolean isAppend) {
        mView.showNetworkError(isAppend);
    }
}
