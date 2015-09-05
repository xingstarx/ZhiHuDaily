package com.example.star.zhihudaily.api;

import com.example.star.zhihudaily.api.model.LatestNews;
import com.example.star.zhihudaily.api.model.StoryDetail;
import com.example.star.zhihudaily.api.model.ThemeItem;
import com.example.star.zhihudaily.api.model.Themes;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by xiongxingxing on 15/9/5.
 */
public interface IAppAPI {

    //http://news-at.zhihu.com/api/4/news/latest
    @GET("/news/latest")
    Observable<LatestNews> latest();


    //curl http://news-at.zhihu.com/api/4/news/7091506
    @GET("/news/{id}")
    Observable<StoryDetail> storyDetail(@Path("id") String id);

    //curl http://news-at.zhihu.com/api/4/themes
    @GET("/themes")
    Observable<Themes> themes();

    @GET("/theme/{id}")
    Observable<ThemeItem> themeItem(@Path("id") String id);

}
