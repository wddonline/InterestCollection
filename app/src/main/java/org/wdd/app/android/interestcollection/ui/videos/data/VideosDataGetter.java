package org.wdd.app.android.interestcollection.ui.videos.data;

import android.content.Context;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    public VideosDataGetter(Context context, DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mManager = HttpManager.getInstance(context);
    }

    public void requestVideosListData(final boolean isAppend, ActivityFragmentAvaliable host) {
        if (isAppend) {
            mPage++;
        } else {
            mPage = 1;
        }
        HttpRequestEntry requestEntry = new HttpRequestEntry();
        requestEntry.setMethod(HttpRequestEntry.Method.GET);
        requestEntry.addRequestHeader("User-Agent", ServerApis.USER_AGENT);
        requestEntry.setUrl(ServerApis.VIDEO_URL + mPage);
        requestEntry.setShouldCached(false);
        mSession = mManager.sendHtmlRequest(host, requestEntry, new HttpConnectCallback() {

            @Override
            public void onRequestOk(HttpResponseEntry res) {
                mSession = null;
                Document document = (Document) res.getData();
                Elements rootNode = document.getElementsByAttributeValue("id", "content-c");
                if (rootNode.size() == 0) {
                    mCallback.onRequestOk(null, isAppend, true);
                    return;
                }
                Elements articleNodes = rootNode.first().getElementsByTag("article");
                if (articleNodes.size() == 0) {
                    mCallback.onRequestOk(null, isAppend, true);
                    return;
                }
                List<Video> videos = new ArrayList<>();
                Element articleNode;
                Element aNode;
                Element imgNode;
                Element titleNode;
                Video video;
                for (int i = 0; i < articleNodes.size(); i++) {
                    articleNode = articleNodes.get(i);
                    video = new Video();
                    aNode = articleNode.getElementsByTag("a").first();
                    video.url = aNode.attr("href");
                    imgNode = articleNode.getElementsByTag("img").first();
                    video.imgUrl = imgNode.attr("src");
                    video.title = imgNode.attr("alt");
                    titleNode = articleNode.getElementsByTag("time").first();
                    video.date = titleNode.text();
                    videos.add(video);
                }

                boolean isLastPage;
                Elements pageNodes = rootNode.first().getElementsByAttributeValue("class", "pagenavi");
                if (pageNodes.size() == 0) {
                    isLastPage = true;
                } else {
                    Elements aNodes = pageNodes.first().getElementsByTag("a");
                    if (aNodes.size() == 0) {
                        isLastPage = true;
                    } else {
                        String navText = aNodes.last().text();
                        if ("下一页".equals(navText)) {
                            isLastPage = false;
                        } else {
                            isLastPage = true;
                        }
                    }
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
