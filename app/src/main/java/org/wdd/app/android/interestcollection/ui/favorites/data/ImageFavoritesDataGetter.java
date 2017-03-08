package org.wdd.app.android.interestcollection.ui.favorites.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.wdd.app.android.interestcollection.database.manager.impl.ImageFavoriteDbManager;
import org.wdd.app.android.interestcollection.database.model.ImageFavorite;
import org.wdd.app.android.interestcollection.ui.base.ActivityFragmentAvaliable;

import java.util.List;

/**
 * Created by richard on 1/24/17.
 */

public class ImageFavoritesDataGetter {

    private Context mContext;
    private ImageFavoriteDbManager mDbManager;
    private ActivityFragmentAvaliable mHost;
    private DataCallback mCallback;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public ImageFavoritesDataGetter(Context context, DataCallback callback, ActivityFragmentAvaliable host) {
        this.mContext = context;
        this.mCallback = callback;
        this.mHost = host;
        mDbManager = new ImageFavoriteDbManager(context);
    }

    public void queryImageFavorites() {
        Thread thread = new Thread(new QueryAction());
        thread.setDaemon(true);
        thread.start();
    }

    public void deleteSelectedFavorites(List<ImageFavorite> selectedItems) {
        Thread thread = new Thread(new DeleteItemsAction(selectedItems));
        thread.setDaemon(true);
        thread.start();
    }

    private class DeleteItemsAction implements Runnable {

        private List<ImageFavorite> favorites;

        public DeleteItemsAction(List<ImageFavorite> doctors) {
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
            final List<ImageFavorite> result = mDbManager.queryAll();
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

        void onDataGetted(List<ImageFavorite> data);
        void onNoDataGetted();
        void onDeleteSelectedData();

    }
}
