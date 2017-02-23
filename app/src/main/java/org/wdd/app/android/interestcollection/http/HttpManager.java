package org.wdd.app.android.interestcollection.http;

import android.content.Context;
import android.util.Log;

import org.wdd.app.android.interestcollection.http.impl.VolleyHttpConnecter;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;

/**
 * Created by wangdd on 16-11-26.
 */

public class HttpManager {
    private final String TAG = "HttpManager";

    private static HttpManager INSTANCE;

    public static HttpManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (HttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpManager(context);
                }
            }
        }
        return INSTANCE;
    }

    private HttpConnecter connecter;

    private HttpManager(Context context) {
        connecter = new VolleyHttpConnecter(context);
    }

    public void setConnecter(HttpConnecter connecter) {
        this.connecter = connecter;
    }

    public HttpSession sendHttpRequest(ActivityFragmentAvaliable host, HttpRequestEntry entry, Class clazz, HttpConnectCallback callback) {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnecter getInstance isn\'t setted for HttpManager");
            return null;
        }
        return connecter.sendHttpRequest(host, entry, clazz, callback);
    }

    public HttpSession sendHttpRequest(ActivityFragmentAvaliable host, HttpRequestEntry entry, HttpConnectCallback callback) {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnecter getInstance isn\'t setted for HttpManager");
            return null;
        }
        return connecter.sendHttpRequest(host, entry, callback);
    }

    public HttpSession sendHtmlRequest(ActivityFragmentAvaliable host, HttpRequestEntry entry, HttpConnectCallback callback) {
        return sendHtmlRequest(null, host, entry, callback);
    }

    public HttpSession sendHtmlRequest(String enocde, ActivityFragmentAvaliable host, HttpRequestEntry entry, HttpConnectCallback callback) {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnecter getInstance isn\'t setted for HttpManager");
            return null;
        }
        return connecter.sendHtmlRequest(enocde, host, entry, callback);
    }

    public void stopAllSession() {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnecter getInstance isn\'t setted for HttpManager");
            return;
        }
        connecter.stopAllSession();
    }

    public void stopSessionByTag(String tag) {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnecter getInstance isn\'t setted for HttpManager");
            return;
        }
        connecter.stopSessionByTag(tag);
    }
}
