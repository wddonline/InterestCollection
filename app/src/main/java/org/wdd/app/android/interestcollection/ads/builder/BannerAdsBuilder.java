package org.wdd.app.android.interestcollection.ads.builder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.app.InterestCollectionApplication;
import org.wdd.app.android.interestcollection.utils.Constants;

import java.util.Calendar;

/**
 * Created by richard on 2/5/17.
 */

public class BannerAdsBuilder implements View.OnClickListener {

    private Activity mActivity;
    private ViewGroup mOutContainer;
    private View mRootView;
    private BannerView mBannerView;
    private RelativeLayout mBannerAdsContainer;
    private View mCloseView;

    private String mAdId;
    private boolean mIsPersistent;

    public BannerAdsBuilder(Activity activity, ViewGroup outContainer, String adId) {
        this(activity, outContainer, adId, false);
    }

    public BannerAdsBuilder(Activity activity, ViewGroup outContainer, String adId, boolean isPersistent) {
        this.mActivity = activity;
        this.mOutContainer = outContainer;
        this.mAdId = adId;
        this.mIsPersistent = isPersistent;

        mRootView = View.inflate(activity, R.layout.layout_banner_ads, null);
        mBannerAdsContainer = (RelativeLayout) mRootView.findViewById(R.id.layout_banner_ads_container);
        mCloseView = mRootView.findViewById(R.id.layout_banner_ads_close);
        mCloseView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        closeBannerAds();
    }

    public void addBannerAds() {
        if (mRootView.getParent() != null) {
            mOutContainer.removeView(mRootView);
        }

        mCloseView.setVisibility(View.GONE);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mOutContainer.addView(mRootView, 0, lp);

        mBannerView = new BannerView(mActivity, ADSize.BANNER, Constants.TENCENT_APP_ID, mAdId);
        mBannerView.setRefresh(30);
        mBannerView.setADListener(new AbstractBannerADListener() {
            @Override
            public void onNoAD(int i) {

            }

            @Override
            public void onADReceiv() {
                if (mIsPersistent) return;
                mCloseView.setVisibility(View.VISIBLE);
            }
        });
        mBannerAdsContainer.addView(mBannerView);

        mOutContainer.post(new Runnable() {
            @Override
            public void run() {
                mBannerView.loadAD();
            }
        });
    }

    public void closeBannerAds() {
        if (mRootView.getParent() == null) return;

        mBannerView.destroy();
        mBannerAdsContainer.removeView(mBannerView);
        mOutContainer.removeView(mRootView);

    }

    public static boolean shouldShowAds() {
        if (!InterestCollectionApplication.getInstance().isAdsOpen()) return false;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour % 2 == 0) {
            return true;
        }
        return true;
    }
}
