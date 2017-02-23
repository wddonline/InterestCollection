package org.wdd.app.android.interestcollection.ui.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.views.LoadingDialog;

/**
 * Created by richard on 11/28/16.
 */

public abstract class BaseFragment extends Fragment {

    private LoadingDialog loadingDialog;
    public ActivityFragmentAvaliable host = new ActivityFragmentAvaliable(this);

    private boolean dataLoaded = false;
    private boolean viewIninted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, container, savedInstanceState);
        viewIninted = true;
        if (dataLoaded) return view;
        if (!getUserVisibleHint()) return view;
        lazyLoad();
        dataLoaded = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) return;
        if (!viewIninted) return;
        if (dataLoaded) return;
        lazyLoad();
        dataLoaded = true;
    }

    protected void showLoadingDialog() {
        showLoadingDialog(null);
    }

    protected void showLoadingDialog(DialogInterface.OnCancelListener listener) {
        if (!host.isAvaliable()) return;
        if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
        loadingDialog = new LoadingDialog(getActivity());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.tip);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.confirm, null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void lazyLoad();
}
