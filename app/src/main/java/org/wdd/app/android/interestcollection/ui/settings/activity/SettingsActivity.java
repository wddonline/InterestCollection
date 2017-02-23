package org.wdd.app.android.interestcollection.ui.settings.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.settings.presenter.SettingsPresenter;
import org.wdd.app.android.interestcollection.utils.AppToaster;
import org.wdd.app.android.interestcollection.utils.MathUtils;

public class SettingsActivity extends BaseActivity {

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    private TextView trashView;

    private SettingsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initData();
        initTitles();
        initViews();
    }

    private void initData() {
        mPresenter = new SettingsPresenter(this);
    }

    private void initTitles() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        trashView = (TextView) findViewById(R.id.layout_main_navigation_header_trash_size);

        mPresenter.queryCacheSize(host);
    }

    public void onVersionUpdateClicked(View v) {
        showLoadingDialog();
        mPresenter.checkLastestVersion();
    }

    public void onTrashClicked(View v) {
        showLoadingDialog();
        mPresenter.cleanFileCache(host);
    }

    public void onAboutClicked(View v) {
        AboutActivity.show(this);
    }

    public void showCacheSize(long size) {
        trashView.setText(getFileSizeString(size));
    }

    public void finishCacheClean(long size) {
        hideLoadingDialog();
        trashView.setText("0B");
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

    public void finishVersionCheck() {
        hideLoadingDialog();
    }
}
