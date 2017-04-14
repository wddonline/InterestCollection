package org.wdd.app.android.interestcollection.ui.jokes.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ads.builder.BannerAdsBuilder;
import org.wdd.app.android.interestcollection.app.InterestCollectionApplication;
import org.wdd.app.android.interestcollection.database.model.DirtyJokeFavorite;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.jokes.adapter.DirtyJokeDetailAdapter;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJoke;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJokeDetail;
import org.wdd.app.android.interestcollection.ui.jokes.presenter.DirtyJokeDetailPresenter;
import org.wdd.app.android.interestcollection.utils.Constants;
import org.wdd.app.android.interestcollection.views.LoadView;

public class DirtyJokeDetailActivity extends BaseActivity {

    public static void show(Activity activity, DirtyJoke joke) {
        Intent intent = new Intent(activity, DirtyJokeDetailActivity.class);
        intent.putExtra("joke", joke);
        activity.startActivity(intent);
    }

    public static void showForResult(Activity activity, int id, DirtyJoke joke, int requestCode) {
        Intent intent = new Intent(activity, DirtyJokeDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("joke", joke);
        activity.startActivityForResult(intent, requestCode);
    }

    private ListView mListView;
    private LoadView mLoadView;
    private View mHeaderView;
    private View mFooterView;
    private Toolbar mToolbar;

    private DirtyJokeDetailPresenter mPresenter;
    private DirtyJokeDetailAdapter mAdapter;
    private DirtyJokeFavorite mFavorite;
    private DirtyJoke mJoke;
    private BannerAdsBuilder mFooterAdsBuilder;

    private int id;
    private boolean initCollectStatus = false;
    private boolean currentCollectStatus = initCollectStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dirty_joke_detail);
        initData();
        initTitles();
        initViews();
    }

    private void initData() {
        mPresenter = new DirtyJokeDetailPresenter(this);

        id = getIntent().getIntExtra("id" , -1);
        mJoke = getIntent().getParcelableExtra("joke");
    }

    private void initTitles() {
        mToolbar = (Toolbar) findViewById(R.id.activity_dirty_joke_detail_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle(mJoke.title);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_detail_collect:
                        mPresenter.uncollectGirl(mFavorite.id, host);
                        return true;
                    case R.id.menu_detail_uncollect:
                        mPresenter.collectGirl(mJoke.title, mJoke.date, mJoke.url, mJoke.imgUrl, host);
                        return false;
                }
                return false;
            }
        });
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.activity_dirty_joke_detail_listview);
        mLoadView = (LoadView) findViewById(R.id.activity_dirty_joke_detail_loadview);

        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getDirtyJokeDetailData(mJoke.url, host);
            }
        });

        mPresenter.getDirtyJokeDetailData(mJoke.url, host);
    }

    @Override
    public void onBackPressed() {
        backAction();
        super.onBackPressed();
    }

    private void backAction() {
        if (currentCollectStatus != initCollectStatus) {
            Intent intent = new Intent();
            intent.putExtra("id", id);
            setResult(RESULT_OK, intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        mPresenter.getDirtyJokeCollectStatus(mJoke.url, host);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.cancelRequest();
    }

    public void showDirtyJokesDetailViews(DirtyJokeDetail data) {
        mLoadView.setStatus(LoadView.LoadStatus.Normal);
        mListView.setVisibility(View.VISIBLE);

        if (mHeaderView == null) {
            mHeaderView = View.inflate(this, R.layout.layout_post_list_header, null);
            mListView.addHeaderView(mHeaderView);
        }
        TextView titleView = (TextView) mHeaderView.findViewById(R.id.layout_post_list_header_title);
        titleView.setText(data.title);
        TextView timeView = (TextView) mHeaderView.findViewById(R.id.layout_post_list_header_date);
        timeView.setText(data.time);
        TextView tagView = (TextView) mHeaderView.findViewById(R.id.layout_post_list_header_tag);
        tagView.setText(data.tag);
        TextView commentCountView = (TextView) mHeaderView.findViewById(R.id.layout_post_list_header_comment_count);
        commentCountView.setText(data.commentCount);

        if (mFooterView == null) {
            mFooterView = View.inflate(this, R.layout.layout_post_list_footer, null);
            mListView.addFooterView(mFooterView);

            ViewGroup footerAdsView = (ViewGroup) mFooterView.findViewById(R.id.layout_post_list_footer_ads);
            mFooterAdsBuilder = new BannerAdsBuilder(this, footerAdsView, Constants.DETAIL_FOOTER_AD_ID, true);
            if (InterestCollectionApplication.getInstance().isAdsOpen()) {
                mFooterAdsBuilder.addBannerAds();
            }
        }
        TextView sourceView = (TextView) mFooterView.findViewById(R.id.layout_post_list_footer_source);
        sourceView.setText(data.source);

        if (mAdapter == null) {
            mAdapter = new DirtyJokeDetailAdapter(this, data.post);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.refreshData(data.post);
        }

    }

    public void showNoDataView() {
        mLoadView.setStatus(LoadView.LoadStatus.No_Data);
    }

    public void showErrorView(String error) {
        mLoadView.setStatus(LoadView.LoadStatus.Request_Failure, error);
    }

    public void showNetworkError() {
        mLoadView.setStatus(LoadView.LoadStatus.Network_Error);
    }

    public void showDirtyJokeCollectViews(DirtyJokeFavorite favorite) {
        if (favorite == null) {
            initCollectStatus = false;
            currentCollectStatus = false;
        } else {
            initCollectStatus = true;
            currentCollectStatus = true;
        }
        mFavorite = favorite;
        mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(initCollectStatus);
        mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(!initCollectStatus);
    }

    public void updateDirtyJokeCollectViews(DirtyJokeFavorite favorite) {
        currentCollectStatus = true;
        mFavorite = favorite;
        mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(true);
        mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(false);
    }

    public void showDirtyJokeUncollectViews(boolean success) {
        if (success) {
            currentCollectStatus = false;
            mFavorite = null;
            mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(false);
            mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(true);
        }
    }

}
