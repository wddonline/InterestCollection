package org.wdd.app.android.interestcollection.ui.videos.presenter;

import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.main.model.HtmlHref;
import org.wdd.app.android.interestcollection.ui.videos.data.VideosMenuDataGetter;
import org.wdd.app.android.interestcollection.ui.videos.fragment.VideosMainFragment;

import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class VideosMenuPresenter implements BasePresenter, VideosMenuDataGetter.DataCallback {

    private VideosMainFragment mView;
    private VideosMenuDataGetter mGetter;


    public VideosMenuPresenter(VideosMainFragment view) {
        this.mView = view;
        mGetter = new VideosMenuDataGetter(view.getContext(), this);
    }

    @Override
    public void cancelRequest() {
        mGetter.cancelSession();
    }

    public void getVideosMenuData(ActivityFragmentAvaliable host) {
        mGetter.requestVideosMenuData(host);
    }

    @Override
    public void onRequestOk(List<HtmlHref> data) {
        if (data == null || data.size() == 0) {
            mView.showNoDataView();
            return;
        }
        mView.showVideosListView(data);
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
