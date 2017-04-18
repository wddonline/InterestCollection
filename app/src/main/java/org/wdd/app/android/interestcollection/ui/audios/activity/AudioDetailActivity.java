package org.wdd.app.android.interestcollection.ui.audios.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
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

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ads.builder.BannerAdsBuilder;
import org.wdd.app.android.interestcollection.app.InterestCollectionApplication;
import org.wdd.app.android.interestcollection.database.model.AudioFavorite;
import org.wdd.app.android.interestcollection.service.music.OnPlayerEventListener;
import org.wdd.app.android.interestcollection.service.music.PlayService;
import org.wdd.app.android.interestcollection.service.music.model.Music;
import org.wdd.app.android.interestcollection.service.music.receiver.RemoteControlReceiver;
import org.wdd.app.android.interestcollection.ui.audios.model.Audio;
import org.wdd.app.android.interestcollection.ui.audios.model.AudioDetail;
import org.wdd.app.android.interestcollection.ui.audios.presenter.AudioDetailPresenter;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.utils.Constants;
import org.wdd.app.android.interestcollection.views.LoadView;
import org.wdd.app.android.interestcollection.views.RoundedNetworkImageView;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class AudioDetailActivity extends BaseActivity implements View.OnClickListener, OnPlayerEventListener {

    public static void show(Context context, Audio audio) {
        Intent intent = new Intent(context, AudioDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("audio", audio);
        context.startActivity(intent);
    }

    public static void showForResult(Activity activity, int id, Audio audio, int requestCode) {
        Intent intent = new Intent(activity, AudioDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("audio", audio);
        activity.startActivityForResult(intent, requestCode);
    }

    private View mScrollView;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private TextView mTitleView;
    private TextView mTimeView;
    private TextView mTagView;
    private TextView mCommentCountView;
    private TextView mSourceView;
    private LoadView mLoadView;
    private ImageView mPlayBtn;
    private RoundedNetworkImageView mCoverView;
    private TextView mPastTimeView;
    private SeekBar mSeekBar;
    private TextView mAllTimeView;
    private Toolbar mToolbar;

    private int id;
    private boolean initCollectStatus = false;
    private boolean currentCollectStatus = initCollectStatus;

    private Audio mAudio;
    private AudioFavorite mFavorite;
    private AudioDetail mDetail;
    private AudioDetailPresenter mPresenter;
    private PlayService mPlayService;
    private AudioManager mAudioManager;
    private ComponentName mRemoteReceiver;
    private UMShareListener mShareListener;
    private ShareAction mShareAction;

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayService = ((PlayService.PlayBinder) service).getService();
            mPlayService.setOnPlayEventListener(AudioDetailActivity.this);

            registerReceiver();

            Music music = new Music();
            music.setType(Music.Type.ONLINE);
            music.setTitle(mAudio.title);
            music.setAlbum(mDetail.column);
            music.setArtist(mDetail.anchor);
            music.setUri(mDetail.audioUrl);
            music.setType(Music.Type.ONLINE);
            music.setCoverUri(mAudio.imgUrl);
            mPlayService.play(music);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_detail);
        initData();
        initTitles();
        initViews();
    }

    private void initData() {
        id = getIntent().getIntExtra("id" , -1);
        mAudio = getIntent().getParcelableExtra("audio");

        mPresenter = new AudioDetailPresenter(this);
    }

    private void initTitles() {
        mToolbar = (Toolbar) findViewById(R.id.activity_audio_detail_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle(mAudio.title);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_detail_collect:
                        mPresenter.uncollectAudio(mFavorite.id, host);
                        return true;
                    case R.id.menu_detail_uncollect:
                        mPresenter.collectAudio(mAudio.title, mAudio.date, mAudio.url, mAudio.imgUrl, host);
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
        mWebView = (WebView) findViewById(R.id.activity_audio_detail_webview);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_audio_detail_progress);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setSupportZoom(false); // 支持缩放
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
            }

        });

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
            }
        });

        mScrollView = findViewById(R.id.activity_audio_detail_scrollview);
        mTitleView = (TextView) findViewById(R.id.layout_post_list_header_title);
        mTimeView = (TextView) findViewById(R.id.layout_post_list_header_date);
        mTagView = (TextView) findViewById(R.id.layout_post_list_header_tag);
        mCommentCountView = (TextView) findViewById(R.id.layout_post_list_header_comment_count);
        mSourceView = (TextView) findViewById(R.id.layout_post_list_footer_source);
        mLoadView = (LoadView) findViewById(R.id.activity_audio_detail_loadview);
        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getAudioDetailData(mAudio.url, host);
            }
        });

        mPlayBtn = (ImageView) findViewById(R.id.activity_audio_detail_play);
        mCoverView = (RoundedNetworkImageView) findViewById(R.id.activity_audio_detail_cover);
        mPastTimeView = (TextView) findViewById(R.id.activity_audio_detail_playtime);
        mSeekBar = (SeekBar) findViewById(R.id.activity_audio_detail_seekbar);
        mSeekBar.setEnabled(false);
        mAllTimeView = (TextView) findViewById(R.id.activity_audio_detail_alltime);

        mPlayBtn.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mPlayService == null) return;
                mPlayService.seekTo(seekBar.getProgress());
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
                        web.setDescription(mDetail.title);
                        web.setThumb(new UMImage(getBaseContext(), mAudio.imgUrl));
                        new ShareAction(AudioDetailActivity.this).withMedia(web)
                                .setPlatform(share_media)
                                .setCallback(mShareListener)
                                .share();
                    }
                });

        mPresenter.getAudioDetailData(mAudio.url, host);
    }

    private static class CustomShareListener implements UMShareListener {

        private WeakReference<AudioDetailActivity> mActivity;

        private CustomShareListener(AudioDetailActivity activity) {
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

    private void registerReceiver() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mRemoteReceiver = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(mRemoteReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        mPresenter.getAudioCollectStatus(mAudio.url, host);
        return true;
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mPlayService == null) return;
//        mPlayService.playPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mPlayService == null) return;
//        mPlayService.playPause();
//    }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 屏幕横竖屏切换时避免出现window leak的问题
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mShareAction.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
        mPresenter.cancelRequest();
        if (mDetail != null && !TextUtils.isEmpty(mDetail.html)) {
            mWebView.loadUrl("about:blank");
        }
        if (mRemoteReceiver != null) {
            mAudioManager.unregisterMediaButtonEventReceiver(mRemoteReceiver);
        }
        if (mPlayService != null) {
            mPlayService.stop();
            mPlayService.setOnPlayEventListener(null);
            unbindService(mConn);
            stopPlayService();
            mPlayService = null;
        }
    }

    public void showAudioDetailViews(AudioDetail data) {
        this.mDetail = data;
        mLoadView.setStatus(LoadView.LoadStatus.Normal);

        if (TextUtils.isEmpty(mDetail.audioUrl)) {
            mWebView.setVisibility(View.VISIBLE);
            mWebView.loadDataWithBaseURL("http://www.dsuu.cc/wp-content/themes/dsuum/", data.html, "text/html","UTF-8", null);
        } else {
            mScrollView.setVisibility(View.VISIBLE);
            mTitleView.setText(data.title);
            mTimeView.setText(data.time);
            mTagView.setText(data.tag);
            mCommentCountView.setText(data.commentCount);
            mSourceView.setText(data.source);
            mCoverView.setImageUrl(mAudio.imgUrl);
            mPastTimeView.setText(formatTime("(mm:ss)", 0));
            mAllTimeView.setText(formatTime("(mm:ss)", 0));

            ViewGroup footerAdsView = (ViewGroup) findViewById(R.id.layout_post_list_footer_ads);
            BannerAdsBuilder adsBuilder = new BannerAdsBuilder(this, footerAdsView, Constants.DETAIL_FOOTER_AD_ID, true);
            if (InterestCollectionApplication.getInstance().isAdsOpen()) {
                adsBuilder.addBannerAds();
            }
        }
    }

    //音频播放交互
    @Override
    public void onPublish(long currentMillions, long duration) {
        int progress = Math.round(1f * currentMillions / duration * 100);
        mSeekBar.setProgress(progress);
        mPastTimeView.setText(formatTime("(mm:ss)", currentMillions));
    }

    @Override
    public void onChange(Music music) {

    }

    @Override
    public void onPlayerPause() {
        mPlayBtn.setSelected(false);
    }

    @Override
    public void onPlayerResume() {
        mPlayBtn.setSelected(true);
    }

    @Override
    public void onPlayerPrepared(long duration) {
        mAllTimeView.setText(formatTime("(mm:ss)", duration));
        mSeekBar.setProgress(0);
        mSeekBar.setEnabled(true);
    }

    @Override
    public void onPlayerCached(long progress) {
        mSeekBar.setSecondaryProgress((int) progress);
    }

    @Override
    public void onPlayeCompletion() {
        mPastTimeView.setText(formatTime("(mm:ss)", 0));
        mPlayBtn.setSelected(false);
        mSeekBar.setProgress(0);
        stopPlayingAnim();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_audio_detail_play:
                if (mPlayService == null) {
                    startPlayService();
                    mPlayBtn.setSelected(true);
                    startPlayingAnim();
                } else {
                    if (mPlayService.isPlaying()) {
                        mPlayBtn.setSelected(false);
                        stopPlayingAnim();
                    } else {
                        mPlayBtn.setSelected(true);
                        startPlayingAnim();
                    }
                    mPlayService.playPause();
                }
                break;
        }
    }

    private void startPlayingAnim() {
        if (mCoverView.getAnimation() == null) {
            RotateAnimation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(3000);
            anim.setFillAfter(true);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setRepeatMode(Animation.RESTART);
            mCoverView.setAnimation(anim);
        }
        mCoverView.getAnimation().startNow();
    }

    private void stopPlayingAnim() {
        if (mCoverView.getAnimation() != null) mCoverView.getAnimation().cancel();
    }

    public void showNoDataView() {
        mLoadView.setStatus(LoadView.LoadStatus.No_Data);
    }

    public void showErrorView(String error) {
        mLoadView.setStatus(LoadView.LoadStatus.Request_Failure , error);
    }

    public void showNetworkError() {
        mLoadView.setStatus(LoadView.LoadStatus.Network_Error);
    }

    public void showAudioCollectViews(AudioFavorite favorite) {
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

    public void updateAudioCollectViews(AudioFavorite favorite) {
        currentCollectStatus = true;
        mFavorite = favorite;
        mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(true);
        mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(false);
    }

    public void showAudioUncollectViews(boolean success) {
        if (success) {
            currentCollectStatus = false;
            mFavorite = null;
            mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(false);
            mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(true);
        }
    }

    private void startPlayService() {
        Intent intent = new Intent(this, PlayService.class);
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    private void stopPlayService() {
        Intent intent = new Intent(this, PlayService.class);
        stopService(intent);
    }

    private String formatTime(String pattern, long milli) {
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String mm = String.format(Locale.getDefault(), "%02d", m);
        String ss = String.format(Locale.getDefault(), "%02d", s);
        return pattern.replace("mm", mm).replace("ss", ss);
    }
}
