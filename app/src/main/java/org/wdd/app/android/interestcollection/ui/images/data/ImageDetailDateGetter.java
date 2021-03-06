package org.wdd.app.android.interestcollection.ui.images.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wdd.app.android.interestcollection.database.manager.impl.ImageFavoriteDbManager;
import org.wdd.app.android.interestcollection.database.model.ImageFavorite;
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
    private ImageFavoriteDbManager mDbManager;
    private Handler mHandler;

    public ImageDetailDateGetter(Context context, DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mManager = HttpManager.getInstance(context);
        mDbManager = new ImageFavoriteDbManager(context);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void requestImageDetailData(String url, ActivityFragmentAvaliable host) {
        if (mSession != null) mSession.cancelRequest();
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
                if (contentNodes.size()> 0) {
                    Element contentNode = contentNodes.first();
                    Elements elements = contentNode.children();
                    detail.nodes = new ArrayList<>();
                    Element element;
                    Elements imgNodes;
                    for (int i = 0; i < elements.size(); i++) {
                        element = elements.get(i);
                        if("noscript".equalsIgnoreCase(element.tagName())) {
                            imgNodes = element.getElementsByTag("img");
                            if (imgNodes.size() == 0) continue;
                            detail.nodes.add(new ImageDetail.Node(true, imgNodes.first().attr("src")));
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

    public void queryImageCollectStatus(final String url, final ActivityFragmentAvaliable host) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final ImageFavorite favorite = mDbManager.getFavoriteByUrl(url);
                if (!host.isAvaliable()) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback == null) return;
                        mCallback.onFavoriteQueried(favorite);
                    }
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void deleteFavoriteById(final int id, final ActivityFragmentAvaliable host) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final int affectedRows = mDbManager.deleteById(id);
                if (!host.isAvaliable()) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback == null) return;
                        mCallback.onFavoriteUncollected(affectedRows > 0);
                    }
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void insertFavorite(String title, String time, String url, String imgUrl, int gifFlag, final ActivityFragmentAvaliable host) {
        final ImageFavorite favorite = new ImageFavorite();
        favorite.title = title;
        favorite.time = time;
        favorite.url = url;
        favorite.imgUrl = imgUrl;
        favorite.gifFlag = gifFlag;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                favorite.id = (int) mDbManager.insert(favorite);
                if (!host.isAvaliable()) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback == null) return;
                        mCallback.onFavoriteCollected(favorite.id != -1, favorite);
                    }
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public interface DataCallback {

        void onRequestOk(ImageDetail detail);
        void onRequestError(String error);
        void onNetworkError();

        void onFavoriteQueried(ImageFavorite favorite);
        void onFavoriteCollected(boolean success, ImageFavorite favorite);
        void onFavoriteUncollected(boolean success);

    }
}
