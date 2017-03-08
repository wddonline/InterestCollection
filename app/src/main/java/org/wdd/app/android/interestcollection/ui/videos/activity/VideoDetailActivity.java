package org.wdd.app.android.interestcollection.ui.videos.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.database.model.VideoFavorite;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.videos.model.Video;
import org.wdd.app.android.interestcollection.ui.videos.model.VideoDetail;
import org.wdd.app.android.interestcollection.ui.videos.presenter.VideoDetailPresenter;
import org.wdd.app.android.interestcollection.views.LoadView;

public class VideoDetailActivity extends BaseActivity {

    public static void show(Context context, Video video) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("video", video);
        context.startActivity(intent);
    }

    public static void showForResult(Activity activity, int id, Video video, int requestCode) {
        Intent intent = new Intent(activity, VideoDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("video", video);
        activity.startActivityForResult(intent, requestCode);
    }

    private View mScrollView;
    private Toolbar mToolbar;
    private TextView mTitleView;
    private TextView mTimeView;
    private TextView mTagView;
    private TextView mCommentCountView;
    private View mImageView;
    private TextView mSourceView;
    private LoadView mLoadView;

    private VideoDetailPresenter mPresenter;

    private int id;
    private boolean initCollectStatus = false;
    private boolean currentCollectStatus = initCollectStatus;

    private Video mVideo;
    private VideoFavorite mFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        initData();
        initTitles();
        initViews();
    }

    private void initData() {
        id = getIntent().getIntExtra("id" , -1);
        mVideo = getIntent().getParcelableExtra("video");

        mPresenter = new VideoDetailPresenter(this);
    }

    private void initTitles() {
        mToolbar = (Toolbar) findViewById(R.id.activity_video_detail_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle(mVideo.title);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_detail_collect:
                        mPresenter.uncollectVideo(mFavorite.id, host);
                        return true;
                    case R.id.menu_detail_uncollect:
                        mPresenter.collectVideo(mVideo.title, mVideo.date, mVideo.url, mVideo.imgUrl, host);
                        return false;
                }
                return false;
            }
        });
    }

    private void initViews() {
        mScrollView = findViewById(R.id.activity_video_detail_scrollview);
        mTitleView = (TextView) findViewById(R.id.layout_post_list_header_title);
        mTimeView = (TextView) findViewById(R.id.layout_post_list_header_date);
        mTagView = (TextView) findViewById(R.id.layout_post_list_header_tag);
        mCommentCountView = (TextView) findViewById(R.id.layout_post_list_header_comment_count);
        mImageView = findViewById(R.id.layout_post_list_header_img);
        mImageView.setVisibility(View.GONE);
        mSourceView = (TextView) findViewById(R.id.layout_post_list_footer_source);
        mLoadView = (LoadView) findViewById(R.id.activity_video_detail_loadview);
        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getVideoDetailData(mVideo.url, host);
            }
        });

        mPresenter.getVideoDetailData(mVideo.url, host);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.cancelRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        mPresenter.getVideoCollectStatus(mVideo.url, host);
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

    public void showNoDataView() {
        mLoadView.setStatus(LoadView.LoadStatus.No_Data);
    }

    public void showVideoDetailViews(VideoDetail data) {
        mScrollView.setVisibility(View.VISIBLE);
        mLoadView.setStatus(LoadView.LoadStatus.Normal);

        mTitleView.setText(data.title);
        mTimeView.setText(data.time);
        mTagView.setText(data.tag);
        mCommentCountView.setText(data.commentCount);
        mSourceView.setText(data.source);
    }

    public void showErrorView(String error) {
        mLoadView.setStatus(LoadView.LoadStatus.Request_Failure , error);
    }

    public void showNetworkError() {
        mLoadView.setStatus(LoadView.LoadStatus.Network_Error);
    }

    public void showVideoCollectViews(VideoFavorite favorite) {
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

    public void updateVideoCollectViews(VideoFavorite favorite) {
        currentCollectStatus = true;
        mFavorite = favorite;
        mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(true);
        mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(false);
    }

    public void showVideoUncollectViews(boolean success) {
        if (success) {
            currentCollectStatus = false;
            mFavorite = null;
            mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(false);
            mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(true);
        }
    }
}
