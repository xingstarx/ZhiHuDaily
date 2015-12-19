package com.example.star.zhihudaily.api.model;

import java.util.List;

/**
 * Created by xiongxingxing on 15/9/5.
 */
public class Story {
    public List<String> images;
    public int type;
    public long id;
    public String ga_prefix;
    public String title;

    @Override
    public String toString() {
        return "Story{" +
                "images=" + images +
                ", type=" + type +
                ", id=" + id +
                ", ga_prefix='" + ga_prefix + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
