package com.example.star.zhihudaily.api.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.example.star.zhihudaily.provider.ThemeDescProvider;

import java.io.Serializable;

/**
 * Created by xiongxingxing on 15/9/5.
 *
 * @desc 主题分类, 侧滑页面的item
 */
public class ThemeDesc implements Serializable {
    public long color;
    public String thumbnail;
    public String description;
    public int id;
    public String name;
    public int _id;//db primary key
    public transient boolean is_like;//用户是否喜欢

    public ThemeDesc() {

    }

    public ThemeDesc(@NonNull Cursor cursor) {
        _id = cursor.getInt(cursor.getColumnIndex(ThemeDescProvider.ThemeDescColumns.COL_ID));
        color = cursor.getLong(cursor.getColumnIndex(ThemeDescProvider.ThemeDescColumns.COL_COLOR));
        name = cursor.getString(cursor.getColumnIndex(ThemeDescProvider.ThemeDescColumns.COL_NAME));
        thumbnail = cursor.getString(cursor.getColumnIndex(ThemeDescProvider.ThemeDescColumns.COL_THUMBNAIL));
        description = cursor.getString(cursor.getColumnIndex(ThemeDescProvider.ThemeDescColumns.COL_DESCRIPTION));
        id = cursor.getInt(cursor.getColumnIndex(ThemeDescProvider.ThemeDescColumns.COL_THEMEID));
        is_like = cursor.getInt(cursor.getColumnIndex(ThemeDescProvider.ThemeDescColumns.COL_ISLIKE)) == 1 ? true : false;
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
