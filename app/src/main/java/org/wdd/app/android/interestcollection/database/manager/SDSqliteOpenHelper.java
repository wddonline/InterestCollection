package org.wdd.app.android.interestcollection.database.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.wdd.app.android.interestcollection.database.manager.impl.AudioFavoriteDbManager;
import org.wdd.app.android.interestcollection.database.manager.impl.DirtyJokeFavoriteDbManager;
import org.wdd.app.android.interestcollection.database.manager.impl.ImageFavoriteDbManager;
import org.wdd.app.android.interestcollection.database.manager.impl.ShareFavoriteDbManager;
import org.wdd.app.android.interestcollection.database.manager.impl.VideoFavoriteDbManager;

/**
 * Created by richard on 1/22/17.
 */

class SDSqliteOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "good_girl.db";
    private static final int DB_VERSION = 1;

    public SDSqliteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            AudioFavoriteDbManager.createTable(db);
            DirtyJokeFavoriteDbManager.createTable(db);
            ImageFavoriteDbManager.createTable(db);
            ShareFavoriteDbManager.createTable(db);
            VideoFavoriteDbManager.createTable(db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
