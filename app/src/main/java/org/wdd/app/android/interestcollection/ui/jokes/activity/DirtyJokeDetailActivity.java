package org.wdd.app.android.interestcollection.ui.jokes.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import org.wdd.app.android.interestcollection.app.ActivityTaskStack;
import org.wdd.app.android.interestcollection.app.InterestCollectionApplication;
import org.wdd.app.android.interestcollection.database.model.DirtyJokeFavorite;
import org.wdd.app.android.interestcollection.permission.PermissionListener;
import org.wdd.app.android.interestcollection.permission.PermissionManager;
import org.wdd.app.android.interestcollection.permission.Rationale;
import org.wdd.app.android.interestcollection.permission.RationaleListener;
import org.wdd.app.android.interestcollection.permission.SettingDialog;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.jokes.adapter.DirtyJokeDetailAdapter;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJoke;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJokeDetail;
import org.wdd.app.android.interestcollection.ui.jokes.presenter.DirtyJokeDetailPresenter;
import org.wdd.app.android.interestcollection.ui.main.activity.MainActivity;
import org.wdd.app.android.interestcollection.utils.Constants;
import org.wdd.app.android.interestcollection.views.LoadView;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.List;

public class DirtyJokeDetailActivity extends BaseActivity implements PermissionListener {

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

    private final int REQUEST_PERMISSION_CODE = 100;

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
    private UMShareListener mShareListener;
    private ShareAction mShareAction;

    private int id;
    private boolean initCollectStatus = false;
    private boolean currentCollectStatus = initCollectStatus;
    private boolean isCheckRequired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dirty_joke_detail);
        initData();
        initTitles();
        initViews();
        checkPermission();
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
                if (ActivityTaskStack.getInstance().getActivityCount() == 1) {
                    MainActivity.show(DirtyJokeDetailActivity.this);
                } else {
                    backAction();
                }
                finish();
            }
        });
        getSupportActionBar().setTitle(mJoke.title);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_detail_collect:
                        mPresenter.collectGirl(mJoke.title, mJoke.date, mJoke.url, mJoke.imgUrl, host);
                        return true;
                    case R.id.menu_detail_uncollect:
                        mPresenter.uncollectGirl(mFavorite.id, host);
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
        mListView = (ListView) findViewById(R.id.activity_dirty_joke_detail_listview);
        mLoadView = (LoadView) findViewById(R.id.activity_dirty_joke_detail_loadview);

        mLoadView.setReloadClickedListener(new LoadView.OnReloadClickedListener() {
            @Override
            public void onReloadClicked() {
                mPresenter.getDirtyJokeDetailData(mJoke.url, host);
            }
        });

        mShareListener = new CustomShareListener(this);
        mShareAction = new ShareAction(this).setDisplayList(
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        String url = "http://www.pgyer.com/QGM2";
                        try {
                            url += "?type=1&path=" + URLEncoder.encode(mJoke.url, "utf-8") + "&name=" + mJoke.title +
                                    "&ico=" + URLEncoder.encode(mJoke.imgUrl, "utf8") + "&date=" + mJoke.date;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        UMWeb web = new UMWeb(url);
                        web.setTitle(getString(R.string.dirty_joke));
                        web.setDescription(mJoke.title);
                        web.setThumb(new UMImage(getBaseContext(), mJoke.imgUrl));
                        new ShareAction(DirtyJokeDetailActivity.this).withMedia(web)
                                .setPlatform(share_media)
                                .setCallback(mShareListener)
                                .share();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCheckRequired) {
            checkPermission();
            isCheckRequired = false;
        }
    }

    private void checkPermission() {
        PermissionManager.with(this)
                .requestCode(REQUEST_PERMISSION_CODE)
                .permission(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        PermissionManager.rationaleDialog(DirtyJokeDetailActivity.this, rationale).show();
                    }
                }).send();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onSucceed(int requestCode, List<String> grantPermissions) {
        mPresenter.getDirtyJokeDetailData(mJoke.url, host);
    }

    @Override
    public void onFailed(int requestCode, List<String> deniedPermissions) {
        if (PermissionManager.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            // 第一种：用默认的提示语。
            PermissionManager.defaultSettingDialog(this)
                    .setSettingDialogListener(new SettingDialog.SettingDialogListener() {
                        @Override
                        public void onSettingClicked() {
                            isCheckRequired = true;
                        }

                        @Override
                        public void onCancelClicked() {
                            finish();
                        }
                    }).show();
        } else {
            finish();
        }
    }

    private static class CustomShareListener implements UMShareListener {

        private WeakReference<DirtyJokeDetailActivity> mActivity;

        private CustomShareListener(DirtyJokeDetailActivity activity) {
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
    public void onBackPressed() {
        if (ActivityTaskStack.getInstance().getActivityCount() == 1) {
            MainActivity.show(DirtyJokeDetailActivity.this);
        } else {
            backAction();
        }
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
        mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(!initCollectStatus);
        mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(initCollectStatus);
    }

    public void updateDirtyJokeCollectViews(DirtyJokeFavorite favorite) {
        currentCollectStatus = true;
        mFavorite = favorite;
        mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(false);
        mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(true);
    }

    public void showDirtyJokeUncollectViews(boolean success) {
        if (success) {
            currentCollectStatus = false;
            mFavorite = null;
            mToolbar.getMenu().findItem(R.id.menu_detail_collect).setVisible(true);
            mToolbar.getMenu().findItem(R.id.menu_detail_uncollect).setVisible(false);
        }
    }

}
