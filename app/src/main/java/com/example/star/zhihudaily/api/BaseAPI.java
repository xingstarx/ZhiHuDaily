package com.example.star.zhihudaily.api;

import android.content.Context;

import com.example.star.zhihudaily.util.LogUtils;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by xiongxingxing on 15/9/5.
 */
public class BaseAPI {

    private static final String endPoint = "http://news-at.zhihu.com/api/4/";
    private static final int TIMEOUT_MILLISEC = 5000;
    private static final long CACHE_SIZE = 10 * 1024 * 1024;
    private RestAdapter restAdapter;
    private OkHttpClient okHttpClient;

    public BaseAPI(Context context) {
        File cacheDir = new File(context.getCacheDir(), "ZhiHuCache");
        okHttpClient = new OkHttpClient();
        if (LogUtils.DEBUG) {
            okHttpClient.networkInterceptors().add(new StethoInterceptor());
        }
        okHttpClient.setCache(new Cache(cacheDir, CACHE_SIZE));
        okHttpClient.setConnectTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setEndpoint(endPoint);//设置远程地址
        builder.setClient(new OkClient(okHttpClient));
        builder.setLogLevel(
                RestAdapter.LogLevel.BASIC);
        restAdapter = builder.build();
    }

    public RestAdapter getRestAdapter() {
        return restAdapter;
    }
}
