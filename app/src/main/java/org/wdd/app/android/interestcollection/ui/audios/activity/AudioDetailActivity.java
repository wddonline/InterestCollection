package org.wdd.app.android.interestcollection.ui.audios.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.audios.model.AudioDetail;
import org.wdd.app.android.interestcollection.ui.audios.presenter.AudioDetailPresenter;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.views.LoadView;

public class AudioDetailActivity extends BaseActivity {

    public static void show(Context context, String url, String title) {
        Intent intent = new Intent(context, AudioDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
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

    private AudioDetailPresenter mPresenter;

    private String mUrl;
    private String mTitle;

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

        mPresenter.getAudioDetailData(mUrl, host);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.cancelRequest();
    }

    public void showNoDataView() {
        mLoadView.setStatus(LoadView.LoadStatus.No_Data);
    }

    public void showAudioDetailViews(AudioDetail data) {
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
}
