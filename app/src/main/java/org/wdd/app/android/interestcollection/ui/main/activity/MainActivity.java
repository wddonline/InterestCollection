package org.wdd.app.android.interestcollection.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabWidget;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.app.InterestCollectionApplication;
import org.wdd.app.android.interestcollection.preference.AppConfManager;
import org.wdd.app.android.interestcollection.ui.audios.fragment.AudiosFragment;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.favorites.activity.FavoritesActivity;
import org.wdd.app.android.interestcollection.ui.images.fragment.ImagesFragment;
import org.wdd.app.android.interestcollection.ui.jokes.fragment.DirtyJokesFragment;
import org.wdd.app.android.interestcollection.ui.main.presenter.MainPresenter;
import org.wdd.app.android.interestcollection.ui.profile.activity.ProfileEditActivity;
import org.wdd.app.android.interestcollection.ui.settings.activity.AboutActivity;
import org.wdd.app.android.interestcollection.ui.settings.activity.AppWallActivity;
import org.wdd.app.android.interestcollection.ui.shares.fragment.SharesFragment;
import org.wdd.app.android.interestcollection.ui.videos.fragment.VideosFragment;
import org.wdd.app.android.interestcollection.utils.AppToaster;
import org.wdd.app.android.interestcollection.utils.AppUtils;
import org.wdd.app.android.interestcollection.utils.BmobUtils;
import org.wdd.app.android.interestcollection.utils.DensityUtils;
import org.wdd.app.android.interestcollection.utils.MathUtils;
import org.wdd.app.android.interestcollection.views.FragmentTabHost;

/**
 * Created by richard on 2/10/17.
 */

public class MainActivity extends BaseActivity implements Runnable {

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    private final int PROFILE_REQUEST_CODE = 1;

    private FragmentTabHost mTabHost;
    private SlidingMenu mSlidingMenu;
    private TextView mNameView;
    private ImageView mHeaderView;

    private Handler handler = new Handler();
    private MainPresenter mPresenter;

