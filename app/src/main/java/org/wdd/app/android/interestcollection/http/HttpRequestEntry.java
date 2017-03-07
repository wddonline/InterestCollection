package org.wdd.app.android.interestcollection.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdd on 16-11-26.
 */

public class HttpRequestEntry {

    private final int TIME_OUT = 15000;

    public enum Method {
        POST,
        GET
    }

    private Map<String, String> headers;
    private Map<String, String> params;
    private Method method = Method.POST;
    private String url;
    private String tag;
    private boolean shouldCached = true;

    private int timeOut = TIME_OUT;

    public HttpRequestEntry() {
        headers = new HashMap<>();
        params = new HashMap<>();

        headers.put("Content-Type", "application/x-www-form-urlencoded");
    }

    public void addRequestHeader(String headerKey, String headerVal) {
        headers.put(headerKey, headerVal);
    }

    public void addRequestHeaders(Map headers) {
        this.headers.putAll(headers);
    }

    public void addRequestParam(String paramKey, String paramVal) {
        params.put(paramKey, paramVal);
    }

    public void addRequestParams(Map params) {
        this.params.putAll(params);
    }

    public Map<String, String> getRequestHeaders() {
        return headers;
    }

    public Map<String, String> getRequestParams() {
        return params;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setShouldCached(boolean shouldCached) {
        this.shouldCached = shouldCached;
    }

    public boolean shouldCache() {
        return shouldCached;
    }

}
