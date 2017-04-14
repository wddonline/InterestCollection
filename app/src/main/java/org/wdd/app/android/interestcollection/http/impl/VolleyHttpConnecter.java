package org.wdd.app.android.interestcollection.http.impl;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.wdd.app.android.interestcollection.http.HttpConnectCallback;
import org.wdd.app.android.interestcollection.http.HttpConnecter;
import org.wdd.app.android.interestcollection.http.HttpRequestEntry;
import org.wdd.app.android.interestcollection.http.HttpResponseEntry;
import org.wdd.app.android.interestcollection.http.HttpSession;
import org.wdd.app.android.interestcollection.http.StatusCode;
import org.wdd.app.android.interestcollection.http.error.ErrorCode;
import org.wdd.app.android.interestcollection.http.error.HttpError;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangdd on 16-11-26.
 */

public class VolleyHttpConnecter implements HttpConnecter {

    private Context context;
    private List<HttpSession> sessionList;
    private RequestQueue requestQueue;

    public VolleyHttpConnecter(Context context) {
        this.context = context;
        sessionList = Collections.synchronizedList(new ArrayList<HttpSession>());

        requestQueue = VolleyTool.getInstance(context).getRequestQueue();
        requestQueue.start();
    }

    @Override
    public HttpSession sendHttpRequest(final ActivityFragmentAvaliable host, final HttpRequestEntry requestEntry, final Class clazz, final HttpConnectCallback callback) {
        int method = Request.Method.POST;
        if (requestEntry.getMethod() == HttpRequestEntry.Method.GET) {
            method = Request.Method.GET;
            requestEntry.setUrl(generateGetUrl(requestEntry.getUrl(), requestEntry.getRequestParams()));
        }
        StringRequest request = new StringRequest(method, requestEntry.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String txt) {
                if (!host.isAvaliable()) return;
                handleResponse(txt, clazz, callback);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                if (!host.isAvaliable()) return;
                handleError(err, callback);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return requestEntry.getRequestParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return requestEntry.getRequestHeaders();
            }
        };
        request.setShouldCache(requestEntry.shouldCache());
        request.setRetryPolicy(new DefaultRetryPolicy(requestEntry.getTimeOut(),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
        HttpSession session = new VolleyHttpSession(request, requestEntry);
        sessionList.add(session);
        return session;
    }

    @Override
    public HttpSession sendHttpRequest(final ActivityFragmentAvaliable host, final HttpRequestEntry requestEntry, final HttpConnectCallback callback) {
        int method = Request.Method.POST;
        if (requestEntry.getMethod() == HttpRequestEntry.Method.GET) {
            method = Request.Method.GET;
            requestEntry.setUrl(generateGetUrl(requestEntry.getUrl(), requestEntry.getRequestParams()));
        }
        StringRequest request = new StringRequest(method, requestEntry.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String txt) {
                if (!host.isAvaliable()) return;
                handleResponse(txt, callback);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                if (!host.isAvaliable()) return;
                handleError(err, callback);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return requestEntry.getRequestParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return requestEntry.getRequestHeaders();
            }
        };
        request.setShouldCache(requestEntry.shouldCache());
        request.setRetryPolicy(new DefaultRetryPolicy(requestEntry.getTimeOut(),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
        HttpSession session = new VolleyHttpSession(request, requestEntry);
        sessionList.add(session);
        return session;
    }

    @Override
    public HttpSession sendHtmlRequest(ActivityFragmentAvaliable host, HttpRequestEntry requestEntry, HttpConnectCallback callback) {
        return sendHtmlRequest(null, host, requestEntry, callback);
    }

    @Override
    public HttpSession sendHtmlRequest(String encode, final ActivityFragmentAvaliable host, final HttpRequestEntry requestEntry, final HttpConnectCallback callback) {
        int method = Request.Method.POST;
        if (requestEntry.getMethod() == HttpRequestEntry.Method.GET) {
            method = Request.Method.GET;
            requestEntry.setUrl(generateGetUrl(requestEntry.getUrl(), requestEntry.getRequestParams()));
        }
        HtmlRequest request = new HtmlRequest(method, requestEntry.getUrl(), new Response.Listener<String>() {

            @Override
            public void onResponse(String txt) {
                if (!host.isAvaliable()) return;
                handleHtmlResponse(txt, callback);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                if (!host.isAvaliable()) return;
                handleError(err, callback);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return requestEntry.getRequestParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return requestEntry.getRequestHeaders();
            }
        };
        request.setShouldCache(requestEntry.shouldCache());
        request.setEncode(encode);
        request.setRetryPolicy(new DefaultRetryPolicy(requestEntry.getTimeOut(),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
        HttpSession session = new VolleyHttpSession(request, requestEntry);
        sessionList.add(session);
        return session;
    }

    private void handleError(VolleyError err, HttpConnectCallback callback) {
        HttpError error;
        if (err instanceof AuthFailureError) {
            error= new HttpError(ErrorCode.AUTH_FAILURE_ERROR, err.getMessage());
        } else if(err instanceof NoConnectionError) {
            error= new HttpError(ErrorCode.NO_CONNECTION_ERROR, err.getMessage());
        } else if(err instanceof NetworkError) {
            error= new HttpError(ErrorCode.NETWORK_ERROR, err.getMessage());
        } else if(err instanceof ParseError) {
            error= new HttpError(ErrorCode.PARSE_ERROR, err.getMessage());
        } else if(err instanceof ServerError) {
            if (err.networkResponse == null) {
                error = new HttpError(ErrorCode.SERVER_ERROR, err.getMessage());
            } else {
                error = new HttpError(ErrorCode.SERVER_ERROR, err.getMessage(), err.networkResponse.statusCode);
            }
        } else if(err instanceof TimeoutError) {
            error= new HttpError(ErrorCode.TIMEOUT_ERROR, err.getMessage());
        } else {
            error= new HttpError(ErrorCode.UNKNOW_ERROR, err.getMessage());
        }
        callback.onRequestFailure(error);
    }

    private void handleResponse(String txt, Class clazz, HttpConnectCallback callback) {
        LogUtils.e(txt);
        JSONObject json = JSONObject.parseObject(txt);
        int status = json.getInteger("status");

        if (status == 1) {//请求成功

            Object segment = json.get("post");
            Object data;
            if (segment instanceof JSONArray) {//是json数组
                JSONArray array = (JSONArray) segment;
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    list.add(JSONObject.parseObject(array.getString(i), clazz));
                }
                data = list;
            } else {//是json对象
                data = JSON.parseObject(segment.toString(), clazz);
            }

            HttpResponseEntry responseEntry = new HttpResponseEntry();
            responseEntry.setStatusCode(StatusCode.HTTP_OK);
            responseEntry.setData(data);
            callback.onRequestOk(responseEntry);

        } else {//请求失败
            HttpError error = new HttpError(ErrorCode.SERVER_ERROR, json.getString("msg"));
            callback.onRequestFailure(error);
        }
    }

    private void handleResponse(String txt, HttpConnectCallback callback) {
        JSONObject json = JSONObject.parseObject(txt);
        int status = json.getInteger("status");

        if (status == 1) {//请求成功
            String data = json.getString("post");
            HttpResponseEntry responseEntry = new HttpResponseEntry();
            responseEntry.setStatusCode(StatusCode.HTTP_OK);
            responseEntry.setData(data);
            callback.onRequestOk(responseEntry);

        } else {//请求失败
            HttpError error = new HttpError(ErrorCode.SERVER_ERROR, json.getString("msg"));
            callback.onRequestFailure(error);
        }
    }

    private void handleHtmlResponse(String txt, HttpConnectCallback callback) {
        if (!TextUtils.isEmpty(txt)) {
            HttpResponseEntry responseEntry = new HttpResponseEntry();
            responseEntry.setStatusCode(StatusCode.HTTP_OK);
            Document document = Jsoup.parse(txt);
            responseEntry.setData(document);
            callback.onRequestOk(responseEntry);
        } else {
            HttpError error = new HttpError(ErrorCode.PARSE_ERROR, "response post is empty");
            callback.onRequestFailure(error);
        }
    }

    private String generateGetUrl(String url, Map<String, String> params) {
        StringBuffer urlBuff = new StringBuffer(url);
        urlBuff.append("?");
        Set<String> keys = params.keySet();
        Iterator<String> it = keys.iterator();
        String key;
        String value;
        while (it.hasNext()) {
            key = it.next();
            value = params.get(key);
            urlBuff.append(key + "=" + value + "&");
        }
        return urlBuff.subSequence(0, urlBuff.length() - 1).toString();
    }

    @Override
    public void stopAllSession() {
        requestQueue.stop();
        for (HttpSession session : sessionList) {
            session.cancelRequest();
        }
        sessionList.clear();
    }

    @Override
    public void stopSessionByTag(String tag) {
        Iterator<HttpSession> it = sessionList.iterator();
        while (it.hasNext()) {
            HttpSession session = it.next();
            if (session.getRequestEntry().getTag().equals(tag)) {
                session.cancelRequest();
                it.remove();
            }
        }
    }
}