package org.wdd.app.android.interestcollection.ui.shares.presenter;

import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.jokes.data.DirtyJokesDataGetter;
import org.wdd.app.android.interestcollection.ui.jokes.fragment.DirtyJokesFragment;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJoke;
import org.wdd.app.android.interestcollection.ui.shares.data.SharesDataGetter;
import org.wdd.app.android.interestcollection.ui.shares.fragment.SharesFragment;
import org.wdd.app.android.interestcollection.ui.shares.model.Share;

import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class SharesPresenter implements BasePresenter, SharesDataGetter.DataCallback {

    private SharesFragment mView;
    private SharesDataGetter mGetter;


    public SharesPresenter(SharesFragment view) {
        this.mView = view;
        mGetter = new SharesDataGetter(view.getContext(), this);
    }

    @Override
    public void cancelRequest() {
        mGetter.cancelSession();
    }

    public void getSharesListData(boolean isAppend, ActivityFragmentAvaliable host) {
        mGetter.requestSharesListData(isAppend, host);
    }

    @Override
    public void onRequestOk(List<Share> data, boolean isAppend, boolean isLastPage) {
        if (data == null || data.size() == 0) {
            mView.showNoDataView(isAppend);
            return;
        }
        mView.showSharesListView(data, isAppend, isLastPage);
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
