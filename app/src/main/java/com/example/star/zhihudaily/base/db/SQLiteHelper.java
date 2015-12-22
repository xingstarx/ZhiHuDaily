package com.example.star.zhihudaily.base.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.star.zhihudaily.util.LogUtils;


/**
 * This class manages database creation and updates.
 * <p/>
 * Based on "Android SQLite Database and ContentProvider - Tutorial" by Lars
 * Vogel. ( http://www.vogella.com/articles/AndroidSQLite/article.html )
 *
 * @author Andreas Bender
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "zhihudatabase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_IDENTITIES = "create table "
            + ThemeDescDb.TABLE_NAME + "(" +
            ThemeDescDb.COL_ID + " integer primary key autoincrement, " +
            ThemeDescDb.COL_COLOR + " integer ," +
            ThemeDescDb.COL_THUMBNAIL + " text ," +
            ThemeDescDb.COL_DESCRIPTION + " text ," +
            ThemeDescDb.COL_THEMEID + " integer ," +
            ThemeDescDb.COL_ISLIKE + " integer ," +
            ThemeDescDb.COL_NAME + " text " +
            ");";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        LogUtils.v(getClass().getSimpleName(), "db execsql: " + CREATE_IDENTITIES);
        database.execSQL(CREATE_IDENTITIES);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.v(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + ThemeDescDb.TABLE_NAME);
        onCreate(db);
    }

}