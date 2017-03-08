package org.wdd.app.android.interestcollection.ui.images.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.cache.DrawableCache;
import org.wdd.app.android.interestcollection.database.model.ImageFavorite;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.images.adapter.ImageDetailAdapter;
import org.wdd.app.android.interestcollection.ui.images.model.Image;
import org.wdd.app.android.interestcollection.ui.images.model.ImageDetail;
import org.wdd.app.android.interestcollection.ui.images.presenter.ImageDetailPresenter;
import org.wdd.app.android.interestcollection.views.LoadView;

public class ImageDetailActivity extends BaseActivity {

    public static void show(Activity activity, Image image) {
        Intent intent = new Intent(activity, ImageDetailActivity.class);
        intent.putExtra("image", image);
        activity.startActivity(intent);
    }

    public static void showForResult(Activity activity, int id, Image image, int requestCode) {
        Intent intent = new Intent(activity, ImageDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("image", image);
        activity.startActivityForResult(intent, requestCode);
    }

    private Toolbar mToolbar;
    private ListView mListView;
    private LoadView mLoadView;
    private View mHeaderView;
    private View mFooterView;

    private ImageDetailPresenter mPresenter;
    private ImageDetailAdapter mAdapter;
    private ImageFavorite mFavorite;
    private Image mImage;

    private int id;
    private boolean initCollectStatus = false;
    private boolean currentCollectStatus = initCollectStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        initData();
        initTitles();
        initViews();
    }

    private void initData() {
        mPresenter = new ImageDetailPresenter(this);

        id = getIntent().getIntExtra("id" , -1);
        mImage = getIntent().getParcelableExtra("image");
    }

    private void initTitles() {
        mToolbar = (Toolbar) findViewById(R.id.activity_image_detail_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle(mImage.title);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_detail_collect:
                        mPresenter.uncollectImage(mFavorite.id, host);
                        return true;
                    case R.id.menu_detail_uncollect:
                        mPresenter.collectImage(mImage.title, mImage.date, mImage.url, mImage.imgUrl, mImage.isGif, host);
                        return false;

                }
                return false;
            }
        });
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.activity_image_detail_listview);
        mLoadView = (LoadView) findViewById(R.id.activity_image_detail_loadview);
        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getImageDetailData(mImage.url, host);
            }
        });

        mPresenter.getImageDetailData(mImage.url, host);
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
        mPresenter.getImageCollectStatus(mImage.url, host);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.cancelRequest();
        DrawableCache.getInstance().clear();
    }

    public void showImageDetailViews(ImageDetail data) {
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
        View imgView = mHeaderView.findViewById(R.id.layout_post_list_header_img);
        imgView.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(data.summary)) {
            View summaryLayout = mHeaderView.findViewById(R.id.layout_post_list_header_summary_container);
            summaryLayout.setVisibility(View.VISIBLE);
            TextView summaryView = (TextView) mHeaderView.findViewById(R.id.layout_post_list_header_summary);
            summaryView.setText(data.summary);
        }

        if (mFooterView == null) {
            mFooterView = View.inflate(this, R.layout.layout_post_list_footer, null);
            mListView.addFooterView(mFooterView);
        }
        TextView sourceView = (TextView) mFooterView.findViewById(R.id.layout_post_list_footer_source);
        sourceView.setText(data.source);

        if (mAdapter == null) {
            mAdapter = new ImageDetailAdapter(this, data.nodes);
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

    public void showImageCollectViews(ImageFavorite favorite) {
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

    public void updateImageCollectViews(ImageFavorite favorite) {
        currentCollectStatus = true;
        mFavorite = favorite;
        mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(true);
        mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(false);
    }

    public void showImageUncollectViews(boolean success) {
        if (success) {
            currentCollectStatus = false;
            mFavorite = null;
            mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(false);
            mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(true);
        }
    }
}
