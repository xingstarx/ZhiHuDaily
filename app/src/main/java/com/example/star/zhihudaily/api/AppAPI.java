package com.example.star.zhihudaily.api;

import android.content.Context;

import com.example.star.zhihudaily.api.model.LatestNews;
import com.example.star.zhihudaily.api.model.StoryDetail;
import com.example.star.zhihudaily.api.model.StoryExtraDetail;
import com.example.star.zhihudaily.api.model.StoryNews;
import com.example.star.zhihudaily.api.model.Themes;

import rx.Observable;

/**
 * Created by xiongxingxing on 15/9/5.
 */
public class AppAPI extends BaseAPI {
    private IAppAPI mAppAPI;

    public AppAPI(Context context) {
        super(context);
        mAppAPI = getRestAdapter().create(IAppAPI.class);
    }

    public Observable<LatestNews> latest() {
        return mAppAPI.latest();
    }

    public Observable<LatestNews> latestBefore(String id) {
        return mAppAPI.latestBefore(id);
    }

    public Observable<StoryDetail> storyDetail(String id) {
        return mAppAPI.storyDetail(id);
    }

    public Observable<StoryExtraDetail> storyExtraDetail(String id) {
        return mAppAPI.storyExtraDetail(id);
    }

    public Observable<Themes> themes() {
        return mAppAPI.themes();
    }

    public Observable<StoryNews> themeItem(int id) {
        return mAppAPI.themeItem(id);
    }

    public Observable<StoryNews> latestBefore(int themeId, long storyId) {
        return mAppAPI.latestBefore(themeId, storyId);
    }
}
