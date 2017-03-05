package org.wdd.app.android.interestcollection.ui.main.presenter;

import android.os.Handler;
import android.os.Looper;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.http.impl.VolleyTool;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.main.activity.MainActivity;
import org.wdd.app.android.interestcollection.utils.AppToaster;
import org.wdd.app.android.interestcollection.utils.BmobUtils;

import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

/**
 * Created by wangdd on 17-3-3.
 */

public class MainPresenter {

    private MainActivity mView;

    private Handler mHandler;

    public MainPresenter(MainActivity view) {
        this.mView = view;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void queryCacheSize(final ActivityFragmentAvaliable host) {
        Thread queryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final long size = VolleyTool.getInstance(mView).getRequestQueue().getCache().getCacheSize();
                if (host.isAvaliable()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            mView.showCacheSize(size);
                        }
                    });
                }
            }
        });
        queryThread.setDaemon(true);
        queryThread.start();
    }

    public void cleanFileCache(final ActivityFragmentAvaliable host) {
        Thread cleanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final long size = VolleyTool.getInstance(mView).getRequestQueue().getCache().getCacheSize();
                VolleyTool.getInstance(mView).getRequestQueue().getCache().clear();
                if (host.isAvaliable()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mView.finishCacheClean(size);
                        }
                    });
                }
            }
        });
        cleanThread.setDaemon(true);
        cleanThread.start();
    }

    public void checkLastestVersion() {
        BmobUtils.mannelUpdateApp(mView.getBaseContext(), new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                if (updateStatus == UpdateStatus.Yes) {//版本有更新

                }else if(updateStatus == UpdateStatus.No){
                    AppToaster.show(R.string.bmob_update_lastest_already);
                }else if(updateStatus==UpdateStatus.EmptyField){//此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
                    AppToaster.show(R.string.bmob_update_field_error);
                }else if(updateStatus==UpdateStatus.IGNORED){
                    AppToaster.show(R.string.bmob_update_version_ignore);
                }else if(updateStatus==UpdateStatus.ErrorSizeFormat){
                    AppToaster.show(R.string.bmob_update_apk_size_error);
                }else if(updateStatus==UpdateStatus.TimeOut){
                    AppToaster.show(R.string.bmob_update_error);
                }
                mView.finishVersionCheck();
            }
        });
    }
}
