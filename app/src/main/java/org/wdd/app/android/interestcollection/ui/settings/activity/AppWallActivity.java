package org.wdd.app.android.interestcollection.ui.settings.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.settings.adapter.AppAdapter;
import org.wdd.app.android.interestcollection.ui.settings.model.AppModel;
import org.wdd.app.android.interestcollection.ui.settings.presenter.AppWallPresenter;
import org.wdd.app.android.interestcollection.utils.AppToaster;
import org.wdd.app.android.interestcollection.utils.AppUtils;
import org.wdd.app.android.interestcollection.views.LineDividerDecoration;
import org.wdd.app.android.interestcollection.views.LoadView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 4/14/17.
 */

public class AppWallActivity extends BaseActivity {

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, AppWallActivity.class);
        activity.startActivity(intent);
    }

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private LoadView mLoadView;

    private AppWallPresenter mPresenter;
    private AppAdapter mAdapter;
    private List<AppModel> mApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_wall);
        initData();
        initTitles();
        initViews();
    }

    private void initData() {
        mPresenter = new AppWallPresenter(this);
    }

    private void initTitles() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_app_wall_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_app_wall_refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_app_wall_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        LineDividerDecoration decoration = new LineDividerDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);
        mLoadView = (LoadView) findViewById(R.id.activity_app_wall_loadview);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getAppList(host);
            }
        });
        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getAppList(host);
            }
        });

        mPresenter.getAppList(host);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.cancelRequest();
    }

    public void showAppListViews(List<AppModel> apps) {
        if (mAdapter == null) {
            mLoadView.setStatus(LoadView.LoadStatus.Normal);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mApps = new ArrayList<>();
            mAdapter = new AppAdapter(this, apps);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setLoadStatus(AbstractCommonAdapter.LoadStatus.NoMore);
            mAdapter.setOnItemClickedListener(new AppAdapter.OnItemClickedListener() {
                @Override
                public void onItemClicked(AppModel app) {
                    startApp(app);
                }
            });
        } else {
            mRefreshLayout.setRefreshing(false);
            mApps.clear();
            mApps.addAll(apps);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void startApp(AppModel app) {
        if (AppUtils.isAppInstalled(this, app.packageName)) {
            PackageManager packageManager = getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(app.packageName);
            if (intent == null) {
                Uri uri = Uri.parse(app.url);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return;
            }
            startActivity(intent);
        } else {
            Uri uri = Uri.parse(app.url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    public void showNoDataViews() {
        if (mAdapter == null) {
            mLoadView.setStatus(LoadView.LoadStatus.No_Data);
        } else {
            AppToaster.show(R.string.no_data_error);
            mRefreshLayout.setRefreshing(false);
        }
    }

    public void showRequestErrorViews(String error) {
        if (mAdapter == null) {
            mLoadView.setStatus(LoadView.LoadStatus.Request_Failure);
        } else {
            AppToaster.show(error);
            mRefreshLayout.setRefreshing(false);
        }
    }

    public void showNetworkErrorViews() {
        if (mAdapter == null) {
            mLoadView.setStatus(LoadView.LoadStatus.Network_Error);
        } else {
            AppToaster.show(R.string.no_connection_error);
            mRefreshLayout.setRefreshing(false);
        }
    }
}