    private final long TIME_LIMIT = 3000;
    private int backPressedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        MobclickAgent.openActivityDurationTrack(false);
        initData();
        initTitles();
        initViews();
        BmobUtils.autoUpdateApp(this);
    }

    private void initTitles() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.showMenu();
            }
        });
    }

    private void initData() {
        mPresenter = new MainPresenter(this);
    }

    private void initViews() {
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setShadowDrawable(R.drawable.shadow);
        int shadowWidth = DensityUtils.dip2px(this, 3);
        mSlidingMenu.setShadowWidth(shadowWidth);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.layout_home_menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusHeight = AppUtils.getStatusHeight(this);
            View statusBar = findViewById(R.id.activity_main_statusbar);
            statusBar.setVisibility(View.VISIBLE);
            statusBar.getLayoutParams().height = statusHeight;
            View menuStatusBar = findViewById(R.id.layout_home_menu_statusbar);
            menuStatusBar.setVisibility(View.VISIBLE);
            menuStatusBar.getLayoutParams().height = statusHeight;
        }
        if(InterestCollectionApplication.getInstance().getAppReviewStatus()) {
            findViewById(R.id.layout_home_menu_app).setVisibility(View.GONE);
            findViewById(R.id.layout_home_menu_app_divider).setVisibility(View.GONE);
        }

        mHeaderView = (ImageView) findViewById(R.id.layout_home_menu_headimg);
        mNameView = (TextView) findViewById(R.id.layout_home_menu__name);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        View tabLayout;
        ImageView tabImgView;
        TextView tabTxtView;

        int[] tabIcons;
        int[] tabTxts;
        String[] tabTags;
        Class[] tabClasses;
        if (InterestCollectionApplication.getInstance().getAppReviewStatus()) {
            tabIcons = new int[]{R.drawable.ic_tab_joke, R.drawable.ic_tab_video, R.drawable.ic_tab_audio, R.drawable.ic_tab_share};
            tabTxts = new int[]{R.string.dirty_joke, R.string.video, R.string.audio, R.string.share};
            tabTags = new String[]{"dirty_joke", "video", "audio", "news"};
            tabClasses = new Class[]{DirtyJokesFragment.class, VideosFragment.class, AudiosFragment.class, SharesFragment.class};
        } else {
            tabIcons = new int[]{R.drawable.ic_tab_joke,R.drawable.ic_tab_image, R.drawable.ic_tab_video, R.drawable.ic_tab_audio, R.drawable.ic_tab_share};
            tabTxts = new int[]{R.string.dirty_joke, R.string.image, R.string.video, R.string.audio, R.string.share};
            tabTags = new String[]{"dirty_joke", "image", "video", "audio", "news"};
            tabClasses = new Class[]{DirtyJokesFragment.class, ImagesFragment.class, VideosFragment.class, AudiosFragment.class, SharesFragment.class};
        }

        int tabCount = tabIcons.length;

        for (int i = 0; i < tabCount; i++) {
            tabLayout = getLayoutInflater().inflate(R.layout.layout_main_tab, null, false);
            tabImgView = (ImageView) tabLayout.findViewById(R.id.layout_main_tab_img);
            tabTxtView = (TextView) tabLayout.findViewById(R.id.layout_main_tab_txt);
            tabTxtView.setText(tabTxts[i]);
            tabImgView.setImageResource(tabIcons[i]);
            mTabHost.addTab(mTabHost.newTabSpec(tabTags[i]).setIndicator(tabLayout), tabClasses[i], null);
        }

        int tabWidth = getResources().getDisplayMetrics().widthPixels / 4;
        TabWidget tabWidget = mTabHost.getTabWidget();
        tabWidget.setBackgroundColor(ActivityCompat.getColor(this, R.color.colorNavigationBar));
        for (int i = 0; i < tabCount; i++) {
            tabWidget.getChildTabViewAt(i).getLayoutParams().width = tabWidth;
        }

        setHeaderAndName();
    }

    private void setHeaderAndName() {
        AppConfManager confManager = AppConfManager.getInstance(this);
        String nickname = confManager.getNickname();
        if (!TextUtils.isEmpty(nickname)) {
            mNameView.setText(nickname);
        } else {
            mNameView.setText(R.string.waiting_for_a_name);
        }
        String sex = confManager.getSex();
        if (sex.equals("0")) {
            mHeaderView.setImageResource(R.drawable.ic_male_header);
        } else {
            mHeaderView.setImageResource(R.drawable.ic_female_header);
        }
    }

    @Override
    public void onBackPressed() {
        if (mSlidingMenu.isMenuShowing()) {
            mSlidingMenu.toggle();
            return;
        }
        if (backPressedCount < 1) {
            handler.postDelayed(this, TIME_LIMIT);
            AppToaster.show(R.string.back_to_exit);
            backPressedCount++;
        } else {
            handler.removeCallbacks(this);
            InterestCollectionApplication.getInstance().exitApp();
        }
    }

    @Override
    public void run() {
        backPressedCount = 0;
    }

    public void finishVersionCheck() {
        hideLoadingDialog();
    }

    public void finishCacheClean(long size) {
        hideLoadingDialog();
        String hint = getString(R.string.disk_cache_clean_result);
        hint = String.format(hint, getFileSizeString(size));
        AppToaster.show(hint);
    }

    private String getFileSizeString(long size) {
        String str;
        if (size >= 1073741824) {//1024 * 1024 * 1024
            str = MathUtils.formatDouble(size / 1073741824.0) + "G";
        } else if (size >= 1048576) {////1024 * 1024
            str = MathUtils.formatDouble(size / 1048576.0) + "M";
        } else if (size >= 1024){
            str = MathUtils.formatDouble(size / 1024.0) + "K";
        } else {
            str = size + "B";
        }
        return str;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case PROFILE_REQUEST_CODE:
                setHeaderAndName();
                break;
        }
    }

    public void onProfileClicked(View v) {
        ProfileEditActivity.showForResult(this, v, PROFILE_REQUEST_CODE);
    }

    public void onFavoritesClicked(View v) {
        FavoritesActivity.show(this);
    }

    public void onVersionCheckClicked(View v) {
        showLoadingDialog();
        mPresenter.checkLastestVersion();
    }

    public void onClearCacheClicked(View v) {
        showLoadingDialog();
        mPresenter.cleanFileCache(host);
    }

    public void onAppRecommendationClicked(View v) {
        AppWallActivity.show(this);
    }

    public void onAboutClicked(View v) {
        AboutActivity.show(this);
    }
}
