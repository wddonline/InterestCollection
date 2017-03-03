package org.wdd.app.android.interestcollection.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabWidget;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.app.InterestCollectionApplication;
import org.wdd.app.android.interestcollection.ui.audios.fragment.AudiosFragment;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.images.fragment.ImagesFragment;
import org.wdd.app.android.interestcollection.ui.jokes.fragment.DirtyJokesFragment;
import org.wdd.app.android.interestcollection.ui.shares.fragment.SharesFragment;
import org.wdd.app.android.interestcollection.ui.videos.fragment.VideosFragment;
import org.wdd.app.android.interestcollection.utils.AppToaster;
import org.wdd.app.android.interestcollection.utils.AppUtils;
import org.wdd.app.android.interestcollection.utils.DensityUtils;
import org.wdd.app.android.interestcollection.views.FragmentTabHost;

/**
 * Created by richard on 2/10/17.
 */

public class MainActivity extends BaseActivity implements Runnable {

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    private FragmentTabHost mTabHost;
    private SlidingMenu mSlidingMenu;

    private Handler handler = new Handler();

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

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        View tabLayout;
        ImageView tabImgView;
        TextView tabTxtView;

        int[] tabIcons = {R.mipmap.ic_paper, R.mipmap.ic_paper, R.mipmap.ic_paper, R.mipmap.ic_paper,
                R.mipmap.ic_paper};
        int[] tabTxts = {R.string.dirty_joke, R.string.image, R.string.video, R.string.audio, R.string.share};
        String[] tabTags = {"dirty_joke", "image", "video", "audio", "news"};
        Class[] tabClasses = {DirtyJokesFragment.class, ImagesFragment.class, VideosFragment.class,
                AudiosFragment.class, SharesFragment.class};

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

}
