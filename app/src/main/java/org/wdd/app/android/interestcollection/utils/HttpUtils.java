package org.wdd.app.android.interestcollection.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.http.error.ErrorCode;

/**
 * Created by richard on 2/17/17.
 */

public class HttpUtils {

    public static boolean isNetworkEnabled(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getErrorDescFromErrorCode(Context context, int errorCode) {
        switch (errorCode) {
            case ErrorCode.AUTH_FAILURE_ERROR:
                return context.getString(R.string.auth_failure_error);
            case ErrorCode.NETWORK_ERROR:
                return context.getString(R.string.network_error);
            case ErrorCode.NO_CONNECTION_ERROR:
                return context.getString(R.string.no_connection_error);
            case ErrorCode.PARSE_ERROR:
                return context.getString(R.string.parse_error);
            case  ErrorCode.SERVER_ERROR:
                return context.getString(R.string.server_error);
            case ErrorCode.TIMEOUT_ERROR:
                return context.getString(R.string.timeout_error);
        }
        return context.getString(R.string.unknown_error);
    }
}
