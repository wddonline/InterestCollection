package org.wdd.app.android.interestcollection.ads.builder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.utils.Constants;

import java.util.List;

/**
 * Created by richard on 2/5/17.
 */

public class NativeAdsBuilder implements NativeAD.NativeAdListener {

    private Activity mActivity;
    private ViewGroup mOutContainer;
    private View mRootView;

    private NativeADDataRef mAdItem;
    private NativeAD mNativeAD;
    protected AQuery mQuery;

    public NativeAdsBuilder(Activity activity, ViewGroup outContainer, String adId) {
        this.mActivity = activity;
        this.mOutContainer = outContainer;
        mRootView = View.inflate(activity, R.layout.layout_native_ads, null);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mOutContainer.addView(mRootView, lp);
        mRootView.setVisibility(View.GONE);

        mQuery = new AQuery(activity);
        mNativeAD = new NativeAD(activity, Constants.TENCENT_APP_ID, adId, this);
        int count = 1; // 一次拉取的广告条数：范围1-10
        mNativeAD.loadAD(count);
    }

    public void showAD() {
        mRootView.setVisibility(View.VISIBLE);

        mQuery.id(R.id.layout_native_ads_logo).image((String) mAdItem.getIconUrl(), false, true);
        mQuery.id(R.id.layout_native_ads_poster).image(mAdItem.getImgUrl(), false, true);
        mQuery.id(R.id.layout_native_ads_name).text((String) mAdItem.getTitle());
        mQuery.id(R.id.layout_native_ads_desc).text((String) mAdItem.getDesc());
        mQuery.id(R.id.layout_native_ads_download).text(getADButtonText());
        mAdItem.onExposured(mRootView); // 需要先调用曝光接口
        mQuery.id(R.id.layout_native_ads_download).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdItem.onClicked(view); // 点击接口
            }
        });
    }

    @Override
    public void onADLoaded(List<NativeADDataRef> list) {
        if (list.size() > 0) {
            mAdItem = list.get(0);
            showAD();
        } else {
            mOutContainer.removeView(mRootView);
        }
    }

    @Override
    public void onADStatusChanged(NativeADDataRef nativeADDataRef) {
        mQuery.id(R.id.layout_native_ads_download).text(getADButtonText());
    }

    @Override
    public void onNoAD(int i) {
        mOutContainer.removeView(mRootView);
    }

    @Override
    public void onADError(NativeADDataRef nativeADDataRef, int i) {
        mOutContainer.removeView(mRootView);
    }

    /**
     * App类广告安装、下载状态的更新（普链广告没有此状态，其值为-1） 返回的AppStatus含义如下： 0：未下载 1：已安装 2：已安装旧版本 4：下载中（可获取下载进度“0-100”）
     * 8：下载完成 16：下载失败
     */
    private String getADButtonText() {
        if (mAdItem == null) {
            return "……";
        }
        if (!mAdItem.isAPP()) {
            return mActivity.getString(R.string.view_detail);
        }
        switch (mAdItem.getAPPStatus()) {
            case 0:
                return mActivity.getString(R.string.click_to_download);
            case 1:
                return mActivity.getString(R.string.click_to_start);
            case 2:
                return mActivity.getString(R.string.click_to_update);
            case 4:
                String downloading = mActivity.getString(R.string.downloading);
                return mAdItem.getProgress() > 0 ? downloading + mAdItem.getProgress()+ "%" : downloading; // 特别注意：当进度小于0时，不要使用进度来渲染界面
            case 8:
                return mActivity.getString(R.string.download_completely);
            case 16:
                return mActivity.getString(R.string.download_failure);
            default:
                return mActivity.getString(R.string.view_detail);
        }
    }


}
