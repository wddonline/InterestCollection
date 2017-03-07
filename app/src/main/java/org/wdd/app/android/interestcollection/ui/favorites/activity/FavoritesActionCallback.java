package org.wdd.app.android.interestcollection.ui.favorites.activity;

/**
 * Created by richard on 3/7/17.
 */

public interface FavoritesActionCallback {

    void switchSelectMode();
    void onAllSelected();
    void onPartSelected();
    void resetTitleBar();

}
