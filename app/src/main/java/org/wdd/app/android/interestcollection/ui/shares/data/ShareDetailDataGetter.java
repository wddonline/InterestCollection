package org.wdd.app.android.interestcollection.ui.shares.data;

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
import org.wdd.app.android.interestcollection.ui.shares.model.ShareDetail;
import org.wdd.app.android.interestcollection.utils.HttpUtils;
import org.wdd.app.android.interestcollection.utils.ServerApis;

import java.util.ArrayList;

/**
 * Created by richard on 2/23/17.
 */

public class ShareDetailDataGetter {

    private Context mContext;
    private HttpSession mSession;
    private HttpManager mManager;
    private DataCallback mCallback;

    public ShareDetailDataGetter(Context context, DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mManager = HttpManager.getInstance(context);
    }

    public void requestShareDetailData(String url, ActivityFragmentAvaliable host) {
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
                ShareDetail detail = new ShareDetail();
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
                Elements elements = contentNode.getAllElements();
                detail.nodes = new ArrayList<>();
                Element element;
                for (int i = 0; i < elements.size(); i++) {
                    element = elements.get(i);
                    if(element.hasAttr("data-original")) {
                        detail.nodes.add(new ShareDetail.Node(true, element.attr("data-original")));
                    } else if("p".equalsIgnoreCase(element.tagName())) {
                        if (element.hasClass("source")) {
                            detail.source = element.text();
                            break;
                        }
                        String p = element.text();
                        if (p.length() == 0) continue;
                        detail.nodes.add(new ShareDetail.Node(false, p));
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

        void onRequestOk(ShareDetail detail);
        void onRequestError(String error);
        void onNetworkError();

    }
}
