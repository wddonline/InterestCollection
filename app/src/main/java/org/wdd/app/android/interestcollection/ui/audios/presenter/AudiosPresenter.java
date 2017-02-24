package org.wdd.app.android.interestcollection.ui.audios.presenter;

import org.wdd.app.android.interestcollection.ui.audios.data.AudiosDataGetter;
import org.wdd.app.android.interestcollection.ui.audios.fragment.AudiosFragment;
import org.wdd.app.android.interestcollection.ui.audios.model.Audio;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.base.BasePresenter;

import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class AudiosPresenter implements BasePresenter, AudiosDataGetter.DataCallback {
    private AudiosFragment mView;
    private AudiosDataGetter mGetter;


    public AudiosPresenter(AudiosFragment view) {
        this.mView = view;
        mGetter = new AudiosDataGetter(view.getContext(), this);
    }

    @Override
    public void cancelRequest() {
        mGetter.cancelSession();
    }

    public void getAudiosListData(boolean isAppend, ActivityFragmentAvaliable host) {
        mGetter.requestAudiosListData(isAppend, host);
    }

    @Override
    public void onRequestOk(List<Audio> data, boolean isAppend, boolean isLastPage) {
        if (data == null || data.size() == 0) {
            mView.showNoDataView(isAppend);
            return;
        }
        mView.showAudiosListView(data, isAppend, isLastPage);
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
