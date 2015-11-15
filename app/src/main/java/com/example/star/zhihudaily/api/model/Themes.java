package com.example.star.zhihudaily.api.model;

import java.util.List;

/**
 * Created by xiongxingxing on 15/9/5.
 * @desc 首页面 主题列表
 */
public class Themes {
    public int limit;
    public List<String> subscribed;
    public List<ThemeDesc> others;
}
