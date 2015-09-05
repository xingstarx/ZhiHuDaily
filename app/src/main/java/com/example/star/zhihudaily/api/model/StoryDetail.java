package com.example.star.zhihudaily.api.model;

import java.util.List;

/**
 * Created by xiongxingxing on 15/9/5.
 */
public class StoryDetail {
    //curl http://news-at.zhihu.com/api/4/news/7091506
    String body;
    String image_source;
    String title;
    String image;
    String share_url;
    List<String> js;
    List<Avatar> recommenders;
    String ga_prefix;
    int type;
    int id;
    List<String> css;

    //知乎日报可能将某个主题日报的站外文章推送至知乎日报首页
    String theme_name;
    String editor_name;
    int theme_id;
}
