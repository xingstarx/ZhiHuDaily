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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.star.zhihudaily.api.AppAPI;
import com.example.star.zhihudaily.api.model.ThemeDesc;
import com.example.star.zhihudaily.api.model.Themes;
import com.example.star.zhihudaily.base.BaseActivity;
import com.example.star.zhihudaily.base.adapter.ListHeaderBaseAdapter;
import com.example.star.zhihudaily.provider.ThemeDescProvider;
import com.example.star.zhihudaily.util.LogUtils;
import com.example.star.zhihudaily.util.SharedPrefsUtils;
import com.example.star.zhihudaily.widget.BezelImageView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

public class MainActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.content_container)
    FrameLayout mContentContainer;
    @Bind(R.id.profile_avatar)
    BezelImageView mProfileAvatar;
    @Bind(R.id.listview)
    ListView mListView;
    @Bind(R.id.menu_content)
    FrameLayout mMenuContent;
    @Bind(R.id.menu_container)
    LinearLayout mMenuContainer;
    @Bind(R.id.drawer)
    DrawerLayout mDrawerLayout;
    private AppAPI mAppAPI;
    private Subscription mSubscription = Subscriptions.empty();
    private String TAG = MainActivity.class.getSimpleName();
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private int mLastSelection;
    private List<ThemeDesc> mThemeDescList;
    private ThemeDescAdapter mThemeDescAdapter;
    private MainFragment mainFragment;
    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            mThemeDescAdapter.notifyDataSetInvalidated();//刷新页面,设置点击后,选中改行效果
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAppAPI = new AppAPI(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.activity_main_open, R.string.activity_main_close);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        if (SharedPrefsUtils.getBooleanPreference(this, Settings.ZHIHU_HAS_THEME_THEMEDESC, false)) {
            mThemeDescList = ThemeDescProvider.queryThemeDescList(MainActivity.this);
            initListViewData();
        } else {
            mSubscription = AppObservable.bindActivity(this, mAppAPI.themes()).subscribe(new Action1<Themes>() {
                @Override
                public void call(Themes themes) {
                    mThemeDescList = themes.others;
                    ThemeDescProvider.addThemeDescList(MainActivity.this, mThemeDescList);
                    LogUtils.d(TAG, new Gson().toJson(themes));
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
        mThemeDescAdapter = new ThemeDescAdapter(MainActivity.this, new ArrayList<>(mThemeDescList), R.layout.draw_layout_item, R.layout.draw_layout_item_header);
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
                mainFragment = MainFragment.newInstance(getResources().getString(R.string.activity_main_title));
                fragment = mainFragment;
            }
            getSupportActionBar().setTitle(R.string.activity_main_title);
        } else {
            fragment = ThemeItemFragment.newInstance(mThemeDescList.get(position - 1));
            getSupportActionBar().setTitle(mThemeDescList.get(position - 1).name);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_container, fragment).commit();

        // update selected item and title, then close the drawer
        mListView.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mMenuContainer);

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
                        ThemeDescProvider.update(MainActivity.this, dataItem);
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

