package org.wdd.app.android.interestcollection.database.manager.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.wdd.app.android.interestcollection.database.manager.DbManager;
import org.wdd.app.android.interestcollection.database.model.ShareFavorite;
import org.wdd.app.android.interestcollection.database.table.ShareFavoriteTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 1/22/17.
 */

public class ShareFavoriteDbManager extends DbManager<ShareFavorite> {

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ShareFavoriteTable.TABLE_NAME + "(" + ShareFavoriteTable.FIELD_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + ShareFavoriteTable.FIELD_TITLE + " VARCHAR2(100), " + ShareFavoriteTable.FIELD_TIME + " VARCHAR2(20)," +
                ShareFavoriteTable.FIELD_URL + " VARCHAR2(100) " + "NOT NULL UNIQUE, " + ShareFavoriteTable.FIELD_IMG_URL + " VARCHAR2(100));");
    }

    public ShareFavoriteDbManager(Context context) {
        super(context);
    }

    @Override
    public long insert(ShareFavorite data) {
        long result = -1;
        try {
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(ShareFavoriteTable.FIELD_TITLE, data.title);
            values.put(ShareFavoriteTable.FIELD_TIME, data.time);
            values.put(ShareFavoriteTable.FIELD_URL, data.url);
            values.put(ShareFavoriteTable.FIELD_IMG_URL, data.imgUrl);
            result = db.insert(ShareFavoriteTable.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return result;
    }

    @Override
    public List<ShareFavorite> queryAll() {
        List<ShareFavorite> result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {ShareFavoriteTable.FIELD_ID, ShareFavoriteTable.FIELD_TITLE, ShareFavoriteTable.FIELD_TIME, ShareFavoriteTable.FIELD_URL, ShareFavoriteTable.FIELD_IMG_URL};
            String orderBy = ShareFavoriteTable.FIELD_ID + " DESC";
            Cursor cursor = db.query(ShareFavoriteTable.TABLE_NAME, columns, null, null, null, null, orderBy);
            result = new ArrayList<>();
            ShareFavorite favorite;
            while (cursor.moveToNext()) {
                favorite = new ShareFavorite();
                favorite.id = cursor.getInt(cursor.getColumnIndex(ShareFavoriteTable.FIELD_ID));
                favorite.title = cursor.getString(cursor.getColumnIndex(ShareFavoriteTable.FIELD_TITLE));
                favorite.time = cursor.getString(cursor.getColumnIndex(ShareFavoriteTable.FIELD_TIME));
                favorite.url = cursor.getString(cursor.getColumnIndex(ShareFavoriteTable.FIELD_URL));
                favorite.imgUrl = cursor.getString(cursor.getColumnIndex(ShareFavoriteTable.FIELD_IMG_URL));
                result.add(favorite);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return result;
    }

    @Override
    public ShareFavorite queryById(int id) {
        ShareFavorite result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {ShareFavoriteTable.FIELD_ID, ShareFavoriteTable.FIELD_TITLE, ShareFavoriteTable.FIELD_TIME, ShareFavoriteTable.FIELD_URL, ShareFavoriteTable.FIELD_IMG_URL};
            String selection = ShareFavoriteTable.FIELD_ID + "=?";
            String[] selectionArgs = {id + ""};
            Cursor cursor = db.query(ShareFavoriteTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                result = new ShareFavorite();
                result.id = cursor.getInt(cursor.getColumnIndex(ShareFavoriteTable.FIELD_ID));
                result.title = cursor.getString(cursor.getColumnIndex(ShareFavoriteTable.FIELD_TITLE));
                result.time = cursor.getString(cursor.getColumnIndex(ShareFavoriteTable.FIELD_TIME));
                result.url = cursor.getString(cursor.getColumnIndex(ShareFavoriteTable.FIELD_URL));
                result.imgUrl = cursor.getString(cursor.getColumnIndex(ShareFavoriteTable.FIELD_IMG_URL));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return result;
    }

    public ShareFavorite getFavoriteByUrl(String url) {
        ShareFavorite result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {ShareFavoriteTable.FIELD_ID, ShareFavoriteTable.FIELD_TITLE, ShareFavoriteTable.FIELD_TIME, ShareFavoriteTable.FIELD_URL, ShareFavoriteTable.FIELD_IMG_URL};
            String selection = ShareFavoriteTable.FIELD_URL + "=?";
            String[] selectionArgs = {url};
            Cursor cursor = db.query(ShareFavoriteTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                result = new ShareFavorite();
                result.id = cursor.getInt(cursor.getColumnIndex(ShareFavoriteTable.FIELD_ID));
                result.title = cursor.getString(cursor.getColumnIndex(ShareFavoriteTable.FIELD_TITLE));
                result.time = cursor.getString(cursor.getColumnIndex(ShareFavoriteTable.FIELD_TIME));
                result.url = cursor.getString(cursor.getColumnIndex(ShareFavoriteTable.FIELD_URL));
                result.imgUrl = cursor.getString(cursor.getColumnIndex(ShareFavoriteTable.FIELD_IMG_URL));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return result;
    }

    @Override
    public int deleteAll() {
        int affectedRows = 0;
        try {
            SQLiteDatabase db = getWritableDatabase();
            affectedRows = db.delete(ShareFavoriteTable.TABLE_NAME, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return affectedRows;
    }

    @Override
    public int deleteById(int id) {
        int affectedRows = 0;
        try {
            SQLiteDatabase db = getWritableDatabase();
            String whereClause = ShareFavoriteTable.FIELD_ID + "=?";
            String[] whereArgs = {id + ""};
            affectedRows = db.delete(ShareFavoriteTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return affectedRows;
    }

    public long deleteByUrl(String departmentid) {
        int affectedRows = 0;
        try {
            SQLiteDatabase db = getWritableDatabase();
            String whereClause = ShareFavoriteTable.FIELD_URL + "=?";
            String[] whereArgs = {departmentid};
            affectedRows = db.delete(ShareFavoriteTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return affectedRows;
    }

    public int deleteFavorites(List<ShareFavorite> departments) {
        int affectedRows = 0;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            String whereClause = ShareFavoriteTable.FIELD_ID + " = ?";
            for (ShareFavorite d : departments) {
                String[] whereArgs = {d.id + ""};
                affectedRows += db.delete(ShareFavoriteTable.TABLE_NAME, whereClause, whereArgs);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }
        return affectedRows;
    }
}
