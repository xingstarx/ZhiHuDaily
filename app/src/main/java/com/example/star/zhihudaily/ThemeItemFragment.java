package com.example.star.zhihudaily;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

import com.example.star.zhihudaily.api.AppAPI;
import com.example.star.zhihudaily.api.model.Story;
import com.example.star.zhihudaily.api.model.StoryNews;
import com.example.star.zhihudaily.api.model.ThemeDesc;
import com.example.star.zhihudaily.base.EndlessRecyclerOnScrollListener;
import com.example.star.zhihudaily.base.recyclerview.AutoRVAdapter;
import com.example.star.zhihudaily.base.recyclerview.ViewHolder;
import com.example.star.zhihudaily.util.LogUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;


public class ThemeItemFragment extends Fragment {
    public static final String TAG = ThemeItemFragment.class.getSimpleName();
    private static final String ARG_THEME_DESC = "themeDesc";
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Activity mActivity;
    private MainAdapter mMainAdapter;
    private List<Story> mStoryList = new ArrayList<>();
    private Subscription mSubscription = Subscriptions.empty();
    private AppAPI mAppAPI;
    private StoryNews mStoryNews;
    private ThemeDesc mThemeDesc;

    public static ThemeItemFragment newInstance(ThemeDesc themeDesc) {
        ThemeItemFragment fragment = new ThemeItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_THEME_DESC, themeDesc);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAppAPI = new AppAPI(mActivity);
        mThemeDesc = (ThemeDesc) getArguments().getSerializable(ARG_THEME_DESC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_theme, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_theme, menu);
        if (mThemeDesc.is_like) {
            menu.findItem(R.id.action_theme_remove).setVisible(false);
        } else {
            menu.findItem(R.id.action_theme_add).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_theme_add:
                break;
            case R.id.action_theme_remove:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchData() {
        mSubscription = AppObservable.bindSupportFragment(this, mAppAPI.themeItem(mThemeDesc.id)).subscribe(new Action1<StoryNews>() {
            @Override
            public void call(StoryNews storyNews) {
                mStoryNews = storyNews;
                mStoryList = storyNews.stories;
                mMainAdapter.setData(mStoryList);
                mMainAdapter.notifyDataSetChanged();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

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
        mRecyclerView.setAdapter(mMainAdapter);
        EndlessRecyclerOnScrollListener onScrollerListener = new EndlessRecyclerOnScrollListener();
        onScrollerListener.setOnListLoadNextPageListener(new EndlessRecyclerOnScrollListener.OnListLoadNextPageListener() {
            @Override
            public void onLoadNextPage(View view) {
                loadMore();
            }
        });
        mRecyclerView.addOnScrollListener(onScrollerListener);
    }

    private void loadMore() {
        mSubscription = AppObservable.bindSupportFragment(this, mAppAPI.latestBefore(mThemeDesc.id, mStoryList.get(mStoryList.size() - 1).id)).subscribe(new Action1<StoryNews>() {
            @Override
            public void call(StoryNews storyNews) {
                mStoryList.addAll(storyNews.stories);
                mMainAdapter.setData(mStoryList);
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
        public void onBindViewHolder(ViewHolder holder, int position) {
            Story story = mStoryList.get(position);
            LogUtils.d(TAG, story.toString());
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
