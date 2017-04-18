package org.wdd.app.android.interestcollection.ui.settings.data;

import android.content.Context;

import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.settings.model.AppModel;
import org.wdd.app.android.interestcollection.utils.HttpUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by richard on 4/14/17.
 */

public class AppWallDataGetter {

    private Context mContext;
    private DataCallback mCallback;

    public AppWallDataGetter(Context context, AppWallDataGetter.DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    public void requestAppListData(final ActivityFragmentAvaliable host) {
        if (!HttpUtils.isNetworkEnabled(mContext)) {
            mCallback.onNetworkError();
            return;
        }
        BmobQuery<AppModel> query = new BmobQuery<>();
        query.findObjects(new FindListener<AppModel>() {
            @Override
            public void done(List<AppModel> list, BmobException e) {
                if (!host.isAvaliable()) return;
                if (e == null) {
                    mCallback.onRequestOk(list);
                    return;
                }
                mCallback.onRequestError(e.getMessage());
            }
        });
    }

    public void cancelRequest() {

    }

    public interface DataCallback {

        void onRequestOk(List<AppModel> apps);
        void onRequestError(String error);
        void onNetworkError();

    }
}
