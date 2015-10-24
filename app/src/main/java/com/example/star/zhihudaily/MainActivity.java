package com.example.star.zhihudaily;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.example.star.zhihudaily.api.AppAPI;
import com.example.star.zhihudaily.api.model.ThemeDesc;
import com.example.star.zhihudaily.api.model.Themes;
import com.example.star.zhihudaily.base.BaseActivity;
import com.example.star.zhihudaily.widget.CustomPrimaryDrawerItem;
import com.google.gson.Gson;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

public class MainActivity extends BaseActivity {
    private static final int PROFILE_SETTING = 1;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private AppAPI mAppAPI;
    private Subscription mSubscription = Subscriptions.empty();
    private String TAG = MainActivity.class.getSimpleName();
    Toolbar toolbar;
    Themes mThemes;
    private ArrayList<IDrawerItem> mDrawerItems;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppAPI = new AppAPI(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.header);

        final IProfile profile = new ProfileDrawerItem().withName("xingxing").withEmail("xxx823952375@gmail.com").withIcon("http://pic3.zhimg.com/0e71e90fd6be47630399d63c58beebfc.jpg");


        mSubscription = AppObservable.bindActivity(this, mAppAPI.themes()).subscribe(new Action1<Themes>() {
            @Override
            public void call(Themes themes) {
                mThemes = themes;
                initDrawerItem();
                initDrawer(savedInstanceState);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "Error Infos");
            }
        });


        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
//            result.setSelection(21, false);
            //set the active profile
//            headerResult.setActiveProfile(profile);
        }
    }

    private void initDrawerItem() {
        mDrawerItems = new ArrayList<>();
        int i = 1;
        for (ThemeDesc themeDesc : mThemes.others) {
            Log.e(TAG, new Gson().toJson(themeDesc).toString());
//            PrimaryDrawerItem drawerItem = new PrimaryDrawerItem().withName(themeDesc.name).withIcon(R.drawable.header).withIdentifier(i).withSelectable(false);
            PrimaryDrawerItem drawerItem = new CustomPrimaryDrawerItem().withName(themeDesc.name).withIcon(R.drawable.menu_follow).withIdentifier(i).withSelectable(false);
            mDrawerItems.add(drawerItem);
            i++;
        }
    }

    private void initDrawer(Bundle savedInstanceState) {
        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withHeader(R.layout.main_header)
//                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .withDrawerItems(mDrawerItems)// add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }
}

