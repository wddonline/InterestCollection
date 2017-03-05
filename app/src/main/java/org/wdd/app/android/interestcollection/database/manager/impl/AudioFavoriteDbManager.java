package org.wdd.app.android.interestcollection.database.manager.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.wdd.app.android.interestcollection.database.manager.DbManager;
import org.wdd.app.android.interestcollection.database.model.AudioFavorite;
import org.wdd.app.android.interestcollection.database.table.AudioFavoriteTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 1/22/17.
 */

public class AudioFavoriteDbManager extends DbManager<AudioFavorite> {

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + AudioFavoriteTable.TABLE_NAME + "(" + AudioFavoriteTable.FIELD_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + AudioFavoriteTable.FIELD_TITLE + " VARCHAR2(100), " + AudioFavoriteTable.FIELD_TIME + " VARCHAR2(20)," +
                AudioFavoriteTable.FIELD_URL + " VARCHAR2(100) " + "NOT NULL UNIQUE, " + AudioFavoriteTable.FIELD_IMG_URL + " VARCHAR2(100));");
    }

    public AudioFavoriteDbManager(Context context) {
        super(context);
    }

    @Override
    public long insert(AudioFavorite data) {
        long result = -1;
        try {
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(AudioFavoriteTable.FIELD_TITLE, data.title);
            values.put(AudioFavoriteTable.FIELD_TIME, data.time);
            values.put(AudioFavoriteTable.FIELD_URL, data.url);
            values.put(AudioFavoriteTable.FIELD_IMG_URL, data.imgUrl);
            result = db.insert(AudioFavoriteTable.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return result;
    }

    @Override
    public List<AudioFavorite> queryAll() {
        List<AudioFavorite> result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {AudioFavoriteTable.FIELD_ID, AudioFavoriteTable.FIELD_TITLE, AudioFavoriteTable.FIELD_TIME, AudioFavoriteTable.FIELD_URL, AudioFavoriteTable.FIELD_IMG_URL};
            String orderBy = AudioFavoriteTable.FIELD_ID + " DESC";
            Cursor cursor = db.query(AudioFavoriteTable.TABLE_NAME, columns, null, null, null, null, orderBy);
            result = new ArrayList<>();
            AudioFavorite favorite;
            while (cursor.moveToNext()) {
                favorite = new AudioFavorite();
                favorite.id = cursor.getInt(cursor.getColumnIndex(AudioFavoriteTable.FIELD_ID));
                favorite.title = cursor.getString(cursor.getColumnIndex(AudioFavoriteTable.FIELD_TITLE));
                favorite.time = cursor.getString(cursor.getColumnIndex(AudioFavoriteTable.FIELD_TIME));
                favorite.url = cursor.getString(cursor.getColumnIndex(AudioFavoriteTable.FIELD_URL));
                favorite.imgUrl = cursor.getString(cursor.getColumnIndex(AudioFavoriteTable.FIELD_IMG_URL));
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
    public AudioFavorite queryById(int id) {
        AudioFavorite result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {AudioFavoriteTable.FIELD_ID, AudioFavoriteTable.FIELD_TITLE, AudioFavoriteTable.FIELD_TIME, AudioFavoriteTable.FIELD_URL, AudioFavoriteTable.FIELD_IMG_URL};
            String selection = AudioFavoriteTable.FIELD_ID + "=?";
            String[] selectionArgs = {id + ""};
            Cursor cursor = db.query(AudioFavoriteTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                result = new AudioFavorite();
                result.id = cursor.getInt(cursor.getColumnIndex(AudioFavoriteTable.FIELD_ID));
                result.title = cursor.getString(cursor.getColumnIndex(AudioFavoriteTable.FIELD_TITLE));
                result.time = cursor.getString(cursor.getColumnIndex(AudioFavoriteTable.FIELD_TIME));
                result.url = cursor.getString(cursor.getColumnIndex(AudioFavoriteTable.FIELD_URL));
                result.imgUrl = cursor.getString(cursor.getColumnIndex(AudioFavoriteTable.FIELD_IMG_URL));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return result;
    }

    public AudioFavorite getFavoriteByUrl(String url) {
        AudioFavorite result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {AudioFavoriteTable.FIELD_ID, AudioFavoriteTable.FIELD_TITLE, AudioFavoriteTable.FIELD_TIME, AudioFavoriteTable.FIELD_URL, AudioFavoriteTable.FIELD_IMG_URL};
            String selection = AudioFavoriteTable.FIELD_URL + "=?";
            String[] selectionArgs = {url};
            Cursor cursor = db.query(AudioFavoriteTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                result = new AudioFavorite();
                result.id = cursor.getInt(cursor.getColumnIndex(AudioFavoriteTable.FIELD_ID));
                result.title = cursor.getString(cursor.getColumnIndex(AudioFavoriteTable.FIELD_TITLE));
                result.time = cursor.getString(cursor.getColumnIndex(AudioFavoriteTable.FIELD_TIME));
                result.url = cursor.getString(cursor.getColumnIndex(AudioFavoriteTable.FIELD_URL));
                result.imgUrl = cursor.getString(cursor.getColumnIndex(AudioFavoriteTable.FIELD_IMG_URL));
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
            affectedRows = db.delete(AudioFavoriteTable.TABLE_NAME, null, null);
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
            String whereClause = AudioFavoriteTable.FIELD_ID + "=?";
            String[] whereArgs = {id + ""};
            affectedRows = db.delete(AudioFavoriteTable.TABLE_NAME, whereClause, whereArgs);
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
            String whereClause = AudioFavoriteTable.FIELD_URL + "=?";
            String[] whereArgs = {departmentid};
            affectedRows = db.delete(AudioFavoriteTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return affectedRows;
    }

    public int deleteFavorites(List<AudioFavorite> departments) {
        int affectedRows = 0;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            String whereClause = AudioFavoriteTable.FIELD_ID + " = ?";
            for (AudioFavorite d : departments) {
                String[] whereArgs = {d.id + ""};
                affectedRows += db.delete(AudioFavoriteTable.TABLE_NAME, whereClause, whereArgs);
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
