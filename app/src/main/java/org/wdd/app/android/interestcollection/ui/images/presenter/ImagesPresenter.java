package org.wdd.app.android.interestcollection.ui.images.presenter;

import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.images.data.ImagesDataGetter;
import org.wdd.app.android.interestcollection.ui.images.fragment.ImagesFragment;
import org.wdd.app.android.interestcollection.ui.images.model.Image;

import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class ImagesPresenter implements BasePresenter, ImagesDataGetter.DataCallback {

    private ImagesFragment mView;
    private ImagesDataGetter mGetter;


    public ImagesPresenter(ImagesFragment view) {
        this.mView = view;
        mGetter = new ImagesDataGetter(view.getContext(), this);
    }

    @Override
    public void cancelRequest() {
        mGetter.cancelSession();
    }

    public void getImagesListData(boolean isAppend, ActivityFragmentAvaliable host) {
        mGetter.requestImagesListData(isAppend, host);
    }

    @Override
    public void onRequestOk(List<Image> data, boolean isAppend, boolean isLastPage) {
        if (data == null || data.size() == 0) {
            mView.showNoDataView(isAppend);
            return;
        }
        mView.showImagesListView(data, isAppend, isLastPage);
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
