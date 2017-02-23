package org.wdd.app.android.interestcollection.ui.jokes.data;

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
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJoke;
import org.wdd.app.android.interestcollection.ui.main.model.HtmlHref;
import org.wdd.app.android.interestcollection.utils.HttpUtils;
import org.wdd.app.android.interestcollection.utils.ServerApis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class DirtyJokesDataGetter {

    private Context mContext;
    private HttpSession mSession;
    private HttpManager mManager;
    private DataCallback mCallback;

    private int mPage = 1;

    public DirtyJokesDataGetter(Context context, DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mManager = HttpManager.getInstance(context);
    }

    public void requestDirtyJokesListData(final boolean isAppend, ActivityFragmentAvaliable host) {
        if (isAppend) {
            mPage++;
        } else {
            mPage = 1;
        }
        HttpRequestEntry requestEntry = new HttpRequestEntry();
        requestEntry.setMethod(HttpRequestEntry.Method.GET);
        requestEntry.setUrl(ServerApis.DIRTY_JOKE_URL + mPage);
        mSession = mManager.sendHtmlRequest(host, requestEntry, new HttpConnectCallback() {

            @Override
            public void onRequestOk(HttpResponseEntry res) {
                mSession = null;
                Document document = (Document) res.getData();
                Elements rootNode = document.getElementsByAttributeValue("class", "box_wrap ajz");
                if (rootNode.size() == 0) {
                    mCallback.onRequestOk(null, isAppend);
                    return;
                }
                Elements postNodes = rootNode.first().getElementsByAttributeValue("class", "post");
                if (postNodes.size() == 0) {
                    mCallback.onRequestOk(null, isAppend);
                    return;
                }
                List<DirtyJoke> jokes = new ArrayList<>();
                Element postNode;
                Element aNode;
                Element imgNode;
                Element spanNode;
                DirtyJoke joke;
                for (int i = 0; i < postNodes.size(); i++) {
                    postNode = postNodes.get(i);
                    joke = new DirtyJoke();
                    aNode = postNode.getElementsByTag("a").first();
                    joke.url = aNode.attr("href");
                    imgNode = postNode.getElementsByTag("img").first();
                    joke.imgUrl = imgNode.attr("data-original");
                    joke.title = imgNode.attr("alt");
                    spanNode = postNode.getElementsByTag("span").first();
                    joke.date = spanNode.text();
                    jokes.add(joke);
                }
                mCallback.onRequestOk(jokes, isAppend);
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

        void onRequestOk(List<DirtyJoke> data, boolean isAppend);
        void onRequestError(String error, boolean isAppend);
        void onNetworkError(boolean isAppend);

    }
}
