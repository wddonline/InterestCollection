package org.wdd.app.android.interestcollection.ui.shares.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.database.model.ShareFavorite;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.shares.adapter.ShareDetailAdapter;
import org.wdd.app.android.interestcollection.ui.shares.model.Share;
import org.wdd.app.android.interestcollection.ui.shares.model.ShareDetail;
import org.wdd.app.android.interestcollection.ui.shares.presenter.ShareDetailPresenter;
import org.wdd.app.android.interestcollection.views.LoadView;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

public class ShareDetailActivity extends BaseActivity {

    public static void show(Context context, Share share) {
        Intent intent = new Intent(context, ShareDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("share", share);
        context.startActivity(intent);
    }

    public static void showForResult(Activity activity, int id, Share share, int requestCode) {
        Intent intent = new Intent(activity, ShareDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("share", share);
        activity.startActivityForResult(intent, requestCode);
    }

    private Toolbar mToolbar;
    private ListView mListView;
    private LoadView mLoadView;
    private View mHeaderView;
    private View mFooterView;

    private ShareDetailPresenter mPresenter;
    private ShareDetailAdapter mAdapter;
    private Share mShare;
    private ShareFavorite mFavorite;

    private int id;
    private boolean initCollectStatus = false;
    private boolean currentCollectStatus = initCollectStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_detail);
        initData();
        initTitles();
        initViews();
    }

    private void initData() {
        mPresenter = new ShareDetailPresenter(this);

        id = getIntent().getIntExtra("id" , -1);
        mShare = getIntent().getParcelableExtra("share");
    }

    private void initTitles() {
        mToolbar = (Toolbar) findViewById(R.id.activity_share_detail_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle(mShare.title);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_detail_collect:
                        mPresenter.uncollectShare(mFavorite.id, host);
                        return true;
                    case R.id.menu_detail_uncollect:
                        mPresenter.collectShare(mShare.title, mShare.date, mShare.url, mShare.imgUrl, host);
                        return false;
                }
                return false;
            }
        });
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.activity_share_detail_listview);
        mLoadView = (LoadView) findViewById(R.id.activity_share_detail_loadview);
        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getShareDetailData(mShare.url, host);
            }
        });

        mPresenter.getShareDetailData(mShare.url, host);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.cancelRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        mPresenter.getShareCollectStatus(mShare.url, host);
        return true;
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

    public void showShareDetailViews(ShareDetail data) {
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
        NetworkImageView imageView = (NetworkImageView) mHeaderView.findViewById(R.id.layout_post_list_header_img);
        imageView.setVisibility(View.GONE);

        if (mFooterView == null) {
            mFooterView = View.inflate(this, R.layout.layout_post_list_footer, null);
            mListView.addFooterView(mFooterView);
        }
        TextView sourceView = (TextView) mFooterView.findViewById(R.id.layout_post_list_footer_source);
        sourceView.setText(data.source);

        if (mAdapter == null) {
            mAdapter = new ShareDetailAdapter(this, data.nodes);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.refreshData(data.nodes);
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

    public void showShareCollectViews(ShareFavorite favorite) {
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

    public void updateShareCollectViews(ShareFavorite favorite) {
        currentCollectStatus = true;
        mFavorite = favorite;
        mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(true);
        mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(false);
    }

    public void showShareUncollectViews(boolean success) {
        if (success) {
            currentCollectStatus = false;
            mFavorite = null;
            mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(false);
            mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(true);
        }
    }
}
