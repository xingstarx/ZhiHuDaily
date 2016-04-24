package com.example.star.zhihudaily.api.model;

import java.util.List;

/**
 * Created by xiongxingxing on 15/9/5.
 */
public class StoryDetail {
    //curl http://news-at.zhihu.com/api/4/news/7091506
    public String body;
    public String image_source;
    public String title;
    public String image;
    public String share_url;
    public List<String> js;
    public List<Avatar> recommenders;
    public String ga_prefix;
    public int type;
    public int id;
    public List<String> css;

    //知乎日报可能将某个主题日报的站外文章推送至知乎日报首页
    public String theme_name;
    public String editor_name;
    public int theme_id;
}
