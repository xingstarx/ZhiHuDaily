package com.example.star.zhihudaily;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.example.star.zhihudaily.api.AppAPI;
import com.example.star.zhihudaily.base.BaseActivity;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class MainActivity extends BaseActivity {
    Toolbar toolbar;
    private AppAPI mAppAPI;
    private Subscription mSubscription = Subscriptions.empty();
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppAPI = new AppAPI(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
//        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        setTitle("首页");




        getSupportFragmentManager().beginTransaction().replace(R.id.menu_content, DrawMenuFragment.newInstance()).commit();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_string, R.string.close_string);
        actionBarDrawerToggle.syncState();

        drawerLayout.setDrawerListener(actionBarDrawerToggle);


//        mSubscription = AppObservable.bindActivity(this, mAppAPI.themes()).subscribe(new Action1<Themes>() {
//            @Override
//            public void call(Themes themes) {
//                mThemes = themes;
////                initDrawerItem();
////                initDrawer(savedInstanceState);
//            }
//        }, new Action1<Throwable>() {
//            @Override
//            public void call(Throwable throwable) {
//                Log.e(TAG, "Error Infos");
//            }
//        });


        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
//            result.setSelection(21, false);
            //set the active profile
//            headerResult.setActiveProfile(profile);
        }
    }

//    private void initDrawerItem() {
//        mDrawerItems = new ArrayList<>();
//        int i = 1;
//        for (ThemeDesc themeDesc : mThemes.others) {
//            Log.e(TAG, new Gson().toJson(themeDesc).toString());
////            PrimaryDrawerItem drawerItem = new PrimaryDrawerItem().withName(themeDesc.name).withIcon(R.drawable.header).withIdentifier(i).withSelectable(false);
//            PrimaryDrawerItem drawerItem = new CustomPrimaryDrawerItem().withName(themeDesc.name).withIcon(R.drawable.menu_follow).withIdentifier(i).withSelectable(false);
//            mDrawerItems.add(drawerItem);
//            i++;
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }
}

