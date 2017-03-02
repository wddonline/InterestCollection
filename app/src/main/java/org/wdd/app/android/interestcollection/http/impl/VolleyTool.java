package org.wdd.app.android.interestcollection.http.impl;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by richard on 11/28/16.
 */

public class VolleyTool {

    private static VolleyTool INSTANCE;

    public static VolleyTool getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (VolleyTool.class) {
                if (INSTANCE == null) {
                    INSTANCE = new VolleyTool(context);
                }
            }
        }
        return INSTANCE;
    }

    private RequestQueue requestQueue;

    private VolleyTool(Context context) {
        int maxDiskCacheBytes = 541065216;//516 * 1024 * 1024
        requestQueue = Volley.newRequestQueue(context, maxDiskCacheBytes);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
