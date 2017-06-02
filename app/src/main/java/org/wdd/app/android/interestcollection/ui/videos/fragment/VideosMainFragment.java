package org.wdd.app.android.interestcollection.ui.videos.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.BaseFragment;
import org.wdd.app.android.interestcollection.ui.main.model.HtmlHref;
import org.wdd.app.android.interestcollection.ui.videos.presenter.VideosMenuPresenter;
import org.wdd.app.android.interestcollection.views.LoadView;

import java.util.List;

public class VideosMainFragment extends BaseFragment {

    private View mRootView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LoadView mLoadView;

    private VideosMenuPresenter mPresenter;
    private VideosFragment[] mFragments;
    private List<HtmlHref> mMenus;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_video_main, container, false);
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
        mPresenter = new VideosMenuPresenter(this);
    }

    private void initViews() {
        mTabLayout = (TabLayout) mRootView.findViewById(R.id.fragment_video_main_tabs);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.fragment_video_main_pager);
        mLoadView = (LoadView) mRootView.findViewById(R.id.fragment_video_main_loadview);

        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getVideosMenuData(host);
            }
        });
    }

    @Override
    protected void lazyLoad() {
        mPresenter.getVideosMenuData(host);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.cancelRequest();
    }

    public void showVideosListView(List<HtmlHref> data) {
        this.mMenus = data;
        mLoadView.setStatus(LoadView.LoadStatus.Normal);
        View bar = mRootView.findViewById(R.id.fragment_video_main_bar);
        bar.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mFragments = new VideosFragment[data.size()];
        for (int i = 0; i < mMenus.size(); i++) {
            mFragments[i] = new VideosFragment();
            Bundle args = new Bundle();
            args.putString("url", mMenus.get(i).url);
            mFragments[i].setArguments(args);
        }
        MenuPagerAdapter adapter = new MenuPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void showNetworkError() {
        mLoadView.setStatus(LoadView.LoadStatus.Network_Error);
    }

    public void showErrorView(String error) {
        mLoadView.setStatus(LoadView.LoadStatus.Request_Failure, error);
    }

    public void showNoDataView() {
        mLoadView.setStatus(LoadView.LoadStatus.No_Data);
    }

    private class MenuPagerAdapter extends FragmentPagerAdapter {

        public MenuPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mMenus.get(position).name;
        }
    }

}
