package org.wdd.app.android.interestcollection.database.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by richard on 1/22/17.
 */

class DatabaseConnecter {

    private static DatabaseConnecter INSTANCE;

    public static DatabaseConnecter getInstance(Context context) {
        if (INSTANCE == null) {

            synchronized (DatabaseConnecter.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DatabaseConnecter(context);
                }
            }
        }
        return INSTANCE;
    }

    private SDSqliteOpenHelper sqliteOpenHelper;

    private AtomicInteger couter;

    private DatabaseConnecter(Context context) {
        sqliteOpenHelper = new SDSqliteOpenHelper(context);
        couter = new AtomicInteger(0);
    }

    SQLiteDatabase getReadableDatabase() {
        couter.incrementAndGet();
        return sqliteOpenHelper.getReadableDatabase();
    }

    SQLiteDatabase getWritableDatabase() {
        couter.incrementAndGet();
        return sqliteOpenHelper.getWritableDatabase();
    }

    void closeDatabase() {
        if(couter.decrementAndGet() == 0) {
            sqliteOpenHelper.close();
        }
    }

}
