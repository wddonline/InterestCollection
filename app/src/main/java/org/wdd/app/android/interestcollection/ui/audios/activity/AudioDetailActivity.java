package org.wdd.app.android.interestcollection.ui.audios.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.service.music.OnPlayerEventListener;
import org.wdd.app.android.interestcollection.service.music.PlayService;
import org.wdd.app.android.interestcollection.service.music.model.Music;
import org.wdd.app.android.interestcollection.service.music.receiver.RemoteControlReceiver;
import org.wdd.app.android.interestcollection.ui.audios.model.Audio;
import org.wdd.app.android.interestcollection.ui.audios.model.AudioDetail;
import org.wdd.app.android.interestcollection.ui.audios.presenter.AudioDetailPresenter;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.utils.SystemUtils;
import org.wdd.app.android.interestcollection.views.LoadView;

public class AudioDetailActivity extends BaseActivity implements View.OnClickListener, OnPlayerEventListener {

    public static void show(Context context, Audio audio) {
        Intent intent = new Intent(context, AudioDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", audio.url);
        intent.putExtra("title", audio.title);
        intent.putExtra("imgUrl", audio.imgUrl);
        context.startActivity(intent);
    }

    private View mScrollView;
    private TextView mTitleView;
    private TextView mTimeView;
    private TextView mTagView;
    private TextView mCommentCountView;
    private View mImageView;
    private TextView mSourceView;
    private LoadView mLoadView;
    private SeekBar mSeekBar;
    private ImageView mPlayBtn;
    private TextView mTimerView;

    private String mUrl;
    private String mTitle;
    private String mImgUrl;
    private AudioDetail mDetail;

    private AudioDetailPresenter mPresenter;
    private PlayService mPlayService;
    private AudioManager mAudioManager;
    private ComponentName mRemoteReceiver;

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayService = ((PlayService.PlayBinder) service).getService();
            mPlayService.setOnPlayEventListener(AudioDetailActivity.this);

            registerReceiver();

            Music music = new Music();
            music.setType(Music.Type.ONLINE);
            music.setTitle(mTitle);
            music.setUri(mDetail.audioUrl);
            music.setType(Music.Type.ONLINE);
            music.setCoverUri(mImgUrl);
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
        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");
        mImgUrl = getIntent().getStringExtra("imgUrl");

        mPresenter = new AudioDetailPresenter(this);
    }

    private void initTitles() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_audio_detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle(mTitle);
    }

    private void initViews() {
        mScrollView = findViewById(R.id.activity_audio_detail_scrollview);
        mTitleView = (TextView) findViewById(R.id.layout_post_list_header_title);
        mTimeView = (TextView) findViewById(R.id.layout_post_list_header_date);
        mTagView = (TextView) findViewById(R.id.layout_post_list_header_tag);
        mCommentCountView = (TextView) findViewById(R.id.layout_post_list_header_comment_count);
        mImageView = findViewById(R.id.layout_post_list_header_img);
        mImageView.setVisibility(View.GONE);
        mSourceView = (TextView) findViewById(R.id.layout_post_list_footer_source);
        mLoadView = (LoadView) findViewById(R.id.activity_audio_detail_loadview);
        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getAudioDetailData(mUrl, host);
            }
        });
        mSeekBar = (SeekBar) findViewById(R.id.activity_audio_detail_seekbar);
        mPlayBtn = (ImageView) findViewById(R.id.activity_audio_detail_play);
        mTimerView = (TextView) findViewById(R.id.activity_audio_detail_timer);

        mPlayBtn.setOnClickListener(this);
        mPresenter.getAudioDetailData(mUrl, host);
    }

    private void registerReceiver() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mRemoteReceiver = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(mRemoteReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.cancelRequest();

        if (mRemoteReceiver != null) {
            mAudioManager.unregisterMediaButtonEventReceiver(mRemoteReceiver);
        }
        if (mPlayService != null) {
            mPlayService.setOnPlayEventListener(null);
            unbindService(mConn);
            mPlayService = null;
        }
    }

    public void showNoDataView() {
        mLoadView.setStatus(LoadView.LoadStatus.No_Data);
    }

    public void showAudioDetailViews(AudioDetail data) {
        this.mDetail = data;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_audio_detail_play:
                mPlayBtn.setSelected(true);
                if (mPlayService == null) {
                    Intent intent = new Intent(this, PlayService.class);
                    bindService(intent, mConn, Context.BIND_AUTO_CREATE);
                } else {
                    mPlayService.playPause();
                }
                break;
        }
    }

    //音频播放交互
    @Override
    public void onPublish(int progress) {
        mSeekBar.setProgress(progress);
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
    public void onTimer(long remain) {
        mTimerView.setText(SystemUtils.formatTime("(mm:ss)", remain));
    }
}
