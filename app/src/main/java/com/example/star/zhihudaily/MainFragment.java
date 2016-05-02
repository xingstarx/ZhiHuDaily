package com.example.star.zhihudaily;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.star.zhihudaily.api.AppAPI;
import com.example.star.zhihudaily.api.model.LatestNews;
import com.example.star.zhihudaily.api.model.Story;
import com.example.star.zhihudaily.base.EndlessRecyclerOnScrollListener;
import com.example.star.zhihudaily.base.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.example.star.zhihudaily.base.adapter.RecyclerViewUtils;
import com.example.star.zhihudaily.base.recyclerview.AutoRVAdapter;
import com.example.star.zhihudaily.base.recyclerview.RVHolder;
import com.example.star.zhihudaily.base.recyclerview.ViewHolder;
import com.example.star.zhihudaily.util.DateUtils;
import com.example.star.zhihudaily.util.LogUtils;
import com.example.star.zhihudaily.util.SharedPrefsUtils;
import com.example.star.zhihudaily.widget.Banner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subscriptions.Subscriptions;


public class MainFragment extends Fragment {
    public static final String TAG = MainFragment.class.getSimpleName();
    private static final String ARG_TITLE = "title";
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Activity mActivity;
    private String mTitle;
    private MainAdapter mMainAdapter;
    private List<Story> mStoryList = new ArrayList<>();
    private Subscription mSubscription = Subscriptions.empty();
    private AppAPI mAppAPI;
    private String mCurrentDateStr = DateUtils.dateToString(new Date(), DateUtils.yyyyMMDD);
    private String mRefreshDateStr;
    private Banner mBanner;

    @NonNull
    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Story story = mStoryList.get(position);
            SharedPrefsUtils.setStringPreference(mActivity, Settings.ZHIHU_NEWS_VIEWPAGER_COUNT_JSON, renderJson(mStoryList));
            NewsActivity.showNews(mActivity, story.id);
        }
    };

    public static MainFragment newInstance(String title) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    private String renderJson(List<Story> storyList) {
        if (storyList == null && storyList.size() == 0) {
            return null;
        }
        StringBuffer sbf = new StringBuffer();
        for (Story story : storyList) {
            sbf.append(story.id).append(",");
        }
        return sbf.toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAppAPI = new AppAPI(mActivity);
        mTitle = getArguments().getString(ARG_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_main, menu);
        if (SharedPrefsUtils.getBooleanPreference(mActivity, Settings.ZHIHU_ACTION_MODE, true)) {
            menu.findItem(R.id.action_daily_mode).setVisible(false);
        } else {
            menu.findItem(R.id.action_night_mode).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notification:
                break;
            case R.id.action_daily_mode:
                SharedPrefsUtils.setBooleanPreference(mActivity, Settings.ZHIHU_ACTION_MODE, true);
                mActivity.invalidateOptionsMenu();
                break;
            case R.id.action_night_mode:
                SharedPrefsUtils.setBooleanPreference(mActivity, Settings.ZHIHU_ACTION_MODE, false);
                mActivity.invalidateOptionsMenu();
                break;
            case R.id.action_settings:
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 第一次，加载两页，是latest api接口，接着是加载before currentdate的数据，应该是同时并发处理的。http://news-at.zhihu.com/api/4/stories/latest，
     * http://news-at.zhihu.com/api/4/stories/before/20151219
     * 首页的加载更多应该就是这样做的了。接下来的加载更多也是一样的处理方式，，加载before currentDate -1
     */
    private void fetchData() {
        LogUtils.d(TAG, "mCurrentDateStr==" + mCurrentDateStr);
        mSubscription = AppObservable.bindSupportFragment(this, Observable.zip(mAppAPI.latest(), mAppAPI.latestBefore(mCurrentDateStr),
                new Func2<LatestNews, LatestNews, LatestNews>() {
                    @Override
                    public LatestNews call(LatestNews latestNews, LatestNews beforeLatestNews) {
                        latestNews.beforeLatestNews = beforeLatestNews;
                        return latestNews;
                    }
                })).subscribe(new Action1<LatestNews>() {
            @Override
            public void call(LatestNews latestNews) {
                mStoryList = latestNews.stories;
                mStoryList.addAll(latestNews.beforeLatestNews.stories);
                mBanner.handleAdapter(latestNews.top_stories);
                mMainAdapter.setData(mStoryList);
                mMainAdapter.notifyDataSetChanged();
                mRefreshDateStr = latestNews.beforeLatestNews.date;
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Toast.makeText(mActivity, "MainFragment throwable==" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mMainAdapter = new MainAdapter(mActivity, mStoryList);
        HeaderAndFooterRecyclerViewAdapter headerViewRecyclerAdapter = new HeaderAndFooterRecyclerViewAdapter(mMainAdapter);
        mMainAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(headerViewRecyclerAdapter);
        EndlessRecyclerOnScrollListener onScrollerListener = new EndlessRecyclerOnScrollListener();
        onScrollerListener.setOnListLoadNextPageListener(new EndlessRecyclerOnScrollListener.OnListLoadNextPageListener() {
            @Override
            public void onLoadNextPage(View view) {
                loadMore();
            }
        });

        mBanner = (Banner) LayoutInflater.from(mActivity).inflate(R.layout.layout_banner, mRecyclerView, false);
        RecyclerViewUtils.setHeaderView(mRecyclerView, mBanner);
        mRecyclerView.addOnScrollListener(onScrollerListener);
    }

    /**
     * load more data logic
     */
    private void loadMore() {
        mSubscription = AppObservable.bindSupportFragment(this, mAppAPI.latestBefore(mRefreshDateStr)).subscribe(new Action1<LatestNews>() {
            @Override
            public void call(LatestNews latestNews) {
                mStoryList.addAll(latestNews.stories);
                mMainAdapter.setData(mStoryList);
                mRefreshDateStr = latestNews.date;
                mMainAdapter.notifyDataSetChanged();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        });
    }

    private void doRefresh() {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        fetchData();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fetchData();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        ButterKnife.unbind(this);
    }

    class MainAdapter extends AutoRVAdapter {

        public MainAdapter(Context context, List<?> list) {
            super(context, list);
        }

        public void setData(List<Story> storyList) {
            this.list = storyList;
        }

        @Override
        public int onCreateViewLayoutID(int viewType) {
            return R.layout.recyclerview_item_story_layout;
        }

        @Override
        public void onBindViewHolder(final RVHolder holder, final int position) {
            onBindViewHolder(holder.getViewHolder(), position);
            if (onItemClickListener != null) {
                holder.getViewHolder().get(R.id.item_background_selector_panel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(null, v, RecyclerViewUtils.getAdapterPosition(mRecyclerView, holder), holder.getItemId());
                    }
                });
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Story story = mStoryList.get(position);
            if (true) {
                // TODO: 15/12/6
            }
            holder.get(R.id.story_title_layout).setVisibility(View.GONE);

            if (story.images != null && story.images.size() > 0) {
                Picasso.with(mActivity).load(story.images.get(0)).placeholder(R.drawable.lks_for_blank_url).fit().into(holder.getImageView(R.id.story_image_view));
            } else {
                Picasso.with(mActivity).load(R.drawable.lks_for_blank_url).fit().into(holder.getImageView(R.id.story_image_view));
            }
            holder.getTextView(R.id.story_content_text).setText(story.title);
        }
    }
}
