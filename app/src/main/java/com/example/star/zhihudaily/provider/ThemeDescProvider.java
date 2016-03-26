package com.example.star.zhihudaily.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.star.zhihudaily.api.model.ThemeDesc;
import com.example.star.zhihudaily.util.LogUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ThemeDescProvider extends ContentProvider {

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.example.star.zhihudaily.provider.themedesc";
    public static final String CONTENT_LIST_TYPE = "vnd.android.cursor.dir/com.example.star.zhihudaily.provider.themedesc";
    public static final String TAG = "ThemeDescProvider";
    private static final int VERSION_NUM = 1;//db_version_number
    private static final int ITEM_TYPE = 1;
    private static final int LIST_TYPE = 2;
    private static final String AUTHORITY = "com.example.star.zhihudaily.provider.themedesc";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/themedesc");
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String DBNAME = "themedescdb";

    static {
        sUriMatcher.addURI(AUTHORITY, "themedesc", LIST_TYPE);
        sUriMatcher.addURI(AUTHORITY, "themedesc/#", ITEM_TYPE);
    }

    private ThemeDescDBHelper mThemeDescDBHelper;

    public ThemeDescProvider() {
    }

    public static void addThemeDescList(Context context, @NonNull List<ThemeDesc> themeDescList) {
        ContentValues[] contentValues = new ContentValues[themeDescList.size()];
        for (int i = 0; i < themeDescList.size(); i++) {
            contentValues[i] = generateValues(themeDescList.get(i));
            LogUtils.d(TAG, new Gson().toJson(themeDescList.get(i)).toString());
        }
        context.getContentResolver().bulkInsert(CONTENT_URI, contentValues);
    }

    private static ContentValues generateValues(ThemeDesc themeDesc) {
        ContentValues values = new ContentValues();
        values.put(ThemeDescColumns.COL_COLOR, themeDesc.color);
        values.put(ThemeDescColumns.COL_DESCRIPTION, themeDesc.description);
        values.put(ThemeDescColumns.COL_NAME, themeDesc.name);
        values.put(ThemeDescColumns.COL_THEMEID, themeDesc.id);
        values.put(ThemeDescColumns.COL_THUMBNAIL, themeDesc.thumbnail);
        values.put(ThemeDescColumns.COL_ISLIKE, themeDesc.is_like == true ? 1 : 0);
        return values;
    }

    public static boolean update(Context context, @NonNull ThemeDesc themeDesc) {
        ContentValues values = new ContentValues();
        values.put(ThemeDescColumns.COL_ISLIKE, themeDesc.is_like == true ? 1 : 0);
        return context.getContentResolver().update(ContentUris.withAppendedId(CONTENT_URI, themeDesc._id), values, null, null) > 0;
    }

    public static List<ThemeDesc> queryThemeDescList(Context context) {
        List<ThemeDesc> themeDescs = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ThemeDesc themeDesc = new ThemeDesc(cursor);
                themeDescs.add(themeDesc);
            }
            cursor.close();
        }
        return themeDescs;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = -1;
        SQLiteDatabase db = mThemeDescDBHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case LIST_TYPE:
                count = db.delete(ThemeDescColumns.TB_NAME, selection, selectionArgs);
                break;
            case ITEM_TYPE:
                count = db.delete(ThemeDescColumns.TB_NAME, ThemeDescColumns.COL_ID + "=" + uri.getPathSegments().get(1) + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);
                break;
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;


    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case LIST_TYPE:
                return CONTENT_LIST_TYPE;
            case ITEM_TYPE:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        boolean isMatch = sUriMatcher.match(uri) == ITEM_TYPE;
        if (!isMatch) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mThemeDescDBHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(ThemeDescColumns.TB_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(uri, null);
            return noteUri;
        } else {
            // Ignore conflict
            return CONTENT_URI;
        }
    }

    @Override
    public boolean onCreate() {
        mThemeDescDBHelper = new ThemeDescDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mThemeDescDBHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case LIST_TYPE:
                qBuilder.setTables(ThemeDescColumns.TB_NAME);
                break;
            case ITEM_TYPE:
                qBuilder.setTables(ThemeDescColumns.TB_NAME);
                qBuilder.appendWhere(ThemeDescColumns.COL_ID + "=" + uri.getPathSegments().get(1));
                break;
        }
        return qBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = -1;
        SQLiteDatabase db = mThemeDescDBHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case LIST_TYPE:
                count = db.update(ThemeDescColumns.TB_NAME, values, selection, selectionArgs);
                break;
            case ITEM_TYPE:
                count = db.update(ThemeDescColumns.TB_NAME, values, ThemeDescColumns.COL_ID + "=" + uri.getPathSegments().get(1) + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);
                break;
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int count = 0;
        boolean isMatch = sUriMatcher.match(uri) == LIST_TYPE;
        if (!isMatch) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = mThemeDescDBHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContentValues cv : values) {
                long newID = db.insertWithOnConflict(ThemeDescColumns.TB_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                if (newID >= 0) {
                    count++;
                }
            }
            db.setTransactionSuccessful();
            if (count > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        } finally {
            db.endTransaction();
        }
        return count;
    }

    protected static final class ThemeDescDBHelper extends SQLiteOpenHelper {
        private static final String SQL_CREATE_THEME_DESC = "create table "
                + ThemeDescColumns.TB_NAME + "(" +
                ThemeDescColumns.COL_ID + " integer primary key autoincrement, " +
                ThemeDescColumns.COL_COLOR + " integer ," +
                ThemeDescColumns.COL_THUMBNAIL + " text ," +
                ThemeDescColumns.COL_DESCRIPTION + " text ," +
                ThemeDescColumns.COL_THEMEID + " integer ," +
                ThemeDescColumns.COL_ISLIKE + " integer ," +
                ThemeDescColumns.COL_NAME + " text " +
                ");";

        ThemeDescDBHelper(Context context) {
            super(context, DBNAME, null, VERSION_NUM);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_THEME_DESC);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ThemeDescColumns.TB_NAME);
            onCreate(db);
        }
    }

    public static class ThemeDescColumns {
        public static final String TB_NAME = "themedesc";
        public static final String COL_ID = "_id";
        public static final String COL_COLOR = "color";
        public static final String COL_THUMBNAIL = "thumbnail";
        public static final String COL_DESCRIPTION = "description";
        public static final String COL_THEMEID = "themeid";
        public static final String COL_NAME = "name";
        public static final String COL_ISLIKE = "islike";
    }

}
