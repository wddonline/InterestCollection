package org.wdd.app.android.interestcollection.ui.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.app.ActivityTaskStack;
import org.wdd.app.android.interestcollection.views.LoadingDialog;

import java.util.List;

/**
 * Created by wangdd on 16-11-26.
 */

public class BaseActivity extends AppCompatActivity {

    private LoadingDialog loadingDialog;
    public ActivityFragmentAvaliable host = new ActivityFragmentAvaliable(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTaskStack.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityTaskStack.getInstance().removeActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) return;
        for (Fragment fragment : fragments) {
            if (fragment == null) continue;
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void showLoadingDialog() {
        showLoadingDialog(null);
    }

    protected void showLoadingDialog(DialogInterface.OnCancelListener listener) {
        if (!host.isAvaliable()) return;
        if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
        loadingDialog = new LoadingDialog(this);
        if (listener != null) loadingDialog.setOnCancelListener(listener);
        loadingDialog.show();
    }

    protected void hideLoadingDialog() {
        if(loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    protected void showMessageDialog(String msg) {
        if (!host.isAvaliable()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tip);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.confirm, null);
        Dialog dialog = builder.create();
        dialog.show();
    }
}
