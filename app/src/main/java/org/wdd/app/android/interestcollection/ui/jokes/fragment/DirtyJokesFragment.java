package org.wdd.app.android.interestcollection.ui.jokes.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter;
import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter.LoadStatus;
import org.wdd.app.android.interestcollection.ui.base.BaseFragment;
import org.wdd.app.android.interestcollection.ui.jokes.adapter.DirtyJodeAdapter;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJoke;
import org.wdd.app.android.interestcollection.ui.jokes.presenter.DirtyJokesPresenter;
import org.wdd.app.android.interestcollection.utils.AppToaster;
import org.wdd.app.android.interestcollection.views.LineDividerDecoration;
import org.wdd.app.android.interestcollection.views.LoadView;

import java.util.ArrayList;
import java.util.List;

public class DirtyJokesFragment extends BaseFragment {

    private View mRootView;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private LoadView mLoadView;

    private DirtyJokesPresenter mPresenter;
    private DirtyJodeAdapter mAdapter;
    private List<DirtyJoke> jokes;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_dirty_jokes, container, false);
            initData();
            initViews();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    private void initData() {
        mPresenter = new DirtyJokesPresenter(this);
    }

    private void initViews() {
        mRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.fragment_dirty_jokes_refreshlayout);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_dirty_jokes_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new LineDividerDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mLoadView = (LoadView) mRootView.findViewById(R.id.fragment_dirty_jokes_loadview);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getDirtyJokesListData(false, host);
            }
        });
        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getDirtyJokesListData(false, host);
            }
        });
    }

    @Override
    protected void lazyLoad() {
        mPresenter.getDirtyJokesListData(false, host);
    }

    public void showDirtyJokesListView(List<DirtyJoke> data, boolean isAppend, boolean isLastPage) {
        if (mAdapter == null) {
            jokes = new ArrayList<>();
            jokes.addAll(data);
            mAdapter = new DirtyJodeAdapter(getContext(), jokes);
            mAdapter.setOnLoadMoreListener(new AbstractCommonAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    mPresenter.getDirtyJokesListData(true, host);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mLoadView.setStatus(LoadView.LoadStatus.Normal);
        } else {
            if (isAppend) {
                int start = jokes.size();
                jokes.addAll(data);
                mAdapter.notifyItemRangeChanged(start, data.size());
            } else {
                jokes.clear();
                jokes.addAll(data);
                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
            }
        }
        mAdapter.setLoadStatus(isLastPage ? LoadStatus.NoMore : LoadStatus.Normal);
    }

    public void showNetworkError(boolean isAppend) {
        if (mAdapter != null) {
            AppToaster.show(R.string.no_connection_error);
            if (isAppend) {
                mAdapter.setLoadStatus(LoadStatus.Normal);
            } else {
                mRefreshLayout.setRefreshing(false);
            }
        } else {
            mLoadView.setStatus(LoadView.LoadStatus.Network_Error);
        }
    }

    public void showErrorView(String error, boolean isAppend) {
        if (mAdapter != null) {
            AppToaster.show(TextUtils.isEmpty(error) ? getString(R.string.unknown_error) : error);
            if (isAppend) {
                mAdapter.setLoadStatus(LoadStatus.Normal);
            } else {
                mRefreshLayout.setRefreshing(false);
            }
        } else {
            mLoadView.setStatus(LoadView.LoadStatus.Request_Failure, error);
        }
    }

    public void showNoDataView(boolean isAppend) {
        if (mAdapter != null) {
            AppToaster.show(R.string.no_data_error);
            if (isAppend) {
                mAdapter.setLoadStatus(LoadStatus.Normal);
            } else {
                mRefreshLayout.setRefreshing(false);
            }
        } else {
            mLoadView.setStatus(LoadView.LoadStatus.No_Data);
        }
    }

}
