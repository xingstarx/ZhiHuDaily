package com.example.star.zhihudaily;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.star.zhihudaily.api.AppAPI;
import com.example.star.zhihudaily.api.model.ThemeDesc;
import com.example.star.zhihudaily.api.model.Themes;
import com.example.star.zhihudaily.base.BaseActivity;
import com.example.star.zhihudaily.base.adapter.ListHeaderBaseAdapter;
import com.example.star.zhihudaily.base.db.ThemeDescDb;
import com.example.star.zhihudaily.util.LogUtils;
import com.example.star.zhihudaily.util.SharedPrefsUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

public class MainActivity extends BaseActivity {
    private AppAPI mAppAPI;
    private Subscription mSubscription = Subscriptions.empty();
    private String TAG = MainActivity.class.getSimpleName();
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private View mMenuContainer;
    private View mMenuContent;
    private ListView mListView;
    private int mLastSelection;
    private Themes mThemes;
    private ThemeDescAdapter mThemeDescAdapter;
    private MainFragment mainFragment;
    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            mThemeDescAdapter.notifyDataSetInvalidated();//刷新页面,设置点击后,选中改行效果
        }
    };
    private ThemeDescDb mThemeDescDb;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppAPI = new AppAPI(this);
        mThemeDescDb = new ThemeDescDb(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);

        mMenuContainer = findViewById(R.id.menu_container);
        mMenuContent = findViewById(R.id.menu_content);
        mListView = (ListView) findViewById(R.id.listview);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_string, R.string.close_string);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        if (SharedPrefsUtils.getBooleanPreference(this, Settings.ZHIHU_HAS_THEME_THEMEDESC, false)) {
            mThemeDescDb.open();
            mThemes = mThemeDescDb.findAllThemes();
            mThemeDescDb.close();
            initListViewData();
        } else {
            mSubscription = AppObservable.bindActivity(this, mAppAPI.themes()).subscribe(new Action1<Themes>() {
                @Override
                public void call(Themes themes) {
                    mThemes = themes;
                    LogUtils.d(TAG, new Gson().toJson(mThemes));
                    mThemeDescDb.open();
                    mThemeDescDb.addAllThemes(themes);
                    mThemeDescDb.close();
                    SharedPrefsUtils.setBooleanPreference(MainActivity.this, Settings.ZHIHU_HAS_THEME_THEMEDESC, true);
                    initListViewData();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e(TAG, "Error Infos");
                }
            });
        }

    }


    private void initListViewData() {
        mThemeDescAdapter = new ThemeDescAdapter(MainActivity.this, new ArrayList<>(mThemes.others), R.layout.draw_layout_item, R.layout.draw_layout_item_header);
        mListView.setAdapter(mThemeDescAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);
        selectItem(0);
    }

    private void selectItem(int position) {
        this.mLastSelection = position;
        Fragment fragment;
        LogUtils.d(TAG, "position==" + position);
        if (position == 0) {
            if (mainFragment != null) {
                fragment = mainFragment;
            } else {
                mainFragment = MainFragment.newInstance("首页");
                fragment = mainFragment;
            }
            getSupportActionBar().setTitle("首页");
        } else {
            fragment = ThemeItemFragment.newInstance(mThemes.others.get(position - 1));
            getSupportActionBar().setTitle(mThemes.others.get(position - 1).name);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_container, fragment).commit();

        // update selected item and title, then close the drawer
        mListView.setItemChecked(position, true);
        drawerLayout.closeDrawer(mMenuContainer);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    public class ThemeDescAdapter extends ListHeaderBaseAdapter<ThemeDesc> {
        public ThemeDescAdapter(Context ctx, ArrayList<ThemeDesc> dataList, int theRowResourceId) {
            super(ctx, dataList, theRowResourceId);
        }

        public ThemeDescAdapter(Context ctx, ArrayList<ThemeDesc> dataList, int theRowResourceId, int headerViewResourceId) {
            super(ctx, dataList, theRowResourceId, headerViewResourceId);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parentView) {
            View view = super.getView(position, convertView, parentView);
            if (position == mLastSelection) {
                view.setBackgroundColor(Color.rgb(239, 240, 241));
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            return view;
        }

        @Override
        public void prepareViewForDisplay(View view, final ThemeDesc dataItem) {
            if (dataItem == null) {
                return;
            }
            TextView textView = (TextView) view.findViewById(R.id.draw_item_text);
            ImageView imageView = (ImageView) view.findViewById(R.id.draw_item_icon);
            View drawItemIconLayout = view.findViewById(R.id.draw_item_icon_layout);
            drawItemIconLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "show infos", Toast.LENGTH_SHORT).show();
                    if (dataItem.is_like == false) {
                        dataItem.is_like = true;
                        mThemeDescDb.open();
                        mThemeDescDb.update(dataItem);
                        mThemeDescDb.close();
                        notifyDataSetChanged();
                    }
                }
            });
            textView.setText(dataItem.name);
            if (dataItem.is_like == false) {
                imageView.setImageResource(R.drawable.ic_menu_arrow);
            } else {
                imageView.setImageResource(R.drawable.ic_menu_follow);
            }
        }
    }
}

