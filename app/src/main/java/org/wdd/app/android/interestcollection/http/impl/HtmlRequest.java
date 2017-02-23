package org.wdd.app.android.interestcollection.http.impl;

import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

/**
 * Created by richard on 2/10/17.
 */

public class HtmlRequest extends StringRequest {

    private String mEncode;

    public HtmlRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public HtmlRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public void setEncode(String encode) {
        this.mEncode = encode;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            if (TextUtils.isEmpty(mEncode)) {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } else {
                parsed = new String(response.data, mEncode);
            }
        } catch (UnsupportedEncodingException var4) {
            parsed = new String(response.data);
        }

        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
