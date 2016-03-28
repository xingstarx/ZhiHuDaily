package com.example.star.zhihudaily.api.model;

import java.util.List;

/**
 * Created by xiongxingxing on 15/9/5.
 */
public class Story {
    public List<String> images;
    public String image;//首页轮播图对应的图片 //top_stories里面的数据
    public int type;
    public long id;
    public String ga_prefix;
    public String title;

    @Override
    public String toString() {
        return "Story{" +
                "images=" + images +
                ", image='" + image + '\'' +
                ", type=" + type +
                ", id=" + id +
                ", ga_prefix='" + ga_prefix + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
