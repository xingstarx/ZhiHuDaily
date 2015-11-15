package com.example.star.zhihudaily.api.model;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.star.zhihudaily.base.ThemeDescDb;

/**
 * Created by xiongxingxing on 15/9/5.
 *
 * @desc 主题分类
 */
public class ThemeDesc {
    public long color;
    public String thumbnail;
    public String description;
    public int id;
    public String name;
    public int _id;//db primary key
    public boolean is_like;//用户是否喜欢

    public ThemeDesc() {

    }

    public ThemeDesc(@NonNull Cursor cursor) {
        _id = cursor.getInt(cursor.getColumnIndex(ThemeDescDb.COL_ID));
        color = cursor.getLong(cursor.getColumnIndex(ThemeDescDb.COL_COLOR));
        name = cursor.getString(cursor.getColumnIndex(ThemeDescDb.COL_NAME));
        thumbnail = cursor.getString(cursor.getColumnIndex(ThemeDescDb.COL_THUMBNAIL));
        description = cursor.getString(cursor.getColumnIndex(ThemeDescDb.COL_DESCRIPTION));
        id = cursor.getInt(cursor.getColumnIndex(ThemeDescDb.COL_THEMEID));
        is_like = cursor.getInt(cursor.getColumnIndex(ThemeDescDb.COL_ISLIKE)) == 1 ? true : false;
        Log.d("ThemeDesc", toString());
    }

    @Override
    public String toString() {
        return "ThemeDesc{" +
                "color=" + color +
                ", thumbnail='" + thumbnail + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", _id=" + _id +
                ", is_like=" + is_like +
                '}';
    }
}
