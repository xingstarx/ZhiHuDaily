package com.example.star.zhihudaily;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ScrollView;

import com.example.star.zhihudaily.api.AppAPI;
import com.example.star.zhihudaily.api.model.StoryDetail;
import com.example.star.zhihudaily.api.model.StoryExtraDetail;
import com.example.star.zhihudaily.widget.CustomMenuView;

import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subscriptions.Subscriptions;


public class NewsFragment extends Fragment {
    public static final String ARG_ID = "id";
    private static final String TAG = "NewsFragment";
    private String mId;
    private ScrollView mScrollView;
    private int mLastScrolly = -1;
    private boolean mIsTansparency = false;
    private boolean mToolbarFlag = false;
    private int mToolbarColor;
    private int mNewsImageHeight;
    private NewsActivity mActivity;
    private CustomMenuView mPraiseMenuView;
    private CustomMenuView mCommentMenuView;
    private CustomMenuView mShareView;
    private CustomMenuView mCollectView;
    private Subscription mSubscription = Subscriptions.empty();
    private AppAPI mAppAPI;
    private WebView mWebView;
    private StoryDetail mStoryDetail;
    private StoryExtraDetail mStoryExtraDetail;

    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
            int scrollY = mScrollView.getScrollY(); //for verticalScrollView
            if (scrollY <= 0) {
                return;
            }
            Log.e(TAG, "scrollY==" + scrollY);

            //超过范围是直接进行hide,show的
            if (scrollY > mNewsImageHeight) {
                //进行show,hide的时候，恢复transparency值
                if (!mIsTansparency) {
                    mIsTansparency = !mIsTansparency;
                    updateActionBarTransparency(1.0f);
                    updateMenuTansparency(1.0f);
                }
                if (scrollY > mLastScrolly && !mToolbarFlag) {
                    mActivity.getSupportActionBar().hide();
                    mToolbarFlag = true;
                } else if (scrollY < mLastScrolly && mToolbarFlag) {
                    mActivity.getSupportActionBar().show();
                    mToolbarFlag = false;
                }
            } else {//做渐变式的处理
                if (mIsTansparency) {
                    mIsTansparency = false;
                }
                updateActionBarTransparency(1.0f - 1.0f * scrollY / mNewsImageHeight);
                updateMenuTansparency(1.0f - 1.0f * scrollY / mNewsImageHeight);
            }

            mLastScrolly = scrollY;
        }
    };


    public static NewsFragment newInstance(String id) {
        NewsFragment fragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void updateActionBarTransparency(float scrollRatio) {
        int newAlpha = (int) (scrollRatio * 255);
        int color = Color.argb(newAlpha, Color.red(mToolbarColor), Color.green(mToolbarColor), Color.blue(mToolbarColor));
        mActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        Drawable indicator = mActivity.getUpArrow();
        indicator.setAlpha(newAlpha);
        mActivity.getSupportActionBar().setHomeAsUpIndicator(indicator);
    }

    private void updateMenuTansparency(float scrollRatio) {
        mShareView.updateMenuTansparency(scrollRatio);
        mCollectView.updateMenuTansparency(scrollRatio);
        mPraiseMenuView.updateMenuTansparency(scrollRatio);
        mCommentMenuView.updateMenuTansparency(scrollRatio);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getString(ARG_ID);
        }
        mAppAPI = new AppAPI(mActivity);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);
        mScrollView = (ScrollView) root.findViewById(R.id.scroll_view);
        mWebView = (WebView) root.findViewById(R.id.web_view);
        mToolbarColor = ContextCompat.getColor(mActivity, R.color.colorPrimary);
        mShareView = new CustomMenuView(mActivity);
        mCollectView = new CustomMenuView(mActivity);
        mPraiseMenuView = new CustomMenuView(mActivity);
        mCommentMenuView = new CustomMenuView(mActivity);
        mShareView.hideText().setImage(R.drawable.ic_menu_share);
        mCollectView.hideText().setImage(R.drawable.ic_menu_collect);
        mPraiseMenuView.setImage(R.drawable.ic_menu_praise);
        mCommentMenuView.setImage(R.drawable.ic_menu_comment);

        mNewsImageHeight = getResources().getDimensionPixelSize(R.dimen.news_image_height);
        updateActionBarTransparency(1);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSubscription = AppObservable.bindSupportFragment(this, mAppAPI.storyDetail(mId).zipWith(mAppAPI.storyExtraDetail(mId), new Func2<StoryDetail, StoryExtraDetail, StoryDetail>() {
            @Override
            public StoryDetail call(StoryDetail storyDetail, StoryExtraDetail storyExtraDetail) {
                mStoryDetail = storyDetail;
                mStoryExtraDetail = storyExtraDetail;
                return storyDetail;
            }
        })).subscribe(new Action1<StoryDetail>() {
            @Override
            public void call(StoryDetail storyDetail) {
                String TMP_URL = "http://stackoverflow.com/questions/10397613/how-can-i-get-the-current-location-of-an-actionbar-menuitem/13300185";
//                startWebView(TMP_URL);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        });
        startWebView("");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (NewsActivity) activity;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_news, menu);
        menu.findItem(R.id.browser_share).setActionView(mShareView);
        menu.findItem(R.id.collect).setActionView(mCollectView);
        menu.findItem(R.id.comment).setActionView(mCommentMenuView);
        menu.findItem(R.id.add_praise).setActionView(mPraiseMenuView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSubscription.unsubscribe();
    }

    private void startWebView(String url) {
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setSupportZoom(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDefaultTextEncodingName("GBK");
        mWebSettings.setLoadsImagesAutomatically(true);
        url = "http://frank-zhu.github.io/android/2015/08/19/android-html5-web-view/";
        mWebView.loadUrl(url);
    }
}
