package org.wdd.app.android.interestcollection.ui.jokes.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.jokes.adapter.DirtyJokeDetailAdapter;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJokeDetail;
import org.wdd.app.android.interestcollection.ui.jokes.presenter.DirtyJokeDetailPresenter;
import org.wdd.app.android.interestcollection.views.LoadView;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

public class DirtyJokeDetailActivity extends BaseActivity {

    public static void show(Activity activity, String url, String title) {
        Intent intent = new Intent(activity, DirtyJokeDetailActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        activity.startActivity(intent);
    }

    private ListView mListView;
    private LoadView mLoadView;

    private DirtyJokeDetailPresenter mPresenter;
    private String mUrl;
    private String mTitle;

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

        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");
    }

    private void initTitles() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_dirty_joke_detail_toolbar);
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
        mListView = (ListView) findViewById(R.id.activity_dirty_joke_detail_listview);
        mLoadView = (LoadView) findViewById(R.id.activity_dirty_joke_detail_loadview);
        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getDirtyJokeDetailData(mUrl, host);
            }
        });

        mPresenter.getDirtyJokeDetailData(mUrl, host);
    }

    public void showDirtyJokesDetailViews(DirtyJokeDetail data) {
        mLoadView.setStatus(LoadView.LoadStatus.Normal);
        mListView.setVisibility(View.VISIBLE);

        View headerView = View.inflate(this, R.layout.layout_post_list_header, null);
        TextView titleView = (TextView) headerView.findViewById(R.id.layout_post_list_header_title);
        titleView.setText(data.title);
        TextView timeView = (TextView) headerView.findViewById(R.id.layout_post_list_header_date);
        timeView.setText(data.time);
        TextView tagView = (TextView) headerView.findViewById(R.id.layout_post_list_header_tag);
        tagView.setText(data.tag);
        TextView commentCountView = (TextView) headerView.findViewById(R.id.layout_post_list_header_comment_count);
        commentCountView.setText(data.commentCount);
        NetworkImageView imageView = (NetworkImageView) headerView.findViewById(R.id.layout_post_list_header_img);
        imageView.setImageUrl(data.imgUrl);
        mListView.addHeaderView(headerView);

        View footerView = View.inflate(this, R.layout.layout_post_list_footer, null);
        TextView sourceView = (TextView) footerView.findViewById(R.id.layout_post_list_footer_source);
        sourceView.setText(data.source);
        mListView.addFooterView(footerView);

        DirtyJokeDetailAdapter adapter = new DirtyJokeDetailAdapter(this, data.posts);
        mListView.setAdapter(adapter);

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
}
