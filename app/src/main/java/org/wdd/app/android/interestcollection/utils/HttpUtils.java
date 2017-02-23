package org.wdd.app.android.interestcollection.utils;

import android.content.Context;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.http.error.ErrorCode;

/**
 * Created by richard on 2/17/17.
 */

public class HttpUtils {

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
