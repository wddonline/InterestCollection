package org.wdd.app.android.interestcollection.database.manager.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.wdd.app.android.interestcollection.database.manager.DbManager;
import org.wdd.app.android.interestcollection.database.model.ImageFavorite;
import org.wdd.app.android.interestcollection.database.table.ImageFavoriteTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 1/22/17.
 */

public class ImageFavoriteDbManager extends DbManager<ImageFavorite> {

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ImageFavoriteTable.TABLE_NAME + "(" + ImageFavoriteTable.FIELD_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + ImageFavoriteTable.FIELD_TITLE + " VARCHAR2(100), " + ImageFavoriteTable.FIELD_TIME + " VARCHAR2(20)," +
                ImageFavoriteTable.FIELD_URL + " VARCHAR2(100) " + "NOT NULL UNIQUE, " + ImageFavoriteTable.FIELD_IMG_URL + " VARCHAR2(100), " +
                ImageFavoriteTable.FIELD_GIF +  " INTEGER NOT NULL);");
    }

    public ImageFavoriteDbManager(Context context) {
        super(context);
    }

    @Override
    public long insert(ImageFavorite data) {
        long result = -1;
        try {
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(ImageFavoriteTable.FIELD_TITLE, data.title);
            values.put(ImageFavoriteTable.FIELD_TIME, data.time);
            values.put(ImageFavoriteTable.FIELD_URL, data.url);
            values.put(ImageFavoriteTable.FIELD_IMG_URL, data.imgUrl);
            values.put(ImageFavoriteTable.FIELD_GIF, data.gifFlag);
            result = db.insert(ImageFavoriteTable.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return result;
    }

    @Override
    public List<ImageFavorite> queryAll() {
        List<ImageFavorite> result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {ImageFavoriteTable.FIELD_ID, ImageFavoriteTable.FIELD_TITLE, ImageFavoriteTable.FIELD_TIME, ImageFavoriteTable.FIELD_URL, ImageFavoriteTable.FIELD_IMG_URL, ImageFavoriteTable.FIELD_GIF};
            String orderBy = ImageFavoriteTable.FIELD_ID + " DESC";
            Cursor cursor = db.query(ImageFavoriteTable.TABLE_NAME, columns, null, null, null, null, orderBy);
            result = new ArrayList<>();
            ImageFavorite favorite;
            while (cursor.moveToNext()) {
                favorite = new ImageFavorite();
                favorite.id = cursor.getInt(cursor.getColumnIndex(ImageFavoriteTable.FIELD_ID));
                favorite.title = cursor.getString(cursor.getColumnIndex(ImageFavoriteTable.FIELD_TITLE));
                favorite.time = cursor.getString(cursor.getColumnIndex(ImageFavoriteTable.FIELD_TIME));
                favorite.url = cursor.getString(cursor.getColumnIndex(ImageFavoriteTable.FIELD_URL));
                favorite.imgUrl = cursor.getString(cursor.getColumnIndex(ImageFavoriteTable.FIELD_IMG_URL));
                favorite.gifFlag = cursor.getInt(cursor.getColumnIndex(ImageFavoriteTable.FIELD_GIF));
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
    public ImageFavorite queryById(int id) {
        ImageFavorite result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {ImageFavoriteTable.FIELD_ID, ImageFavoriteTable.FIELD_TITLE, ImageFavoriteTable.FIELD_TIME, ImageFavoriteTable.FIELD_URL, ImageFavoriteTable.FIELD_IMG_URL, ImageFavoriteTable.FIELD_GIF};
            String selection = ImageFavoriteTable.FIELD_ID + "=?";
            String[] selectionArgs = {id + ""};
            Cursor cursor = db.query(ImageFavoriteTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                result = new ImageFavorite();
                result.id = cursor.getInt(cursor.getColumnIndex(ImageFavoriteTable.FIELD_ID));
                result.title = cursor.getString(cursor.getColumnIndex(ImageFavoriteTable.FIELD_TITLE));
                result.time = cursor.getString(cursor.getColumnIndex(ImageFavoriteTable.FIELD_TIME));
                result.url = cursor.getString(cursor.getColumnIndex(ImageFavoriteTable.FIELD_URL));
                result.imgUrl = cursor.getString(cursor.getColumnIndex(ImageFavoriteTable.FIELD_IMG_URL));
                result.gifFlag = cursor.getInt(cursor.getColumnIndex(ImageFavoriteTable.FIELD_GIF));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return result;
    }

    public ImageFavorite getFavoriteByUrl(String url) {
        ImageFavorite result = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {ImageFavoriteTable.FIELD_ID, ImageFavoriteTable.FIELD_TITLE, ImageFavoriteTable.FIELD_TIME, ImageFavoriteTable.FIELD_URL, ImageFavoriteTable.FIELD_IMG_URL, ImageFavoriteTable.FIELD_GIF};
            String selection = ImageFavoriteTable.FIELD_URL + "=?";
            String[] selectionArgs = {url};
            Cursor cursor = db.query(ImageFavoriteTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                result = new ImageFavorite();
                result.id = cursor.getInt(cursor.getColumnIndex(ImageFavoriteTable.FIELD_ID));
                result.title = cursor.getString(cursor.getColumnIndex(ImageFavoriteTable.FIELD_TITLE));
                result.time = cursor.getString(cursor.getColumnIndex(ImageFavoriteTable.FIELD_TIME));
                result.url = cursor.getString(cursor.getColumnIndex(ImageFavoriteTable.FIELD_URL));
                result.imgUrl = cursor.getString(cursor.getColumnIndex(ImageFavoriteTable.FIELD_IMG_URL));
                result.gifFlag = cursor.getInt(cursor.getColumnIndex(ImageFavoriteTable.FIELD_GIF));
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
            affectedRows = db.delete(ImageFavoriteTable.TABLE_NAME, null, null);
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
            String whereClause = ImageFavoriteTable.FIELD_ID + "=?";
            String[] whereArgs = {id + ""};
            affectedRows = db.delete(ImageFavoriteTable.TABLE_NAME, whereClause, whereArgs);
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
            String whereClause = ImageFavoriteTable.FIELD_URL + "=?";
            String[] whereArgs = {departmentid};
            affectedRows = db.delete(ImageFavoriteTable.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return affectedRows;
    }

    public int deleteFavorites(List<ImageFavorite> departments) {
        int affectedRows = 0;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            String whereClause = ImageFavoriteTable.FIELD_ID + " = ?";
            for (ImageFavorite d : departments) {
                String[] whereArgs = {d.id + ""};
                affectedRows += db.delete(ImageFavoriteTable.TABLE_NAME, whereClause, whereArgs);
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
