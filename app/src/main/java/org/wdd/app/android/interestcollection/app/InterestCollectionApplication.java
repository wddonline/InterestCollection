package org.wdd.app.android.interestcollection.app;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.common.QueuedWork;
import com.youku.cloud.player.YoukuPlayerConfig;

import org.wdd.app.android.interestcollection.http.HttpManager;
import org.wdd.app.android.interestcollection.utils.BmobUtils;
import org.wdd.app.android.interestcollection.utils.Constants;

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

        YoukuPlayerConfig.setClientIdAndSecret("549fc7c5622c1f61", "1240d184012e29b5dfb15d3ba35b0c4e");
        YoukuPlayerConfig.onInitial(this);
        YoukuPlayerConfig.setLog(false);

        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        Config.DEBUG = Constants.DEBUG;
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(this);
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

    {
        PlatformConfig.setWeixin("wx3e22eb228c61a725", "6ddecdba647b4e72affcb7207dfa417b");
        PlatformConfig.setSinaWeibo("996509505", "ece6bcc4834a485612b58a4c5a17f3d1","https://www.pgyer.com/QGM2");
        PlatformConfig.setQQZone("1106053180", "CNOcdrWmv3xCxXY9");
    }
}
