package org.wdd.app.android.interestcollection.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.wdd.app.android.interestcollection.ads.model.AdsSwitcher;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by richard on 2/10/17.
 */

class BmobConfManager {

    private final String ADS_OPEN_STATUS = "ads_open_status";
    private final String APP_REVIEW_STATUS = "app_review_status";

    private AdsSwitcher mSwitcher;
    private SharedPreferences mPreferences;
    private boolean isAppReviewing;

    public BmobConfManager(Context context) {
        mSwitcher = new AdsSwitcher();
        mPreferences = context.getSharedPreferences("ads", Context.MODE_PRIVATE);
        mSwitcher.isOpen = mPreferences.getBoolean(ADS_OPEN_STATUS, true);
        isAppReviewing = mPreferences.getBoolean(APP_REVIEW_STATUS, false);

        mSwitcher.isOpen = true;
//        mSwitcher.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if (e == null) {
//                    Log.e("BmobConfManager", "ok");
//                } else {
//                    Log.e("BmobConfManager", "error");
//                }
//            }
//        });
    }

    public void init() {
        BmobQuery<AdsSwitcher> query = new BmobQuery<>();
        query.setLimit(1);
        query.findObjects(new FindListener<AdsSwitcher>() {
            @Override
            public void done(List<AdsSwitcher> list, BmobException e) {
                if (e == null && list.size() > 0) {
                    mSwitcher.isOpen = list.get(0).isOpen;
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putBoolean(ADS_OPEN_STATUS, mSwitcher.isOpen);
                    editor.commit();
                }
            }
        });
    }

    public void setAppReviewStatus(boolean isAppReviewing) {
        this.isAppReviewing = isAppReviewing;
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(APP_REVIEW_STATUS, isAppReviewing);
        editor.commit();
    }

    public boolean isAdsOpen() {
        return mSwitcher.isOpen;
    }

    public boolean isAppReviewing() {
        return isAppReviewing;
    }
}
