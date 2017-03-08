package org.wdd.app.android.interestcollection.ui.favorites.fragment.impl;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.database.model.DirtyJokeFavorite;
import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter;
import org.wdd.app.android.interestcollection.ui.favorites.activity.FavoritesActionCallback;
import org.wdd.app.android.interestcollection.ui.favorites.activity.FavoritesActivity;
import org.wdd.app.android.interestcollection.ui.favorites.adapter.impl.AudioFavoritesAdapter;
import org.wdd.app.android.interestcollection.ui.favorites.adapter.impl.DirtyJokeFavoritesAdapter;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.FavoritesFragment;
import org.wdd.app.android.interestcollection.ui.favorites.presenter.DirtyJokeFavoritesPresenter;
import org.wdd.app.android.interestcollection.ui.jokes.activity.DirtyJokeDetailActivity;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJoke;
import org.wdd.app.android.interestcollection.views.LineDividerDecoration;
import org.wdd.app.android.interestcollection.views.LoadView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 3/7/17.
 */

public class DirtyJokeFavoritesFragment extends FavoritesFragment implements DirtyJokeFavoritesAdapter.DirtyJokeFavoritesCallback {

    private View mRootView;
    private RecyclerView mRecyclerView;
    private LoadView mLoadView;

    private List<DirtyJokeFavoritesAdapter.DirtyJokeItem> mFavorites;
    private DirtyJokeFavoritesPresenter mPresenter;
    private DirtyJokeFavoritesAdapter mAdapter;

    private FavoritesActionCallback callback;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_audio_favorites, container, false);
            initData();
            initViews();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    protected void lazyLoad() {
        mPresenter.getDirtyJokeFavorites();
    }

    private void initData() {
        mPresenter = new DirtyJokeFavoritesPresenter(this);
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.activity_audio_favorites_recyclerview);
        mRecyclerView.addItemDecoration(new LineDividerDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mLoadView = (LoadView) mRootView.findViewById(R.id.activity_audio_favorites_loadview);

    }

    @Override
    public void cancelSelectMode() {
        mAdapter.setMode(AudioFavoritesAdapter.Mode.Normal);
        callback.resetTitleBar();
    }

    @Override
    public AudioFavoritesAdapter.Mode getMode() {
        if (mAdapter == null) return AudioFavoritesAdapter.Mode.Normal;
        return mAdapter.getMode();
    }

    @Override
    public int getSelectedCount() {
        return mAdapter.getSelectedItem().size();
    }

    @Override
    public void deleteSelectedFavorites() {
        showLoadingDialog();
        mPresenter.deleteSelectedFavorites(mAdapter.getSelectedItem());
    }

    @Override
    public void refreshDataRemoved(int id) {
        mAdapter.removeDataById(id);
    }

    public void showNoDataViews() {
        if (mAdapter == null) {
            mLoadView.setStatus(LoadView.LoadStatus.No_Data);
        } else {
            mAdapter.setLoadStatus(AbstractCommonAdapter.LoadStatus.NoMore);
        }
    }

    public void bindDirtyJokeListViews(List<DirtyJokeFavorite> data) {
        mFavorites = new ArrayList<>();
        for (DirtyJokeFavorite favorite : data) {
            mFavorites.add(new DirtyJokeFavoritesAdapter.DirtyJokeItem(false, favorite));
        }
        mAdapter = new DirtyJokeFavoritesAdapter(getContext(), mFavorites);
        mAdapter.setCallback(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setMode(AudioFavoritesAdapter.Mode.Normal);
        mAdapter.setLoadStatus(AbstractCommonAdapter.LoadStatus.NoMore);
    }

    @Override
    public void jumpToDetailActivity(int id, DirtyJoke joke) {
        DirtyJokeDetailActivity.showForResult(getActivity(), id, joke, FavoritesActivity.REQUEST_CODE_DETAIL);
    }

    @Override
    public void switchSelectMode() {
        callback.switchSelectMode();
    }

    @Override
    public void onAllSelected() {
        callback.onAllSelected();
    }

    @Override
    public void onPartSelected() {
        callback.onPartSelected();
    }

    @Override
    public void selectAll() {
        mAdapter.selectAll();
    }

    @Override
    public void unselectAll() {
        mAdapter.unselectAll();
    }

    public void showDeleteOverViews() {
        hideLoadingDialog();
        List<DirtyJokeFavoritesAdapter.DirtyJokeItem> selectedItems = mAdapter.getSelectedOriginItem();
        mFavorites.removeAll(selectedItems);
        cancelSelectMode();
    }

    public void setCallback(FavoritesActionCallback callback) {
        this.callback = callback;
    }
}
