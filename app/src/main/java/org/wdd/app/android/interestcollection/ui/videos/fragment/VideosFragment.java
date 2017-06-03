package org.wdd.app.android.interestcollection.ui.videos.fragment;


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
import org.wdd.app.android.interestcollection.ui.videos.activity.VideoDetailActivity;
import org.wdd.app.android.interestcollection.ui.videos.adapter.VideoAdapter;
import org.wdd.app.android.interestcollection.ui.videos.model.Video;
import org.wdd.app.android.interestcollection.ui.videos.presenter.VideosPresenter;
import org.wdd.app.android.interestcollection.utils.AppToaster;
import org.wdd.app.android.interestcollection.views.LineDividerDecoration;
import org.wdd.app.android.interestcollection.views.LoadView;

import java.util.ArrayList;
import java.util.List;

public class VideosFragment extends BaseFragment {

    private View mRootView;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private LoadView mLoadView;

    private VideosPresenter mPresenter;
    private VideoAdapter mAdapter;
    private List<Video> mVideos;
    private String mUrl;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_videos, container, false);
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
        mPresenter = new VideosPresenter(this);
        mUrl = getArguments().getString("url");
    }

    private void initViews() {
        mRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.fragment_videos_refreshlayout);
        int color = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        mRefreshLayout.setColorSchemeColors(color, color);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_videos_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new LineDividerDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mLoadView = (LoadView) mRootView.findViewById(R.id.fragment_videos_loadview);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getVideosListData(mUrl, false, host);
            }
        });
        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getVideosListData(mUrl, false, host);
            }
        });
    }

    @Override
    protected void lazyLoad() {
        mPresenter.getVideosListData(mUrl, false, host);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.cancelRequest();
    }

    public void showVideosListView(List<Video> data, boolean isAppend, boolean isLastPage) {
        if (mAdapter == null) {
            mVideos = new ArrayList<>();
            mVideos.addAll(data);
            mAdapter = new VideoAdapter(getContext(), mVideos);
            mAdapter.setOnLoadMoreListener(new AbstractCommonAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    mPresenter.getVideosListData(mUrl, true, host);
                }
            });
            mAdapter.setOnItemClickedListener(new VideoAdapter.OnItemClickedListener() {
                @Override
                public void onItemClicked(int position, Video item) {
                    VideoDetailActivity.show(getActivity(), item);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mLoadView.setStatus(LoadView.LoadStatus.Normal);

        } else {

            if (isAppend) {
                int start = mVideos.size();
                mVideos.addAll(data);
                mAdapter.notifyItemRangeChanged(start, data.size());
            } else {
                mVideos.clear();
                mVideos.addAll(data);
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
