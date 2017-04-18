package org.wdd.app.android.interestcollection.app;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import com.umeng.analytics.MobclickAgent;
import com.youku.cloud.player.YoukuPlayerConfig;

import org.wdd.app.android.interestcollection.http.HttpManager;
import org.wdd.app.android.interestcollection.utils.BmobUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdd on 16-11-27.
 */

public class InterestCollectionApplication extends Application {

    private static InterestCollectionApplication INSTANCE;

    public static InterestCollectionApplication getInstance() {
        return INSTANCE;
    }

    private Handler uiHandler;
    private Map<String, Object> tempZone;
    private BmobConfManager bombConfManager;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        tempZone = new HashMap<>();

        uiHandler = new Handler(Looper.getMainLooper());
        BmobUtils.initBombClient(this);

        //设置umeng统计场景
        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(10 * 60 * 1000);

        bombConfManager = new BmobConfManager(this);
        bombConfManager.init();

//        YoukuPlayerConfig.setClientIdAndSecret(YoukuProfile.CLIENT_ID,YoukuProfile.CLIENT_SECRET);
        YoukuPlayerConfig.setClientIdAndSecret("549fc7c5622c1f61", "1240d184012e29b5dfb15d3ba35b0c4e");
        YoukuPlayerConfig.onInitial(this);
        YoukuPlayerConfig.setLog(false);
    }

    public boolean isAdsOpen() {
        return bombConfManager.isAdsOpen();
    }

    public void setAppReviewStatus(boolean isAppReviewing) {
        bombConfManager.setAppReviewStatus(isAppReviewing);
    }

    public boolean getAppReviewStatus() {
        return bombConfManager.isAppReviewing();
    }

    public Handler getUiHandler() {
        return uiHandler;
    }

    public void putTempData(String key, Object data) {
        tempZone.put(key, data);
    }

    public Object getTempData(String key) {
        Object data = tempZone.get(key);
        tempZone.remove(key);
        return data;
    }

    public void exitApp() {
        ActivityTaskStack.getInstance().clearActivities();
        HttpManager.getInstance(this).stopAllSession();
        MobclickAgent.onKillProcess(this);
        Process.killProcess(Process.myPid());
    }
}
