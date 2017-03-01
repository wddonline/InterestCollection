package org.wdd.app.android.interestcollection.ui.shares.presenter;

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
}
