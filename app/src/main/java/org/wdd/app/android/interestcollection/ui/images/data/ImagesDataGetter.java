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
import org.wdd.app.android.interestcollection.ui.images.model.Image;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJoke;
import org.wdd.app.android.interestcollection.utils.HttpUtils;
import org.wdd.app.android.interestcollection.utils.ServerApis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 2/23/17.
 */

public class ImagesDataGetter {

    private Context mContext;
    private HttpSession mSession;
    private HttpManager mManager;
    private DataCallback mCallback;

    private int mPage = 1;

    public ImagesDataGetter(Context context, DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mManager = HttpManager.getInstance(context);
    }

    public void requestImagesListData(final boolean isAppend, ActivityFragmentAvaliable host) {
        if (isAppend) {
            mPage++;
        } else {
            mPage = 1;
        }
        HttpRequestEntry requestEntry = new HttpRequestEntry();
        requestEntry.setMethod(HttpRequestEntry.Method.GET);
        requestEntry.addRequestHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0.1; SAMSUNG SM-J7108 Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36");
        requestEntry.setUrl(ServerApis.IMAGE_URL + mPage);
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
                List<Image> images = new ArrayList<>();
                Element postNode;
                Image image;
                for (int i = 0; i < postNodes.size(); i++) {
                    postNode = postNodes.get(i);
                    image = new Image();

                    images.add(image);
                }
                mCallback.onRequestOk(images, isAppend);
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

        void onRequestOk(List<Image> data, boolean isAppend);
        void onRequestError(String error, boolean isAppend);
        void onNetworkError(boolean isAppend);

    }
}
