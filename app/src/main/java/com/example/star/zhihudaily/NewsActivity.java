package com.example.star.zhihudaily;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.example.star.zhihudaily.base.BaseActivity;

public class NewsActivity extends BaseActivity {

    private ViewPager mViewPager;
    private NewsAdapter mNewsAdapter;
    private Toolbar mToolbar;

    public static void showNews(Context context) {
        context.startActivity(new Intent(context, NewsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mToolbar = (Toolbar) findViewById(R.id.layout_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNewsAdapter = new NewsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mNewsAdapter);

    }

    private class NewsAdapter extends FragmentPagerAdapter {

        public NewsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return NewsFragment.newInstance(position + "");
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

}
