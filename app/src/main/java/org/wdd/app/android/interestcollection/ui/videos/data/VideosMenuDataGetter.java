package org.wdd.app.android.interestcollection.ui.videos.data;

import android.content.Context;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.http.HttpConnectCallback;
import org.wdd.app.android.interestcollection.http.HttpManager;
import org.wdd.app.android.interestcollection.http.HttpRequestEntry;
import org.wdd.app.android.interestcollection.http.HttpResponseEntry;
import org.wdd.app.android.interestcollection.http.HttpSession;
import org.wdd.app.android.interestcollection.http.error.ErrorCode;
import org.wdd.app.android.interestcollection.http.error.HttpError;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.main.model.HtmlHref;
import org.wdd.app.android.interestcollection.utils.HttpUtils;
import org.wdd.app.android.interestcollection.utils.ServerApis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class VideosMenuDataGetter {

    private Context mContext;
    private HttpSession mSession;
    private HttpManager mManager;
    private DataCallback mCallback;

    public VideosMenuDataGetter(Context context, DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mManager = HttpManager.getInstance(context);
    }

    public void requestVideosMenuData(ActivityFragmentAvaliable host) {
        HttpRequestEntry requestEntry = new HttpRequestEntry();
        requestEntry.setMethod(HttpRequestEntry.Method.GET);
        requestEntry.addRequestHeader("User-Agent", ServerApis.USER_AGENT);
        requestEntry.setUrl(ServerApis.VIDEO_URL);
        mSession = mManager.sendHtmlRequest("GB2312", host, requestEntry, new HttpConnectCallback() {

            @Override
            public void onRequestOk(HttpResponseEntry res) {
                mSession = null;
                Document document = (Document) res.getData();
                List<HtmlHref> menus = null;
                Elements nodes = document.getElementsByAttributeValue("id", "nav");
                if (nodes.size() == 0) {
                    mCallback.onRequestError(mContext.getString(R.string.parse_error));
                    return;
                }
                Element node = nodes.first();
                nodes = node.getElementsByTag("a");
                if (nodes.size() <= 2) {
                    mCallback.onRequestError(mContext.getString(R.string.parse_error));
                    return;
                }
                menus = new ArrayList<>();
                HtmlHref href;
                String name;
                for (int i = 1; i < nodes.size() - 1; i++) {
                    node = nodes.get(i);
                    name = node.text();
                    href = new HtmlHref();
                    href.name = name;
                    href.url = node.attr("href");
                    menus.add(href);
                }
                mCallback.onRequestOk(menus);
            }

            @Override
            public void onRequestFailure(HttpError error) {
                mSession = null;
                if (error.getErrorCode() == ErrorCode.NO_CONNECTION_ERROR) {
                    mCallback.onNetworkError();
                } else {
                    mCallback.onRequestError(HttpUtils.getErrorDescFromErrorCode(mContext, error.getErrorCode()));
                }
            }
        });
    }

    public void cancelSession() {
        if (mSession == null) return;
        mSession.cancelRequest();
        mSession = null;
    }

    public interface DataCallback {

        void onRequestOk(List<HtmlHref> data);
        void onRequestError(String error);
        void onNetworkError();

    }
}
