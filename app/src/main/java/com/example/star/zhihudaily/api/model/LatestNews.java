package com.example.star.zhihudaily.api.model;

import java.util.List;

/**
 * Created by xiongxingxing on 15/9/5.
 */
public class LatestNews {
    public String date;
    public List<Story> stories;
    public List<Story> top_stories;
    public LatestNews beforeLatestNews;//reference ,接口中并未使用到
}
