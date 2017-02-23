package org.wdd.app.android.interestcollection.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by richard on 2/10/17.
 */

public class HttpDataCache {

    private final String KEY_PHOTO_CATEGORY = "key_photo_category";

    private SharedPreferences mPref;

    public HttpDataCache(Context context) {
        mPref = context.getSharedPreferences("http_data", Context.MODE_PRIVATE);
    }

    public void savePhotoCategoryData(String category) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_PHOTO_CATEGORY, category);
        editor.commit();
    }

    public String getPhotoCategoryData() {
        return mPref.getString(KEY_PHOTO_CATEGORY, null);
    }

}
