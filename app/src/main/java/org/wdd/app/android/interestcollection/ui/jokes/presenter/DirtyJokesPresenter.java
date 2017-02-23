package org.wdd.app.android.interestcollection.ui.jokes.presenter;

import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;
import org.wdd.app.android.interestcollection.ui.jokes.data.DirtyJokesDataGetter;
import org.wdd.app.android.interestcollection.ui.jokes.fragment.DirtyJokesFragment;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJoke;

import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class DirtyJokesPresenter implements BasePresenter, DirtyJokesDataGetter.DataCallback {

    private DirtyJokesFragment mView;
    private DirtyJokesDataGetter mGetter;


    public DirtyJokesPresenter(DirtyJokesFragment view) {
        this.mView = view;
        mGetter = new DirtyJokesDataGetter(view.getContext(), this);
    }

    @Override
    public void cancelRequest() {
        mGetter.cancelSession();
    }

    public void getDirtyJokesListData(boolean isAppend, ActivityFragmentAvaliable host) {
        mGetter.requestDirtyJokesListData(isAppend, host);
    }

    @Override
    public void onRequestOk(List<DirtyJoke> data, boolean isAppend) {
        if (data == null || data.size() == 0) {
            mView.showNoDataView(isAppend);
            return;
        }
        mView.showDirtyJokesListView(data, isAppend);
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
