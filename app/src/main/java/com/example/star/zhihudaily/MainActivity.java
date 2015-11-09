package com.example.star.zhihudaily;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.star.zhihudaily.base.ListBaseAdapter;
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
    private String[] mPlanetTitles;
    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    };
    private Themes mThemes;
    private ThemeDescAdapter mThemeDescAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppAPI = new AppAPI(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        setTitle("首页");

        mMenuContainer = findViewById(R.id.menu_container);
        mMenuContent = findViewById(R.id.menu_content);
        mListView = (ListView) findViewById(R.id.listview);
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_string, R.string.close_string);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        mSubscription = AppObservable.bindActivity(this, mAppAPI.themes()).subscribe(new Action1<Themes>() {
            @Override
            public void call(Themes themes) {
                mThemes = themes;
                Log.e(TAG, new Gson().toJson(mThemes));
                initListViewData();

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "Error Infos");
            }
        });
    }

    private void initListViewData() {
        mThemeDescAdapter = new ThemeDescAdapter(MainActivity.this, new ArrayList<>(mThemes.others), R.layout.draw_layout_item);
        mListView.setAdapter(mThemeDescAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);
        selectItem(0);
    }

    private void selectItem(int position) {
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_container, fragment).commit();

        // update selected item and title, then close the drawer
        mListView.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        drawerLayout.closeDrawer(mMenuContainer);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(mMenuContainer);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_settings:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "app_not_available", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.planets_array)[i];
            ((TextView) rootView.findViewById(R.id.textview)).setText(planet);
            getActivity().setTitle(planet);
            return rootView;
        }
    }

    public class ThemeDescAdapter extends ListBaseAdapter<ThemeDesc> {

        public ThemeDescAdapter(Context ctx, ArrayList<ThemeDesc> dataList, int theRowResourceId) {
            super(ctx, dataList, theRowResourceId);
        }

        @Override
        public void prepareViewForDisplay(View view, ThemeDesc dataItem) {
            TextView textView = (TextView) view.findViewById(R.id.draw_item_text);
            ImageView imageView = (ImageView) view.findViewById(R.id.draw_item_icon);
            View drawItemIconLayout=view.findViewById(R.id.draw_item_icon_layout);
            drawItemIconLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "show infos", Toast.LENGTH_SHORT).show();
                }
            });
            textView.setText(dataItem.name);
            imageView.setImageResource(R.drawable.ic_menu_arrow);
        }
    }
}

