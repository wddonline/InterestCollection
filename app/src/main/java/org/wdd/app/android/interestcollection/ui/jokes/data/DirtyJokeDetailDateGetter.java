package org.wdd.app.android.interestcollection.ui.jokes.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wdd.app.android.interestcollection.database.manager.impl.DirtyJokeFavoriteDbManager;
import org.wdd.app.android.interestcollection.database.model.DirtyJokeFavorite;
import org.wdd.app.android.interestcollection.http.HttpConnectCallback;
import org.wdd.app.android.interestcollection.http.HttpManager;
import org.wdd.app.android.interestcollection.http.HttpRequestEntry;
import org.wdd.app.android.interestcollection.http.HttpResponseEntry;
import org.wdd.app.android.interestcollection.http.HttpSession;
import org.wdd.app.android.interestcollection.http.error.ErrorCode;
import org.wdd.app.android.interestcollection.http.error.HttpError;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJokeDetail;
import org.wdd.app.android.interestcollection.utils.HttpUtils;
import org.wdd.app.android.interestcollection.utils.ServerApis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 2/24/17.
 */

public class DirtyJokeDetailDateGetter {

    private Context mContext;
    private HttpSession mSession;
    private HttpManager mManager;
    private DataCallback mCallback;
    private DirtyJokeFavoriteDbManager mDbManager;
    private Handler mHandler;

    public DirtyJokeDetailDateGetter(Context context,DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mManager = HttpManager.getInstance(context);
        mDbManager = new DirtyJokeFavoriteDbManager(context);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void requestDirtyJokeDetailData(String url, ActivityFragmentAvaliable host) {
        HttpRequestEntry requestEntry = new HttpRequestEntry();
        requestEntry.addRequestHeader("User-Agent", ServerApis.USER_AGENT);
        requestEntry.setMethod(HttpRequestEntry.Method.GET);
        requestEntry.setUrl(url);
        mSession = mManager.sendHtmlRequest(host, requestEntry, new HttpConnectCallback() {

            @Override
            public void onRequestOk(HttpResponseEntry res) {
                mSession = null;
                Document document = (Document) res.getData();
                Elements articleNodes = document.getElementsByTag("article");
                if (articleNodes.size() == 0) {
                    mCallback.onRequestOk(null);
                    return;
                }
                Element articleNode = articleNodes.first();
                DirtyJokeDetail detail = new DirtyJokeDetail();
                detail.title = articleNode.getElementsByAttributeValue("class", "post-title").first().text();

                Elements postMetaNodes = articleNode.getElementsByAttributeValue("class", "post-meta");
                detail.time = postMetaNodes.get(0).text();
                detail.tag = postMetaNodes.get(1).text();
                detail.commentCount = postMetaNodes.get(2).text();

                Elements contentNodes = articleNode.getElementsByAttributeValue("class", "single-post-content");
                if (contentNodes.size() > 0) {
                    Element contentNode = contentNodes.first();
                    Elements nodes = contentNode.children();
                    if (nodes.size() > 0) {
                        Element node;
                        DirtyJokeDetail.Post post;
                        Elements imgNodes;
                        String name;
                        List<DirtyJokeDetail.Post> list = new ArrayList<>();
                        for (int i = 0; i < nodes.size(); i++) {
                            node = nodes.get(i);
                            if (node.tagName().equals("h2")) {
                                post = new DirtyJokeDetail.Post();
                                post.type = DirtyJokeDetail.PostType.HEADER;
                                post.content = node.text();
                                list.add(post);
                            } else if (node.tagName().equals("p")) {
                                if ("source".equals(node.attr("class"))) {
                                    detail.source = node.text();
                                    continue;
                                }
                                name = node.text();
                                if (TextUtils.isEmpty(name)) continue;
                                post = new DirtyJokeDetail.Post();
                                post.type = DirtyJokeDetail.PostType.TEXT;
                                post.content = name;
                                list.add(post);
                            } else if (node.tagName().equals("noscript")) {
                                imgNodes = node.getElementsByTag("img");
                                if (imgNodes.size() == 0) continue;
                                post = new DirtyJokeDetail.Post();
                                post.type = DirtyJokeDetail.PostType.IMAGE;
                                post.content = imgNodes.first().attr("src");
                                list.add(post);
                            }
                        }
                        detail.post = list;
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

    public void queryGirlCollectStatus(final String url, final ActivityFragmentAvaliable host) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final
                DirtyJokeFavorite favorite = mDbManager.getFavoriteByUrl(url);
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
        final DirtyJokeFavorite favorite = new DirtyJokeFavorite();
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

        void onRequestOk(DirtyJokeDetail detail);
        void onRequestError(String error);
        void onNetworkError();

        void onFavoriteQueried(DirtyJokeFavorite favorite);
        void onFavoriteCollected(boolean success, DirtyJokeFavorite favorite);
        void onFavoriteUncollected(boolean success);

    }
}
