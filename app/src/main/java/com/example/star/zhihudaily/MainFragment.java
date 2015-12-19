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
import com.example.star.zhihudaily.api.model.LatestNews;
import com.example.star.zhihudaily.api.model.Story;
import com.example.star.zhihudaily.base.recyclerview.AutoRVAdapter;
import com.example.star.zhihudaily.base.recyclerview.ViewHolder;
import com.example.star.zhihudaily.util.LogUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;


public class MainFragment extends Fragment {
    public static final String TAG = MainFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Activity mActivity;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MainAdapter mMainAdapter;
    private List<Story> mStoryList = new ArrayList<>();
    private Subscription mSubscription = Subscriptions.empty();
    private AppAPI mAppAPI;
    private LatestNews mLatestNews;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAppAPI = new AppAPI(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void fetchData() {
        mSubscription = AppObservable.bindSupportFragment(this, mAppAPI.latest()).subscribe(new Action1<LatestNews>() {
            @Override
            public void call(LatestNews latestNews) {
                mLatestNews = latestNews;
                mStoryList = latestNews.stories;
                mMainAdapter.setAdapter(mStoryList);
                mMainAdapter.notifyDataSetChanged();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        });

    }

    private void initViews(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mMainAdapter = new MainAdapter(mActivity, mStoryList);
        mRecyclerView.setAdapter(mMainAdapter);
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

    }

    class MainAdapter extends AutoRVAdapter {

        public MainAdapter(Context context, List<?> list) {
            super(context, list);
        }

        public void setAdapter(List<Story> storyList) {
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
