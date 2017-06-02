package org.wdd.app.android.interestcollection.ui.videos.data;

import android.content.Context;
import android.text.TextUtils;

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
import org.wdd.app.android.interestcollection.ui.videos.model.Video;
import org.wdd.app.android.interestcollection.utils.HttpUtils;
import org.wdd.app.android.interestcollection.utils.ServerApis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class VideosDataGetter {

    private Context mContext;
    private HttpSession mSession;
    private HttpManager mManager;
    private DataCallback mCallback;

    private int mPage = 1;
    private int mPageNum = 1;
    private String mUrlPrefix = null;

    public VideosDataGetter(Context context, DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mManager = HttpManager.getInstance(context);
    }

    public void requestVideosListData(String url, final boolean isAppend, ActivityFragmentAvaliable host) {
        String realUrl;
        if (isAppend) {
            realUrl = ServerApis.VIDEO_URL + url + "/" + mUrlPrefix + mPage + ".html";
        } else {
            realUrl = ServerApis.VIDEO_URL + url;
        }
        HttpRequestEntry requestEntry = new HttpRequestEntry();
        requestEntry.setMethod(HttpRequestEntry.Method.GET);
        requestEntry.addRequestHeader("User-Agent", ServerApis.USER_AGENT);
        requestEntry.setUrl(realUrl);
        requestEntry.setShouldCached(false);
        mSession = mManager.sendHtmlRequest(host, requestEntry, new HttpConnectCallback() {

            @Override
            public void onRequestOk(HttpResponseEntry res) {
                mSession = null;
                List<Video> videos;
                Document document = (Document) res.getData();
                Elements nodes = document.getElementsByAttributeValue("class", "item_list");
                if (nodes.size() == 0) {
                    mCallback.onRequestError(mContext.getString(R.string.parse_error), isAppend);
                    return;
                }
                nodes = nodes.first().getElementsByTag("a");
                if (nodes.size() == 0) {
                    mCallback.onRequestError(mContext.getString(R.string.parse_error), isAppend);
                    return;
                }
                videos = new ArrayList<>();
                Element node;
                Element imgNode;
                Element spanNode;
                Video video;
                for (int i = 0; i < nodes.size(); i++) {
                    node = nodes.get(i);
                    video = new Video();
                    video.url = node.attr("href");
                    imgNode = node.getElementsByTag("img").first();
                    video.title = imgNode.attr("alt");
                    video.imgUrl = ServerApis.VIDEO_URL + imgNode.attr("src");
                    spanNode = node.getElementsByTag("span").first();
                    video.date = spanNode.text();
                    videos.add(video);
                }

                if (videos.size() > 0) {
                    mPage++;
                }
                boolean isLastPage = true;
                if (mPageNum == -1 || TextUtils.isEmpty(mUrlPrefix)) {
                    nodes = document.getElementsByTag("select");
                    if (nodes.size() > 0) {
                        nodes = nodes.first().getElementsByTag("option");
                        if (nodes.size() > 0) {
                            node = nodes.last();
                            mPageNum = Integer.parseInt(node.text());
                            String value = node.attr("value");
                            mUrlPrefix = value.substring(0, value.lastIndexOf('_') + 1);
                            isLastPage = mPage == mPageNum;
                        }
                    }
                } else {
                    isLastPage = mPage == mPageNum;
                }

                mCallback.onRequestOk(videos, isAppend, isLastPage);
            }

            @Override
            public void onRequestFailure(HttpError error) {
                mSession = null;
                if (error.getErrorCode() == ErrorCode.NO_CONNECTION_ERROR) {
                    mCallback.onNetworkError(isAppend);
                } else {
                    mCallback.onRequestError(HttpUtils.getErrorDescFromErrorCode(mContext, error.getErrorCode()), isAppend);
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

        void onRequestOk(List<Video> data, boolean isAppend, boolean isLastPage);
        void onRequestError(String error, boolean isAppend);
        void onNetworkError(boolean isAppend);

    }
}
