package org.wdd.app.android.interestcollection.ui.favorites.adapter;

import android.content.Context;

import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter;

import java.util.List;

/**
 * Created by richard on 3/8/17.
 */

public abstract class AbstractFavoritesAdapter<T> extends AbstractCommonAdapter<T> {

    public enum Mode {
        Normal,
        Select
    }

    public AbstractFavoritesAdapter(Context context) {
        super(context);
    }

    public AbstractFavoritesAdapter(Context context, List<T> data) {
        super(context, data);
    }

}
