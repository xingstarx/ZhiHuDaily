package com.example.star.zhihudaily.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.star.zhihudaily.NewsActivity;
import com.example.star.zhihudaily.R;
import com.example.star.zhihudaily.Settings;
import com.example.star.zhihudaily.api.model.Story;
import com.example.star.zhihudaily.util.SharedPrefsUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Banner
 */
public class Banner extends LinearLayout {

    public static final String TAG = "Banner";
    private static final int MSG_PLAY = 1000;
    private final int FAKE_BANNER_SIZE = 100;
    private final int DEFAULT_BANNER_SIZE = 5;
    private int mPlayInterval = 3000;
    private ViewPager mViewPager;
    private BannerAdapter bannerAdapter;
    private TextView mTitle;
    private ImageView[] mIndicator;
    private int mBannerPosition = 0;
    private View root;
    private Context mContext;
    private List<Story> mTopStoryList;

    private Handler mPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBannerPosition = (mBannerPosition + 1) % FAKE_BANNER_SIZE;
            if (mBannerPosition == FAKE_BANNER_SIZE - 1) {
                mViewPager.setCurrentItem(DEFAULT_BANNER_SIZE - 1, false);
            } else {
                mViewPager.setCurrentItem(mBannerPosition);
            }
            mPlayHandler.sendEmptyMessageDelayed(MSG_PLAY, mPlayInterval);
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mBannerPosition = position;
            setIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private boolean mIsPlaying = false;

    public Banner(Context context) {
        super(context);
        this.mContext = context;
        init(null, 0);
    }


    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs, 0);
    }


    public Banner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init(attrs, defStyle);
    }

    private void setIndicator(int position) {
        position %= DEFAULT_BANNER_SIZE;
        //遍历mIndicator重置src为normal
        for (ImageView indicator : mIndicator) {
            indicator.setImageResource(R.drawable.indicator_normal);
        }
        mIndicator[position].setImageResource(R.drawable.indicator_focused);
        mTitle.setText(mTopStoryList.get(position).title);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        root = LayoutInflater.from(mContext).inflate(R.layout.view_banner, null);
        addView(root, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        initView();
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    }


    private void initView() {
        mIndicator = new ImageView[]{
                (ImageView) root.findViewById(R.id.indicator1),
                (ImageView) root.findViewById(R.id.indicator2),
                (ImageView) root.findViewById(R.id.indicator3),
                (ImageView) root.findViewById(R.id.indicator4),
                (ImageView) root.findViewById(R.id.indicator5),
        };
        //view
        mViewPager = (ViewPager) root.findViewById(R.id.view_pager);
        mTitle = (TextView) root.findViewById(R.id.title);
        //Touch
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        stopPlay();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        startPlay();
                        break;
                }
                return false;
            }
        });

    }

    public void handleAdapter(List<Story> topStoryList) {
        mTopStoryList = topStoryList;
        bannerAdapter = new BannerAdapter(mContext);
        mViewPager.setAdapter(bannerAdapter);
        startPlay();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPlay();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            startPlay();
        } else if (visibility == INVISIBLE) {
            stopPlay();
        }
    }

    public void startPlay() {
        if (!mIsPlaying) {
            mIsPlaying = true;
            mPlayHandler.sendEmptyMessageDelayed(MSG_PLAY, mPlayInterval);
        }
    }

    public void stopPlay() {
        if (mIsPlaying) {
            mIsPlaying = false;
            mPlayHandler.removeMessages(MSG_PLAY);
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startPlay();
    }

    private class BannerAdapter extends PagerAdapter {
        private Context context;

        public BannerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position %= DEFAULT_BANNER_SIZE;
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item, container, false);
            ImageView image = (ImageView) view.findViewById(R.id.image);
            Picasso.with(context).load(mTopStoryList.get(position).image).placeholder(R.drawable.ic_banner_default).fit().centerCrop().into(image);
            final int pos = position;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPrefsUtils.setStringPreference(context, Settings.ZHIHU_NEWS_VIEWPAGER_COUNT_JSON, renderJson(mTopStoryList));
                    NewsActivity.showNews(context, mTopStoryList.get(pos).id);
                }
            });
            container.addView(view);
            return view;
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
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return FAKE_BANNER_SIZE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            int position = mViewPager.getCurrentItem();
            if (position == 0) {
                position = DEFAULT_BANNER_SIZE;
                mViewPager.setCurrentItem(position, false);
            } else if (position == FAKE_BANNER_SIZE - 1) {
                position = DEFAULT_BANNER_SIZE - 1;
                mViewPager.setCurrentItem(position, false);
            }
        }
    }
}
