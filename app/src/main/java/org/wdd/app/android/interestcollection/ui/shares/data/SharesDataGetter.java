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
import org.wdd.app.android.interestcollection.ui.shares.model.Share;
import org.wdd.app.android.interestcollection.utils.HttpUtils;
import org.wdd.app.android.interestcollection.utils.ServerApis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class SharesDataGetter {

    private Context mContext;
    private HttpSession mSession;
    private HttpManager mManager;
    private DataCallback mCallback;

    private int mPage = 1;

    public SharesDataGetter(Context context, DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mManager = HttpManager.getInstance(context);
    }

    public void requestSharesListData(final boolean isAppend, ActivityFragmentAvaliable host) {
        if (isAppend) {
            mPage++;
        } else {
            mPage = 1;
        }
        HttpRequestEntry requestEntry = new HttpRequestEntry();
        requestEntry.addRequestHeader("User-Agent", ServerApis.USER_AGENT);
        requestEntry.setMethod(HttpRequestEntry.Method.GET);
        requestEntry.setUrl(ServerApis.SHARE_URL + mPage);
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
                List<Share> audios = new ArrayList<>();
                Element articleNode;
                Element aNode;
                Element imgNode;
                Element titleNode;
                Share share;
                for (int i = 0; i < articleNodes.size(); i++) {
                    articleNode = articleNodes.get(i);
                    share = new Share();
                    aNode = articleNode.getElementsByTag("a").first();
                    share.url = aNode.attr("href");
                    imgNode = articleNode.getElementsByTag("img").first();
                    share.imgUrl = imgNode.attr("src");
                    share.title = imgNode.attr("alt");
                    titleNode = articleNode.getElementsByTag("time").first();
                    share.date = titleNode.text();
                    audios.add(share);
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
                mCallback.onRequestOk(audios, isAppend, isLastPage);
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

        void onRequestOk(List<Share> data, boolean isAppend, boolean isLastPage);
        void onRequestError(String error, boolean isAppend);
        void onNetworkError(boolean isAppend);

    }

    public interface OnItemClickedListener {

        void onItemClicked(int position, Share item);

    }
}
