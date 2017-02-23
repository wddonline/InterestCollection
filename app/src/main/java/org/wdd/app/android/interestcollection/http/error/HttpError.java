package org.wdd.app.android.interestcollection.http.error;

/**
 * Created by wangdd on 16-11-26.
 */

public class HttpError extends Throwable {

    private int errorCode;
    private String errorMsg;
    private int statusCode = -1;

    public HttpError() {
    }

    public HttpError(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public HttpError(int errorCode, String errorMsg, int statusCode) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.statusCode = statusCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
