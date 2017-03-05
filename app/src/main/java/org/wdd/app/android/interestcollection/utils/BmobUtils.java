package org.wdd.app.android.interestcollection.utils;

import android.content.Context;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by richard on 1/27/17.
 */

public class BmobUtils {

    public static void autoUpdateApp(Context context) {
        BmobUpdateAgent.setUpdateOnlyWifi(true);//设置仅在Wifi模式下更新系统
        BmobUpdateAgent.update(context);
    }

    public static void mannelUpdateApp(Context context, BmobUpdateListener listener) {
        BmobUpdateAgent.setUpdateListener(listener);
        BmobUpdateAgent.forceUpdate(context);
    }

    public static void initBombClient(Context context) {
        //集成检测设置
        BmobUpdateAgent.setUpdateCheckConfig(true);
        BmobConfig.Builder builder = new BmobConfig.Builder(context);
        //设置appkey
        builder.setApplicationId("1a4fe80265fa960e6185cd852064cd42");
        //请求超时时间（单位为秒）：默认15s
        builder.setConnectTimeout(30);
        //文件分片上传时每片的大小（单位字节），默认512*1024
        builder.setUploadBlockSize(1024 * 1024);
        //文件的过期时间(单位为秒)：默认1800s
        builder.setFileExpiration(2500);
        BmobConfig config = builder.build();
        Bmob.initialize(config);

        //调试结束，且后台生成AppVersion后注销此代码，否则产生多与记录
//        BmobUpdateAgent.initAppVersion();
    }
}
