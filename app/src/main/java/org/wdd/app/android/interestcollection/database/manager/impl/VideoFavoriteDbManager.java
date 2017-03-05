package org.wdd.app.android.interestcollection.database.manager.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.wdd.app.android.interestcollection.database.manager.DbManager;
import org.wdd.app.android.interestcollection.database.model.VideoFavorite;
import org.wdd.app.android.interestcollection.database.table.ShareFavoriteTable;
import org.wdd.app.android.interestcollection.database.table.VideoFavoriteTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 1/22/17.
 */

public class VideoFavoriteDbManager extends DbManager<VideoFavorite> {

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + VideoFavoriteTable.TABLE_NAME + "(" + VideoFavoriteTable.FIELD_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + VideoFavoriteTable.FIELD_TITLE + " VARCHAR2(100), " + VideoFavoriteTable.FIELD_TIME + " VARCHAR2(20)," +
                VideoFavoriteTable.FIELD_URL + " VARCHAR2(100) " + "NOT NULL UNIQUE, " + VideoFavoriteTable.FIELD_IMG_URL + " VARCHAR2(100));");
    }

    public VideoFavoriteDbManager(Context context) {
        super(context);
    }

    @Override
    public long insert(VideoFavorite data) {
        long result = -1;
        try {
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(VideoFavoriteTable.FIELD_TITLE, data.title);
            values.put(VideoFavoriteTable.FIELD_TIME, data.time);
            values.put(VideoFavoriteTable.FIELD_URL, data.url);
            values.put(VideoFavoriteTable.FIELD_IMG_URL, data.imgUrl);
            result = db.insert(VideoFavoriteTable.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return result;
    }

    @Override
    public List<VideoFavorite> queryAll() {
        List<VideoFavorite> result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {VideoFavoriteTable.FIELD_ID, VideoFavoriteTable.FIELD_TITLE, VideoFavoriteTable.FIELD_TIME, VideoFavoriteTable.FIELD_URL, VideoFavoriteTable.FIELD_IMG_URL};
            String orderBy = VideoFavoriteTable.FIELD_ID + " DESC";
            Cursor cursor = db.query(VideoFavoriteTable.TABLE_NAME, columns, null, null, null, null, orderBy);
            result = new ArrayList<>();
            VideoFavorite favorite;
            while (cursor.moveToNext()) {
                favorite = new VideoFavorite();
                favorite.id = cursor.getInt(cursor.getColumnIndex(VideoFavoriteTable.FIELD_ID));
                favorite.title = cursor.getString(cursor.getColumnIndex(VideoFavoriteTable.FIELD_TITLE));
                favorite.time = cursor.getString(cursor.getColumnIndex(VideoFavoriteTable.FIELD_TIME));
                favorite.url = cursor.getString(cursor.getColumnIndex(VideoFavoriteTable.FIELD_URL));
                favorite.imgUrl = cursor.getString(cursor.getColumnIndex(VideoFavoriteTable.FIELD_IMG_URL));
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
    public VideoFavorite queryById(int id) {
        VideoFavorite result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {VideoFavoriteTable.FIELD_ID, VideoFavoriteTable.FIELD_TITLE, VideoFavoriteTable.FIELD_TIME, VideoFavoriteTable.FIELD_URL, VideoFavoriteTable.FIELD_IMG_URL};
            String selection = VideoFavoriteTable.FIELD_ID + "=?";
            String[] selectionArgs = {id + ""};
            Cursor cursor = db.query(VideoFavoriteTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                result = new VideoFavorite();
                result.id = cursor.getInt(cursor.getColumnIndex(ShareFavoriteTable.FIELD_ID));
                result.title = cursor.getString(cursor.getColumnIndex(VideoFavoriteTable.FIELD_TITLE));
                result.time = cursor.getString(cursor.getColumnIndex(VideoFavoriteTable.FIELD_TIME));
                result.url = cursor.getString(cursor.getColumnIndex(VideoFavoriteTable.FIELD_URL));
                result.imgUrl = cursor.getString(cursor.getColumnIndex(VideoFavoriteTable.FIELD_IMG_URL));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return result;
    }

    public VideoFavorite getFavoriteByUrl(String url) {
        VideoFavorite result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {VideoFavoriteTable.FIELD_ID, VideoFavoriteTable.FIELD_TITLE, VideoFavoriteTable.FIELD_TIME, VideoFavoriteTable.FIELD_URL, VideoFavoriteTable.FIELD_IMG_URL};
            String selection = VideoFavoriteTable.FIELD_URL + "=?";
            String[] selectionArgs = {url};
            Cursor cursor = db.query(VideoFavoriteTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                result = new VideoFavorite();
                result.id = cursor.getInt(cursor.getColumnIndex(ShareFavoriteTable.FIELD_ID));
                result.title = cursor.getString(cursor.getColumnIndex(VideoFavoriteTable.FIELD_TITLE));
                result.time = cursor.getString(cursor.getColumnIndex(VideoFavoriteTable.FIELD_TIME));
                result.url = cursor.getString(cursor.getColumnIndex(VideoFavoriteTable.FIELD_URL));
                result.imgUrl = cursor.getString(cursor.getColumnIndex(VideoFavoriteTable.FIELD_IMG_URL));
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
            affectedRows = db.delete(VideoFavoriteTable.TABLE_NAME, null, null);
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
            String whereClause = VideoFavoriteTable.FIELD_ID + "=?";
            String[] whereArgs = {id + ""};
            affectedRows = db.delete(VideoFavoriteTable.TABLE_NAME, whereClause, whereArgs);
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
            String whereClause = VideoFavoriteTable.FIELD_URL + "=?";
            String[] whereArgs = {departmentid};
            affectedRows = db.delete(VideoFavoriteTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return affectedRows;
    }

    public int deleteFavorites(List<VideoFavorite> departments) {
        int affectedRows = 0;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            String whereClause = VideoFavoriteTable.FIELD_ID + " = ?";
            for (VideoFavorite d : departments) {
                String[] whereArgs = {d.id + ""};
                affectedRows += db.delete(VideoFavoriteTable.TABLE_NAME, whereClause, whereArgs);
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
