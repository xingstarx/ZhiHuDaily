package com.example.star.zhihudaily.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.star.zhihudaily.util.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides basic functionality to use a database table.
 *
 * @author Andreas Bender
 */
public abstract class AbstractDb {


    public static final String COL_ID = "_id";

    protected SQLiteHelper dbHelper;
    protected SQLiteDatabase database;

    public AbstractDb(Context context) {
        this.dbHelper = new SQLiteHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Create a new entry.
     *
     * @param values A HashMap containing Column - Value pairs.
     *               new HashMap<String, Object>().put(COL_IP4, "127.0.0.1");
     * @return The ID of the newly created entry or -1 if an error occured.
     */
    public long create(HashMap<String, ?> values) {

        ContentValues storeValues = new ContentValues();


        for (Map.Entry<String, ?> entry : values.entrySet()) {
            String column = entry.getKey();
            if (isStringColumn(column)) {
                storeValues.put(column, (String) entry.getValue());
            } else if (isIntegerColumn(column)) {
                storeValues.put(column, (Integer) entry.getValue());
            } else {
                Log.w(getLogTag(), ThemeDescDb.class.getName() +
                        "- create: Trying to insert an unknown column type! " +
                        "(Column: " + column + ")");
            }
        }

        return database.insert(getTableName(), null, storeValues);
    }

    public long create(ContentValues contentValues) {
        long result = 0;
        try {
            result = database.insert(getTableName(), null, contentValues);
        } catch (Exception e) {
            LogUtils.e(getLogTag(), e.getMessage());
        }
        return result;
    }

    public void delete(long id) {
        database.delete(getTableName(), COL_ID + " = " + id, null);
    }

    /**
     * Update an existing entry.
     *
     * @param id     The themeId identifying the entry.
     * @param values A HashMap containing Column - Value pairs.
     *               new HashMap<String, Object>().put(COL_IP4, "127.0.0.1");
     * @return True if any row has been changed. False otherwise.
     */
    public boolean update(long id, HashMap<String, ?> values) {
        ContentValues storeValues = new ContentValues();

        for (Map.Entry<String, ?> entry : values.entrySet()) {
            String column = entry.getKey();
            if (isStringColumn(column)) {
                storeValues.put(column, (String) entry.getValue());
            } else if (isIntegerColumn(column)) {
                storeValues.put(column, (Integer) entry.getValue());
            } else if (isBlobColumn(column)) {
                storeValues.put(column, (byte[]) entry.getValue());
            } else {
                Log.w(getLogTag(), ThemeDescDb.class.getName() +
                        "- update: Trying to update an unknown column type! " +
                        "(Column: " + column + ")");
            }
        }
        return database.update(getTableName(), storeValues, COL_ID + " = " + id, null) > 0;
    }

    /**
     * Find an item based on its ID.
     *
     * @param id The ID of the required item.
     * @return A Cursor pointing to the required item or null if it's not present.
     */
    public Cursor get(long id) {

        Cursor cursor = database.query(true, getTableName(), getColumns(),
                COL_ID + "=" + id, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }

    /**
     * Find all items of the table.
     *
     * @return A Cursor pointing to the first item of the table.
     */
    public Cursor all() {
        return database.query(getTableName(), getColumns(), null, null, null, null, null);
    }

    public boolean isEmpty() {
        return (all().getCount() == 0);
    }

    /**
     * @return All columns of the table, including the ID-column.
     */
    protected abstract String[] getColumns();

    /**
     * @return The name of the table.
     */
    protected abstract String getTableName();

    /**
     * @return The log tag used by LogCat, something like "MY_APP - DB"
     */
    protected abstract String getLogTag();

    /**
     * @param c The column which needs to be evaluated.
     * @return True if the column stores Strings, false otherwise.
     */
    protected abstract boolean isStringColumn(String c);


    /**
     * @param c The column which needs to be evaluated.
     * @return True if the column stores Integers, false otherwise.
     */
    protected abstract boolean isIntegerColumn(String c);

    /**
     * @param c The column which needs to be evaluated.
     * @return True if the column stores Blobs, false otherwise.
     */
    protected abstract boolean isBlobColumn(String c);

}