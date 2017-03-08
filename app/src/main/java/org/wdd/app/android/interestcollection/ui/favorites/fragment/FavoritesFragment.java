package org.wdd.app.android.interestcollection.ui.favorites.fragment;

import org.wdd.app.android.interestcollection.ui.base.BaseFragment;
import org.wdd.app.android.interestcollection.ui.favorites.adapter.AbstractFavoritesAdapter;

/**
 * Created by richard on 3/7/17.
 */

public abstract class FavoritesFragment extends BaseFragment {

    public abstract void selectAll();
    public abstract void unselectAll();
    public abstract void cancelSelectMode();
    public abstract AbstractFavoritesAdapter.Mode getMode();
    public abstract int getSelectedCount();
    public abstract void deleteSelectedFavorites();
    public abstract void refreshDataRemoved(int id);
}
