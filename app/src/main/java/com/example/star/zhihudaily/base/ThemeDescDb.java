package com.example.star.zhihudaily.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.example.star.zhihudaily.api.model.ThemeDesc;
import com.example.star.zhihudaily.api.model.Themes;
import com.example.star.zhihudaily.util.LogUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc ThemeDesc 主题栏目
 */
public class ThemeDescDb extends AbstractDb {

    public static final String TABLE_NAME = "themedesc";
    public static final String COL_COLOR = "color";
    public static final String COL_THUMBNAIL = "thumbnail";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_THEMEID = "themeid";
    public static final String COL_NAME = "name";
    public static final String COL_ISLIKE = "islike";
    public static final String[] COLUMNS = {
            COL_ID, COL_COLOR, COL_THUMBNAIL, COL_DESCRIPTION, COL_THEMEID, COL_NAME, COL_ISLIKE
    };

    public ThemeDescDb(Context context) {
        super(context);
    }

    protected String[] getColumns() {
        return COLUMNS;
    }

    protected String getTableName() {
        return TABLE_NAME;
    }

    protected String getLogTag() {
        return "ThemeDescDb";
    }

    protected boolean isStringColumn(String c) {
        return (c == COL_THUMBNAIL || c == COL_DESCRIPTION || c == COL_NAME);
    }

    protected boolean isIntegerColumn(String c) {
        return (c == COL_ID || c == COL_THEMEID || c == COL_COLOR || c == COL_ISLIKE);
    }

    @Override
    protected boolean isBlobColumn(String c) {
        return false;
    }

    public Themes findAllThemes() {
        Themes themes = new Themes();
        List<ThemeDesc> themeDescs = new ArrayList<>();
        Cursor cursor = all();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ThemeDesc themeDesc = new ThemeDesc(cursor);
                themeDescs.add(themeDesc);
            }
            cursor.close();
        }
        themes.others = themeDescs;
        return themes;
    }

    public void addAllThemes(@NonNull Themes themes) {
        for (ThemeDesc themeDesc : themes.others) {
            ContentValues values = new ContentValues();
            values.put(COL_COLOR, themeDesc.color);
            values.put(COL_DESCRIPTION, themeDesc.description);
            values.put(COL_NAME, themeDesc.name);
            values.put(COL_THEMEID, themeDesc.id);
            values.put(COL_THUMBNAIL, themeDesc.thumbnail);
            values.put(COL_ISLIKE, themeDesc.is_like == true ? 1 : 0);
            LogUtils.d(getLogTag(), new Gson().toJson(values).toString());
            LogUtils.d(getLogTag(), themeDesc.toString());
            create(values);
        }
    }

    public boolean update(@NonNull ThemeDesc themeDesc) {
        ContentValues values = new ContentValues();
        values.put(ThemeDescDb.COL_ISLIKE, themeDesc.is_like == true ? 1 : 0);
        return database.update(getTableName(), values, COL_ID + " = " + themeDesc._id, null) > 0;
    }

}