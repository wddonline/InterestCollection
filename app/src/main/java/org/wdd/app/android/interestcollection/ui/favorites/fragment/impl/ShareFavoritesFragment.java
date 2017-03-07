package org.wdd.app.android.interestcollection.ui.favorites.fragment.impl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wdd.app.android.interestcollection.ui.favorites.activity.FavoritesActionCallback;
import org.wdd.app.android.interestcollection.ui.favorites.adapter.AudioFavoritesAdapter;
import org.wdd.app.android.interestcollection.ui.favorites.fragment.FavoritesFragment;

/**
 * Created by richard on 3/7/17.
 */

public class ShareFavoritesFragment extends FavoritesFragment {

    private FavoritesActionCallback callback;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    protected void lazyLoad() {

    }

    public void setCallback(FavoritesActionCallback callback) {
        this.callback = callback;
    }

    @Override
    public void selectAll() {

    }

    @Override
    public void unselectAll() {

    }

    @Override
    public void cancelSelectMode() {

    }

    @Override
    public AudioFavoritesAdapter.Mode getMode() {
        return null;
    }

    @Override
    public int getSelectedCount() {
        return 0;
    }

    @Override
    public void deleteSelectedFavorites() {

    }

    @Override
    public void refreshDataRemoved(int position) {

    }
}
