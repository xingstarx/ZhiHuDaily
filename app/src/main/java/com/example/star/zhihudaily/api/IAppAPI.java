package com.example.star.zhihudaily.api;

import com.example.star.zhihudaily.api.model.LatestNews;
import com.example.star.zhihudaily.api.model.StoryDetail;
import com.example.star.zhihudaily.api.model.StoryExtraDetail;
import com.example.star.zhihudaily.api.model.StoryNews;
import com.example.star.zhihudaily.api.model.Themes;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by xiongxingxing on 15/9/5.
 */
public interface IAppAPI {

    //http://news-at.zhihu.com/api/4/news/latest
    //http://news-at.zhihu.com/api/4/stories/latest 于2015年12月19日抓包测试的新接口
    @GET("/stories/latest")
    Observable<LatestNews> latest();

    //http://news-at.zhihu.com/api/4/stories/before/20151219
    @GET("/stories/before/{id}")
    Observable<LatestNews> latestBefore(@Path("id") String id);

    //curl http://news-at.zhihu.com/api/4/news/7091506
    //http://news-at.zhihu.com/api/4/story/7548380  于2015年12月19日抓包测试的新接口
    @GET("/story/{id}")
    Observable<StoryDetail> storyDetail(@Path("id") String id);

    //评论,点赞数据汇总
    @GET("/story-extra/{id}")
    Observable<StoryExtraDetail> storyExtraDetail(@Path("id") String id);


    //curl http://news-at.zhihu.com/api/4/themes
    @GET("/themes")
    Observable<Themes> themes();

    @GET("/theme/{id}")
    Observable<StoryNews> themeItem(@Path("id") int id);

    //http://news-at.zhihu.com/api/4/theme/13/before/4737612
    @GET("/theme/{themeId}/before/{storyId}")
    Observable<StoryNews> latestBefore(@Path("themeId") int themeId, @Path("storyId") long storyId);


}
