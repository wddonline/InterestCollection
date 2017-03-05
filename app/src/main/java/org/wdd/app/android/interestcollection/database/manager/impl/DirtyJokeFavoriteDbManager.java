package org.wdd.app.android.interestcollection.database.manager.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.wdd.app.android.interestcollection.database.manager.DbManager;
import org.wdd.app.android.interestcollection.database.model.DirtyJokeFavorite;
import org.wdd.app.android.interestcollection.database.table.DirtyJokeFavoriteTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 1/22/17.
 */

public class DirtyJokeFavoriteDbManager extends DbManager<DirtyJokeFavorite> {

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DirtyJokeFavoriteTable.TABLE_NAME + "(" + DirtyJokeFavoriteTable.FIELD_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + DirtyJokeFavoriteTable.FIELD_TITLE + " VARCHAR2(100), " + DirtyJokeFavoriteTable.FIELD_TIME + " VARCHAR2(20)," +
                DirtyJokeFavoriteTable.FIELD_URL + " VARCHAR2(100) " + "NOT NULL UNIQUE, " + DirtyJokeFavoriteTable.FIELD_IMG_URL + " VARCHAR2(100));");
    }

    public DirtyJokeFavoriteDbManager(Context context) {
        super(context);
    }

    @Override
    public long insert(DirtyJokeFavorite data) {
        long result = -1;
        try {
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(DirtyJokeFavoriteTable.FIELD_TITLE, data.title);
            values.put(DirtyJokeFavoriteTable.FIELD_TIME, data.time);
            values.put(DirtyJokeFavoriteTable.FIELD_URL, data.url);
            values.put(DirtyJokeFavoriteTable.FIELD_IMG_URL, data.imgUrl);
            result = db.insert(DirtyJokeFavoriteTable.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return result;
    }

    @Override
    public List<DirtyJokeFavorite> queryAll() {
        List<DirtyJokeFavorite> result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {DirtyJokeFavoriteTable.FIELD_ID, DirtyJokeFavoriteTable.FIELD_TITLE, DirtyJokeFavoriteTable.FIELD_TIME, DirtyJokeFavoriteTable.FIELD_URL, DirtyJokeFavoriteTable.FIELD_IMG_URL};
            String orderBy = DirtyJokeFavoriteTable.FIELD_ID + " DESC";
            Cursor cursor = db.query(DirtyJokeFavoriteTable.TABLE_NAME, columns, null, null, null, null, orderBy);
            result = new ArrayList<>();
            DirtyJokeFavorite favorite;
            while (cursor.moveToNext()) {
                favorite = new DirtyJokeFavorite();
                favorite.id = cursor.getInt(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_ID));
                favorite.title = cursor.getString(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_TITLE));
                favorite.time = cursor.getString(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_TIME));
                favorite.url = cursor.getString(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_URL));
                favorite.imgUrl = cursor.getString(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_IMG_URL));
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
    public DirtyJokeFavorite queryById(int id) {
        DirtyJokeFavorite result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {DirtyJokeFavoriteTable.FIELD_ID, DirtyJokeFavoriteTable.FIELD_TITLE, DirtyJokeFavoriteTable.FIELD_TIME, DirtyJokeFavoriteTable.FIELD_URL, DirtyJokeFavoriteTable.FIELD_IMG_URL};
            String selection = DirtyJokeFavoriteTable.FIELD_ID + "=?";
            String[] selectionArgs = {id + ""};
            Cursor cursor = db.query(DirtyJokeFavoriteTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                result = new DirtyJokeFavorite();
                result.id = cursor.getInt(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_ID));
                result.title = cursor.getString(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_TITLE));
                result.time = cursor.getString(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_TIME));
                result.url = cursor.getString(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_URL));
                result.imgUrl = cursor.getString(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_IMG_URL));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return result;
    }

    public DirtyJokeFavorite getFavoriteByUrl(String url) {
        DirtyJokeFavorite result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {DirtyJokeFavoriteTable.FIELD_ID, DirtyJokeFavoriteTable.FIELD_TITLE, DirtyJokeFavoriteTable.FIELD_TIME, DirtyJokeFavoriteTable.FIELD_URL, DirtyJokeFavoriteTable.FIELD_IMG_URL};
            String selection = DirtyJokeFavoriteTable.FIELD_URL + "=?";
            String[] selectionArgs = {url};
            Cursor cursor = db.query(DirtyJokeFavoriteTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                result = new DirtyJokeFavorite();
                result.id = cursor.getInt(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_ID));
                result.title = cursor.getString(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_TITLE));
                result.time = cursor.getString(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_TIME));
                result.url = cursor.getString(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_URL));
                result.imgUrl = cursor.getString(cursor.getColumnIndex(DirtyJokeFavoriteTable.FIELD_IMG_URL));
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
            affectedRows = db.delete(DirtyJokeFavoriteTable.TABLE_NAME, null, null);
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
            String whereClause = DirtyJokeFavoriteTable.FIELD_ID + "=?";
            String[] whereArgs = {id + ""};
            affectedRows = db.delete(DirtyJokeFavoriteTable.TABLE_NAME, whereClause, whereArgs);
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
            String whereClause = DirtyJokeFavoriteTable.FIELD_URL + "=?";
            String[] whereArgs = {departmentid};
            affectedRows = db.delete(DirtyJokeFavoriteTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return affectedRows;
    }

    public int deleteFavorites(List<DirtyJokeFavorite> departments) {
        int affectedRows = 0;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            String whereClause = DirtyJokeFavoriteTable.FIELD_ID + " = ?";
            for (DirtyJokeFavorite d : departments) {
                String[] whereArgs = {d.id + ""};
                affectedRows += db.delete(DirtyJokeFavoriteTable.TABLE_NAME, whereClause, whereArgs);
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
