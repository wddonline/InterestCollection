package org.wdd.app.android.interestcollection.ui.shares.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.shares.adapter.ShareDetailAdapter;
import org.wdd.app.android.interestcollection.ui.shares.model.ShareDetail;
import org.wdd.app.android.interestcollection.ui.shares.presenter.ShareDetailPresenter;
import org.wdd.app.android.interestcollection.views.LoadView;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

public class ShareDetailActivity extends BaseActivity {

    public static void show(Context context, String url, String title) {
        Intent intent = new Intent(context, ShareDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    private ListView mListView;
    private LoadView mLoadView;

    private ShareDetailPresenter mPresenter;
    private String mUrl;
    private String mTitle;

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

        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");
    }

    private void initTitles() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_share_detail_toolbar);
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
        mListView = (ListView) findViewById(R.id.activity_share_detail_listview);
        mLoadView = (LoadView) findViewById(R.id.activity_share_detail_loadview);
        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getShareDetailData(mUrl, host);
            }
        });

        mPresenter.getShareDetailData(mUrl, host);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.cancelRequest();
    }

    public void showShareDetailViews(ShareDetail data) {
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
        imageView.setVisibility(View.GONE);
        mListView.addHeaderView(headerView);

        View footerView = View.inflate(this, R.layout.layout_post_list_footer, null);
        TextView sourceView = (TextView) footerView.findViewById(R.id.layout_post_list_footer_source);
        sourceView.setText(data.source);
        mListView.addFooterView(footerView);

        ShareDetailAdapter adapter = new ShareDetailAdapter(this, data.nodes);
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
