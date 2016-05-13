package com.example.star.zhihudaily;

import android.app.Application;

import com.example.star.zhihudaily.util.LogUtils;
import com.facebook.stetho.Stetho;

public class ZhiHuApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LogUtils.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }
}
