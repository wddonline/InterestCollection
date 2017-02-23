package org.wdd.app.android.interestcollection.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdd on 16-11-26.
 */

public class HttpResponseEntry {

    private Map<String, String> headers;

    private int statusCode;
    private Object data;

    public HttpResponseEntry() {
        headers = new HashMap<>();
    }

    public String getResponseHeader(String headerKey) {
        return headers.get(headerKey);
    }

    public Map<String, String> getResponseHeaders(Map headers) {
        return headers;
    }

    public void addResponseHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public void addResponseHeader(String headerKey, String headerVal) {
        this.headers.put(headerKey, headerVal);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
