package org.wdd.app.android.interestcollection.ui.shares.fragment;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter;
import org.wdd.app.android.interestcollection.ui.base.BaseFragment;
import org.wdd.app.android.interestcollection.ui.shares.activity.ShareDetailActivity;
import org.wdd.app.android.interestcollection.ui.shares.adapter.SharesAdapter;
import org.wdd.app.android.interestcollection.ui.shares.model.Share;
import org.wdd.app.android.interestcollection.ui.shares.presenter.SharesPresenter;
import org.wdd.app.android.interestcollection.utils.AppToaster;
import org.wdd.app.android.interestcollection.views.LineDividerDecoration;
import org.wdd.app.android.interestcollection.views.LoadView;

import java.util.ArrayList;
import java.util.List;

public class SharesFragment extends BaseFragment {

    private View mRootView;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private LoadView mLoadView;

    private SharesPresenter mPresenter;
    private SharesAdapter mAdapter;
    private List<Share> shares;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_shares, container, false);
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
        mPresenter = new SharesPresenter(this);
    }

    private void initViews() {
        mRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.fragment_shares_refreshlayout);
        int color = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        mRefreshLayout.setColorSchemeColors(color, color);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_shares_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new LineDividerDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mLoadView = (LoadView) mRootView.findViewById(R.id.fragment_shares_loadview);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getSharesListData(false, host);
            }
        });
        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getSharesListData(false, host);
            }
        });
    }

    @Override
    protected void lazyLoad() {
        mPresenter.getSharesListData(false, host);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.cancelRequest();
    }

    public void showSharesListView(List<Share> data, boolean isAppend, boolean isLastPage) {
        if (mAdapter == null) {
            shares = new ArrayList<>();
            shares.addAll(data);
            mAdapter = new SharesAdapter(getContext(), shares);
            mAdapter.setOnLoadMoreListener(new AbstractCommonAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    mPresenter.getSharesListData(true, host);
                }
            });
            mAdapter.setOnItemClickedListener(new SharesAdapter.OnItemClickedListener() {
                @Override
                public void onItemClicked(int position, Share item) {
                    ShareDetailActivity.show(getContext(), item.url, item.title);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mLoadView.setStatus(LoadView.LoadStatus.Normal);
        } else {
            if (isAppend) {
                int start = shares.size();
                shares.addAll(data);
                mAdapter.notifyItemRangeChanged(start, data.size());
            } else {
                shares.clear();
                shares.addAll(data);
                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
            }
        }
        mAdapter.setLoadStatus(isLastPage ? AbstractCommonAdapter.LoadStatus.NoMore : AbstractCommonAdapter.LoadStatus.Normal);
    }

    public void showNetworkError(boolean isAppend) {
        if (mAdapter != null) {
            AppToaster.show(R.string.no_connection_error);
            if (isAppend) {
                mAdapter.setLoadStatus(AbstractCommonAdapter.LoadStatus.Normal);
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
                mAdapter.setLoadStatus(AbstractCommonAdapter.LoadStatus.Normal);
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
                mAdapter.setLoadStatus(AbstractCommonAdapter.LoadStatus.Normal);
            } else {
                mRefreshLayout.setRefreshing(false);
            }
        } else {
            mLoadView.setStatus(LoadView.LoadStatus.No_Data);
        }
    }

}
