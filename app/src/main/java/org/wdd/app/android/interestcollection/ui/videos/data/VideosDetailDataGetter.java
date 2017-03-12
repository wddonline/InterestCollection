package org.wdd.app.android.interestcollection.ui.videos.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
        HttpRequestEntry requestEntry = new HttpRequestEntry();
        requestEntry.setMethod(HttpRequestEntry.Method.GET);
        requestEntry.addRequestHeader("User-Agent", ServerApis.USER_AGENT);
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
//                Element rootNode = rootNodes.first();
//                VideoDetail detail = new VideoDetail();
//                detail.title = rootNode.getElementsByAttributeValue("class", "post-title").first().text();
//
//                Elements postMetaNodes = rootNode.getElementsByAttributeValue("class", "post-meta");
//                detail.time = postMetaNodes.get(0).text();
//                detail.tag = postMetaNodes.get(1).text();
//                detail.commentCount = postMetaNodes.get(2).text();
//
//                Elements contentNodes = rootNode.getElementsByAttributeValue("class", "single-post-content");
//                if (contentNodes.size() == 0) {
//                    mCallback.onRequestOk(null);
//                    return;
//                }
//                Element contentNode = contentNodes.first();
//                detail.videoUrl = contentNode.getElementsByTag("iframe").first().attr("src");
//                detail.source = contentNode.getElementsByAttributeValue("class", "source").first().text();

                VideoDetail detail = new VideoDetail();
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
