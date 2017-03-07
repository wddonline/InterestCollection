package org.wdd.app.android.interestcollection.ui.favorites.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.favorites.adapter.AudioFavoritesAdapter;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.impl.AudioFavoritesFragment;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.impl.DirtyJokeFavoritesFragment;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.FavoritesFragment;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.impl.ImageFavoritesFragment;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.impl.ShareFavoritesFragment;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.impl.VideoFavoritesFragment;
import org.wdd.app.android.interestcollection.utils.AppToaster;
import org.wdd.app.android.interestcollection.views.NewViewPager;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends BaseActivity implements FavoritesActionCallback {

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, FavoritesActivity.class);
        activity.startActivity(intent);
    }

    public static final int REQUEST_CODE_DETAIL = 1;

    private Toolbar mToolbar;
    private CheckBox mCheckBox;
    private NewViewPager mViewPager;

    private List<FavoritesFragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        initData();
        initTitles();
        initViews();
    }

    private void initData() {
        mFragments = new ArrayList<>();

        DirtyJokeFavoritesFragment dirtyJoke = new DirtyJokeFavoritesFragment();
        dirtyJoke.setCallback(this);
        mFragments.add(dirtyJoke);

        ImageFavoritesFragment image = new ImageFavoritesFragment();
        image.setCallback(this);
        mFragments.add(image);

        VideoFavoritesFragment video = new VideoFavoritesFragment();
        video.setCallback(this);
        mFragments.add(video);

        AudioFavoritesFragment audio = new AudioFavoritesFragment();
        audio.setCallback(this);
        mFragments.add(audio);

        ShareFavoritesFragment share = new ShareFavoritesFragment();
        share.setCallback(this);
        mFragments.add(share);
    }

    private void initTitles() {
        mToolbar = (Toolbar) findViewById(R.id.activity_favorites_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.collect);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCheckBox = (CheckBox) findViewById(R.id.activity_favorites_all);
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoritesFragment fragment = mFragments.get(mViewPager.getCurrentItem());
                if (mCheckBox.isChecked()) {
                    fragment.selectAll();
                } else {
                    fragment.unselectAll();
                }
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FavoritesFragment fragment = mFragments.get(mViewPager.getCurrentItem());
                switch (item.getItemId()) {
                    case R.id.menu_favorites_cancel:
                        if (fragment.getMode() == AudioFavoritesAdapter.Mode.Select) {
                            cancelSelectMode();
                        }
                        return true;
                    case R.id.menu_favorites_ok:
                        if (fragment.getSelectedCount() == 0) {
                            AppToaster.show(R.string.no_favorite_selected);
                            return true;
                        }
                        fragment.deleteSelectedFavorites();
                        return true;
                }
                return false;
            }
        });
    }

    private void initViews() {
        mViewPager = (NewViewPager) findViewById(R.id.activity_favorites_viewpager);
        mViewPager.setAdapter(new FavoritesPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);

        PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.activity_favorites_tabs);
        int color = ContextCompat.getColor(this, R.color.colorPrimary);
        tabStrip.setTextColor(color);
        tabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tabStrip.setTabIndicatorColor(color);
    }

    private void cancelSelectMode() {
        FavoritesFragment fragment = mFragments.get(mViewPager.getCurrentItem());
        fragment.cancelSelectMode();
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        getSupportActionBar().setTitle(R.string.collect);
        mViewPager.setCanSwitch(true);
        mCheckBox.setVisibility(View.GONE);
        mToolbar.getMenu().findItem(R.id.menu_favorites_ok).setVisible(false);
        mToolbar.getMenu().findItem(R.id.menu_favorites_cancel).setVisible(false);
        mCheckBox.setChecked(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        FavoritesFragment fragment = mFragments.get(mViewPager.getCurrentItem());
        if (fragment.getMode() == AudioFavoritesAdapter.Mode.Select) {
            cancelSelectMode();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_CODE_DETAIL:
                int position = data.getIntExtra("position", -1);
                if (position == -1) return;
                FavoritesFragment fragment = mFragments.get(mViewPager.getCurrentItem());
                fragment.refreshDataRemoved(position);
                break;
        }
    }

    @Override
    public void switchSelectMode() {
        mViewPager.setCanSwitch(false);
        mToolbar.setNavigationIcon(null);
        getSupportActionBar().setTitle("");
        mCheckBox.setVisibility(View.VISIBLE);
        mToolbar.getMenu().findItem(R.id.menu_favorites_ok).setVisible(true);
        mToolbar.getMenu().findItem(R.id.menu_favorites_cancel).setVisible(true);
    }

    @Override
    public void onAllSelected() {
        mCheckBox.setChecked(true);
    }

    @Override
    public void onPartSelected() {
        mCheckBox.setChecked(false);
    }

    @Override
    public void resetTitleBar() {
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mCheckBox.setVisibility(View.GONE);
        mToolbar.getMenu().findItem(R.id.menu_favorites_ok).setVisible(false);
        mToolbar.getMenu().findItem(R.id.menu_favorites_cancel).setVisible(false);
        mCheckBox.setChecked(false);
    }

    private class FavoritesPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = { getString(R.string.dirty_joke), getString(R.string.image),
                                          getString(R.string.video), getString(R.string.audio),
                                          getString(R.string.share)};

        public FavoritesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }
}
