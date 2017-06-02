package org.wdd.app.android.interestcollection.ui.videos.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.youku.cloud.module.PlayerErrorInfo;
import com.youku.cloud.player.PlayerListener;
import com.youku.cloud.player.VideoDefinition;
import com.youku.cloud.player.YoukuPlayerView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ads.builder.BannerAdsBuilder;
import org.wdd.app.android.interestcollection.app.InterestCollectionApplication;
import org.wdd.app.android.interestcollection.database.model.VideoFavorite;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.videos.model.Video;
import org.wdd.app.android.interestcollection.ui.videos.model.VideoDetail;
import org.wdd.app.android.interestcollection.ui.videos.presenter.VideoDetailPresenter;
import org.wdd.app.android.interestcollection.utils.AppToaster;
import org.wdd.app.android.interestcollection.utils.Constants;
import org.wdd.app.android.interestcollection.views.LoadView;

import java.lang.ref.WeakReference;

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

    private View mVideoContainer;
    private View mHeaderView;
    private View mFooterView;
    private Toolbar mToolbar;
    private TextView mTitleView;
    private TextView mTimeView;
    private TextView mTagView;
    private TextView mCommentCountView;
    private TextView mSourceView;
    private LoadView mLoadView;
    private YoukuPlayerView mYoukuPlayerView;

    private VideoDetailPresenter mPresenter;
    private VideoDetail mDetail;
    private UMShareListener mShareListener;
    private ShareAction mShareAction;


    private int id;
    private boolean initCollectStatus = false;
    private boolean currentCollectStatus = initCollectStatus;
    private boolean isSupported = false;

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
                        mPresenter.collectVideo(mVideo.title, mVideo.date, mVideo.url, mVideo.imgUrl, host);
                        return true;
                    case R.id.menu_detail_uncollect:
                        mPresenter.uncollectVideo(mFavorite.id, host);
                        return true;
                    case R.id.menu_detail_share:
                        ShareBoardConfig config = new ShareBoardConfig();
                        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);
                        mShareAction.open(config);
                        return true;
                }
                return false;
            }
        });
    }

    private void initViews() {
        mVideoContainer = findViewById(R.id.activity_video_detail_container);
        mHeaderView = findViewById(R.id.activity_video_detail_header);
        mFooterView = findViewById(R.id.activity_video_detail_footer);
        mTitleView = (TextView) findViewById(R.id.layout_post_list_header_title);
        mTimeView = (TextView) findViewById(R.id.layout_post_list_header_date);
        mTagView = (TextView) findViewById(R.id.layout_post_list_header_tag);
        mCommentCountView = (TextView) findViewById(R.id.layout_post_list_header_comment_count);
        mSourceView = (TextView) findViewById(R.id.layout_post_list_footer_source);
        mYoukuPlayerView = (YoukuPlayerView) findViewById(R.id.activity_video_detail_playerview);
        mLoadView = (LoadView) findViewById(R.id.activity_video_detail_loadview);
        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getVideoDetailData(mVideo.url, host);
            }
        });

        mShareListener = new CustomShareListener(this);
        mShareAction = new ShareAction(this).setDisplayList(
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        UMWeb web = new UMWeb("https://www.pgyer.com/QGM2");
                        web.setTitle(getString(R.string.app_name));
                        web.setDescription(mVideo.title);
                        web.setThumb(new UMImage(getBaseContext(), mVideo.imgUrl));
                        new ShareAction(VideoDetailActivity.this).withMedia(web)
                                .setPlatform(share_media)
                                .setCallback(mShareListener)
                                .share();
                    }
                });

        mPresenter.getVideoDetailData(mVideo.url, host);
    }

    private static class CustomShareListener implements UMShareListener {

        private WeakReference<VideoDetailActivity> mActivity;

        private CustomShareListener(VideoDetailActivity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(mActivity.get(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            if (platform == SHARE_MEDIA.QQ || platform == SHARE_MEDIA.QZONE) return;
            Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSupported) mYoukuPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSupported) mYoukuPlayerView.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
        mPresenter.cancelRequest();
        if (isSupported) mYoukuPlayerView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        mPresenter.getVideoCollectStatus(mVideo.url, host);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        if (isSupported) {
            if (mYoukuPlayerView.isFullScreen()) {
                mYoukuPlayerView.goSmallScreen();
            } else {
                backAction();
                super.onBackPressed();
            }
        } else {
            backAction();
            super.onBackPressed();
        }
    }

    private void backAction() {
        if (currentCollectStatus != initCollectStatus) {
            Intent intent = new Intent();
            intent.putExtra("id", id);
            setResult(RESULT_OK, intent);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mShareAction.close();
        if (!isSupported) return;
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                mToolbar.setVisibility(View.VISIBLE);
                mHeaderView.setVisibility(View.VISIBLE);
                mFooterView.setVisibility(View.VISIBLE);
                mYoukuPlayerView.setShowBackBtn(false);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                mToolbar.setVisibility(View.GONE);
                mHeaderView.setVisibility(View.GONE);
                mFooterView.setVisibility(View.GONE);
                mYoukuPlayerView.setShowBackBtn(true);
                break;
        }
    }

    private void autoplayvideo() {
        mYoukuPlayerView.playYoukuVideo(mDetail.vid);
    }

    public void showNoDataView() {
        mLoadView.setStatus(LoadView.LoadStatus.No_Data);
    }

    public void showVideoDetailViews(VideoDetail data) {
        this.mDetail = data;
        mLoadView.setStatus(LoadView.LoadStatus.Normal);

        isSupported = true;
        mVideoContainer.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(data.title)) {
            mTitleView.setText(data.title);
        }
        if (!TextUtils.isEmpty(data.time)) {
            mTimeView.setText(data.time);
        }
        if (!TextUtils.isEmpty(data.tag)) {
            mTagView.setText(data.tag);
        }
        findViewById(R.id.layout_post_list_header_comment_icon).setVisibility(View.GONE);
        mCommentCountView.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(data.source)) {
            mSourceView.setText(data.source);
        }
        ViewGroup adsView = (ViewGroup) mFooterView.findViewById(R.id.layout_post_list_footer_ads);
        BannerAdsBuilder adsBuilder = new BannerAdsBuilder(this, adsView, Constants.DETAIL_FOOTER_AD_ID, true);
        if (InterestCollectionApplication.getInstance().isAdsOpen()) {
            adsBuilder.addBannerAds();
        }

        // 初始化播放器
        mYoukuPlayerView.attachActivity(this);
        mYoukuPlayerView.setPreferVideoDefinition(VideoDefinition.VIDEO_HD);
        mYoukuPlayerView.setShowBackBtn(false);
        mYoukuPlayerView.setPlayerListener(new VideoPlayerListener());

        autoplayvideo();
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
        mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(!initCollectStatus);
        mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(initCollectStatus);
    }

    public void updateVideoCollectViews(VideoFavorite favorite) {
        currentCollectStatus = true;
        mFavorite = favorite;
        mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(false);
        mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(true);
    }

    public void showVideoUncollectViews(boolean success) {
        if (success) {
            currentCollectStatus = false;
            mFavorite = null;
            mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(true);
            mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(false);
        }
    }

    private class VideoPlayerListener extends PlayerListener {
        @Override
        public void onComplete() {
            super.onComplete();
        }

        @Override
        public void onError(int code, PlayerErrorInfo info) {
            switch(code) {
                case 3001://无版权
                case 3002://被禁止播放
                case 3004://订阅才能观看
                default:
                    AppToaster.show(info.getDesc());
                    break;
            }

        }

        @Override
        public void OnCurrentPositionChanged(int msec) {
            super.OnCurrentPositionChanged(msec);
        }

        @Override
        public void onVideoNeedPassword(int code) {
            super.onVideoNeedPassword(code);
        }

        @Override
        public void onVideoSizeChanged(int width, int height) {
            super.onVideoSizeChanged(width, height);
        }
    }
}
