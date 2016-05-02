package com.example.star.zhihudaily;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.example.star.zhihudaily.base.BaseActivity;
import com.example.star.zhihudaily.util.LogUtils;
import com.example.star.zhihudaily.util.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewsActivity extends BaseActivity {

    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    private Drawable upArrow;
    private List<String> mNewsIdList;
    private static final String TAG = "NewsActivity";

    public static void showNews(Context context, long newsId) {
        Intent intent = new Intent(context, NewsActivity.class);
        intent.putExtra(NewsFragment.ARG_ID, newsId);
        context.startActivity(intent);
    }

    @SuppressLint("PrivateResource")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        String mNewsId = getIntent().getLongExtra(NewsFragment.ARG_ID, 0) + "";
        upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String newsViewPagerCountJson = SharedPrefsUtils.getStringPreference(NewsActivity.this, Settings.ZHIHU_NEWS_VIEWPAGER_COUNT_JSON);

        mNewsIdList = TextUtils.isEmpty(newsViewPagerCountJson) ? new ArrayList<String>(0) : Arrays.asList(newsViewPagerCountJson.split(","));

        NewsAdapter mNewsAdapter = new NewsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mNewsAdapter);
        if (!TextUtils.equals(mNewsId, "0")) {
            mViewPager.setCurrentItem(mNewsIdList.indexOf(mNewsId));
        }
    }

    public Drawable getUpArrow() {
        return upArrow;
    }

    private class NewsAdapter extends FragmentPagerAdapter {

        public NewsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return NewsFragment.newInstance(mNewsIdList.get(position));
        }

        @Override
        public int getCount() {
            return mNewsIdList.size();
        }
    }

}
