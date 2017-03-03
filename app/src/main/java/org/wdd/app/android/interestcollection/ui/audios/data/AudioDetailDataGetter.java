package org.wdd.app.android.interestcollection.ui.audios.data;

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
import org.wdd.app.android.interestcollection.ui.audios.model.Audio;
import org.wdd.app.android.interestcollection.ui.audios.model.AudioDetail;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.videos.model.VideoDetail;
import org.wdd.app.android.interestcollection.utils.HttpUtils;
import org.wdd.app.android.interestcollection.utils.ServerApis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class AudioDetailDataGetter {

    private Context mContext;
    private HttpSession mSession;
    private HttpManager mManager;
    private DataCallback mCallback;

    public AudioDetailDataGetter(Context context, DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mManager = HttpManager.getInstance(context);
    }

    public void requestAudioDetailData(String url, ActivityFragmentAvaliable host) {
        HttpRequestEntry requestEntry = new HttpRequestEntry();
        requestEntry.addRequestHeader("User-Agent", ServerApis.USER_AGENT);
        requestEntry.setMethod(HttpRequestEntry.Method.GET);
        requestEntry.setUrl(url);
        mSession = mManager.sendHtmlRequest(host, requestEntry, new HttpConnectCallback() {

            @Override
            public void onRequestOk(HttpResponseEntry res) {
                mSession = null;
                Document document = (Document) res.getData();
                Elements rootNodes = document.getElementsByAttributeValue("id", "content-c");
                if (rootNodes.size() == 0) {
                    mCallback.onRequestOk(null);
                    return;
                }
                Element rootNode = rootNodes.first();
                AudioDetail detail = new AudioDetail();
                detail.title = rootNode.getElementsByAttributeValue("class", "post-title").first().text();

                Elements postMetaNodes = rootNode.getElementsByAttributeValue("class", "post-meta");
                detail.time = postMetaNodes.get(0).text();
                detail.tag = postMetaNodes.get(1).text();
                detail.commentCount = postMetaNodes.get(2).text();

                Elements contentNodes = rootNode.getElementsByAttributeValue("class", "single-post-content");
                if (contentNodes.size() == 0) {
                    mCallback.onRequestOk(null);
                    return;
                }
                Element contentNode = contentNodes.first();
                String pHtml = contentNode.getElementsByTag("p").first().html();
                try {
                    detail.anchor = pHtml.substring(pHtml.indexOf("：") + 1, pHtml.indexOf("<span>"));
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                try {
                    detail.column = pHtml.substring(pHtml.lastIndexOf("：") + 1, pHtml.indexOf("</span>"));
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                Elements audioNodes = contentNode.getElementsByTag("audio");
                if (audioNodes.size() > 0) {
                    detail.audioUrl = audioNodes.first().attr("src");
                } else {
                    document.getElementsByTag("script").remove();
                    document.getElementsByAttributeValue("id", "sidemenu-container").remove();
                    document.getElementsByTag("header").remove();
                    document.getElementsByAttributeValue("role", "search").remove();
                    Elements articles = document.getElementsByTag("article");
                    Elements divs = articles.first().getElementsByTag("div");
                    if (divs.size() > 0) {
                        divs = divs.last().getElementsByTag("div");
                        if (divs.size() > 0) {
                            divs.last().remove();
                        }
                    }
                    document.getElementsByAttributeValue("id", "pageGo").remove();
                    document.getElementsByClass("post-xg clear").remove();
                    document.getElementsByTag("footer").remove();
                    document.getElementsByClass("comments-area").first().remove();
                    document.getElementsByAttributeValue("id", "btn_top").first().remove();
                    detail.html = document.html();
                }
                detail.source = contentNode.getElementsByAttributeValue("class", "source").first().text();


                mCallback.onRequestOk(detail);
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

        void onRequestOk(AudioDetail detail);
        void onRequestError(String error);
        void onNetworkError();

    }
}
