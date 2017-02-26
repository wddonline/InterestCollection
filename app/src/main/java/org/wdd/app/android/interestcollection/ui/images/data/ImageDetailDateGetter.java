package org.wdd.app.android.interestcollection.ui.images.data;

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
import org.wdd.app.android.interestcollection.ui.images.model.ImageDetail;
import org.wdd.app.android.interestcollection.utils.HttpUtils;
import org.wdd.app.android.interestcollection.utils.ServerApis;

import java.util.ArrayList;

/**
 * Created by richard on 2/24/17.
 */

public class ImageDetailDateGetter {

    private Context mContext;
    private HttpSession mSession;
    private HttpManager mManager;
    private DataCallback mCallback;

    public ImageDetailDateGetter(Context context, DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mManager = HttpManager.getInstance(context);
    }

    public void requestImageDetailData(String url, ActivityFragmentAvaliable host) {
        HttpRequestEntry requestEntry = new HttpRequestEntry();
        requestEntry.addRequestHeader("User-Agent", ServerApis.USER_AGENT);
        requestEntry.setMethod(HttpRequestEntry.Method.GET);
        requestEntry.setUrl(url);
        mSession = mManager.sendHtmlRequest(host, requestEntry, new HttpConnectCallback() {

            @Override
            public void onRequestOk(HttpResponseEntry res) {
                mSession = null;
                Document document = (Document) res.getData();
                Elements articleNodes = document.getElementsByAttributeValue("id", "content-c");
                if (articleNodes.size() == 0) {
                    mCallback.onRequestOk(null);
                    return;
                }
                Element articleNode = articleNodes.first();
                ImageDetail detail = new ImageDetail();
                detail.title = articleNode.getElementsByAttributeValue("class", "post-title").first().text();

                Elements postMetaNodes = articleNode.getElementsByAttributeValue("class", "post-meta");
                detail.time = postMetaNodes.get(0).text();
                detail.tag = postMetaNodes.get(1).text();
                detail.commentCount = postMetaNodes.get(2).text();

                Elements contentNodes = articleNode.getElementsByAttributeValue("class", "single-post-content");
                if (contentNodes.size() == 0) {
                    mCallback.onRequestOk(null);
                    return;
                }
                Element contentNode = contentNodes.first();
                Elements summaryNodes = contentNode.getElementsByAttributeValue("class", "summary");
                if (summaryNodes.size() > 0) {
                    detail.summary = summaryNodes.first().text();
                }
                Elements elements = contentNode.getAllElements();
                detail.nodes = new ArrayList<>();
                Element element;
                for (int i = 0; i < elements.size(); i++) {
                    element = elements.get(i);
                    if(element.hasAttr("data-original")) {
                        detail.nodes.add(new ImageDetail.Node(true, element.attr("data-original")));
                    } else if("p".equalsIgnoreCase(element.tagName())) {
                        if (element.hasClass("source")) {
                            detail.source = element.text();
                            break;
                        }
                        String p = element.text();
                        if (p.length() == 0) continue;
                        detail.nodes.add(new ImageDetail.Node(false, p));
                    } else if (element.hasClass("summary")) {
                        detail.summary = element.text();
                    }
                }

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

        void onRequestOk(ImageDetail detail);
        void onRequestError(String error);
        void onNetworkError();

    }
}
