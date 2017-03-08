package org.wdd.app.android.interestcollection.ui.favorites.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.wdd.app.android.interestcollection.database.manager.impl.VideoFavoriteDbManager;
import org.wdd.app.android.interestcollection.database.model.VideoFavorite;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;

import java.util.List;

/**
 * Created by richard on 1/24/17.
 */

public class VideoFavoritesDataGetter {

    private Context mContext;
    private VideoFavoriteDbManager mDbManager;
    private ActivityFragmentAvaliable mHost;
    private DataCallback mCallback;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public VideoFavoritesDataGetter(Context context, DataCallback callback, ActivityFragmentAvaliable host) {
        this.mContext = context;
        this.mCallback = callback;
        this.mHost = host;
        mDbManager = new VideoFavoriteDbManager(context);
    }

    public void queryVideoFavorites() {
        Thread thread = new Thread(new QueryAction());
        thread.setDaemon(true);
        thread.start();
    }

    public void deleteSelectedFavorites(List<VideoFavorite> selectedItems) {
        Thread thread = new Thread(new DeleteItemsAction(selectedItems));
        thread.setDaemon(true);
        thread.start();
    }

    private class DeleteItemsAction implements Runnable {

        private List<VideoFavorite> favorites;

        public DeleteItemsAction(List<VideoFavorite> doctors) {
            this.favorites = doctors;
        }

        @Override
        public void run() {
            mDbManager.deleteFavorites(favorites);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!mHost.isAvaliable()) return;
                    mCallback.onDeleteSelectedData();
                }
            });
        }
    }

    private class QueryAction implements Runnable {

        @Override
        public void run() {
            final List<VideoFavorite> result = mDbManager.queryAll();
            if (result == null || result.size() == 0) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!mHost.isAvaliable()) return;
                        mCallback.onNoDataGetted();
                    }
                });
                return;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!mHost.isAvaliable()) return;
                    mCallback.onDataGetted(result);
                }
            });
        }
    }

    public interface DataCallback {

        void onDataGetted(List<VideoFavorite> data);
        void onNoDataGetted();
        void onDeleteSelectedData();

    }
}
