package org.wdd.app.android.interestcollection.ui.videos.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.database.manager.impl.VideoFavoriteDbManager;
import org.wdd.app.android.interestcollection.database.model.VideoFavorite;
import org.wdd.app.android.interestcollection.http.HttpConnectCallback;
import org.wdd.app.android.interestcollection.http.HttpManager;
import org.wdd.app.android.interestcollection.http.HttpRequestEntry;
import org.wdd.app.android.interestcollection.http.HttpResponseEntry;
import org.wdd.app.android.interestcollection.http.HttpSession;
import org.wdd.app.android.interestcollection.http.error.ErrorCode;
import org.wdd.app.android.interestcollection.http.error.HttpError;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.videos.model.VideoDetail;
import org.wdd.app.android.interestcollection.utils.HttpUtils;
import org.wdd.app.android.interestcollection.utils.ServerApis;

/**
 * Created by richard on 2/23/17.
 */

public class VideosDetailDataGetter {

    private Context mContext;
    private HttpSession mSession;
    private HttpManager mManager;
    private DataCallback mCallback;
    private VideoFavoriteDbManager mDbManager;
    private Handler mHandler;

    public VideosDetailDataGetter(Context context, DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mManager = HttpManager.getInstance(context);
        mDbManager = new VideoFavoriteDbManager(context);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void requestVideoDetailData(String url, ActivityFragmentAvaliable host) {
        if (mSession != null) mSession.cancelRequest();
        HttpRequestEntry requestEntry = new HttpRequestEntry();
        requestEntry.setMethod(HttpRequestEntry.Method.GET);
        requestEntry.addRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        requestEntry.setUrl("http://www.yiledao.com/" + url);
        mSession = mManager.sendHtmlRequest("GB2312", host, requestEntry, new HttpConnectCallback() {

            @Override
            public void onRequestOk(HttpResponseEntry res) {
                mSession = null;
                VideoDetail detail = new VideoDetail();
                Document document = (Document) res.getData();
                Elements nodes = document.getElementsByAttributeValue("id", "area-title-view");
                Element node;
                if (nodes.size() > 0) {
                    node = nodes.first();
                    detail.title = node.getElementsByTag("h1").first().text();
                    detail.time = node.getElementsByAttributeValue("class", "date").first().text();
                    Elements aNodes = node.getElementsByTag("a");
                    if (aNodes.size() > 0) {
                        detail.tag = aNodes.get(0).text();
                    }
                    if (aNodes.size() > 2) {
                        detail.source = aNodes.get(2).text();
                    }
                }
                nodes = document.getElementsByAttributeValue("class", "content");
                if (nodes.size() > 0) {
                    nodes = nodes.first().getElementsByTag("embed");
                    if (nodes.size() > 0) {
                        String params = nodes.first().attr("flashvars");
                        String[] pieces = params.split("&");
                        for (String piece : pieces) {
                            if (piece.startsWith("VideoIDS")) {
                                detail.vid = piece.split("=")[1];
                                break;
                            }
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

    public void queryVideoCollectStatus(final String url, final ActivityFragmentAvaliable host) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final VideoFavorite favorite = mDbManager.getFavoriteByUrl(url);
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

    public void insertFavorite(String title, String time, String url, String imgUrl, final ActivityFragmentAvaliable host) {
        final VideoFavorite favorite = new VideoFavorite();
        favorite.title = title;
        favorite.time = time;
        favorite.url = url;
        favorite.imgUrl = imgUrl;
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

        void onRequestOk(VideoDetail data);
        void onRequestError(String error);
        void onNetworkError();

        void onFavoriteQueried(VideoFavorite favorite);
        void onFavoriteCollected(boolean success, VideoFavorite favorite);
        void onFavoriteUncollected(boolean success);

    }
}
