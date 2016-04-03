package com.example.star.zhihudaily.widget;

import android.content.Context;
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
import android.widget.Toast;

import com.example.star.zhihudaily.R;
import com.example.star.zhihudaily.api.model.Story;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Banner
 */
public class Banner extends LinearLayout {

    public static final String TAG = "Banner";
    private final int FAKE_BANNER_SIZE = 100;
    private final int DEFAULT_BANNER_SIZE = 5;
    private ViewPager mViewPager;
    private BannerAdapter bannerAdapter;
    private TextView mTitle;
    private ImageView[] mIndicator;
    private int mBannerPosition = 0;
    private boolean mIsUserTouched = false;
    private View root;
    private Context mContext;
    private Timer mTimer = new Timer();
    private List<Story> mTopStoryList;

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (!mIsUserTouched) {
                mBannerPosition = (mBannerPosition + 1) % FAKE_BANNER_SIZE;
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mBannerPosition == FAKE_BANNER_SIZE - 1) {
                            mViewPager.setCurrentItem(DEFAULT_BANNER_SIZE - 1, false);
                        } else {
                            mViewPager.setCurrentItem(mBannerPosition);
                        }
                    }
                });
            }
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
        //第一个5000表示从调用schedule()方法到第一次执行mTimerTask的run()方法的时间间隔
        //第二个5000表示以后每隔5000毫秒执行一次mTimerTask的run()方法
        mTimer.schedule(mTimerTask, 5000, 5000);
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
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    mIsUserTouched = true;
                } else if (action == MotionEvent.ACTION_UP) {
                    mIsUserTouched = false;
                }
                return false;
            }
        });

    }

    public void handleAdapter(List<Story> topStoryList) {
        mTopStoryList = topStoryList;
        bannerAdapter = new BannerAdapter(mContext);
        mViewPager.setAdapter(bannerAdapter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
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
            Picasso.with(context).load(mTopStoryList.get(position).image).placeholder(R.drawable.ic_banner_default).fit().into(image);
            final int pos = position;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, pos + "-->" + mTopStoryList.get(pos).title, Toast.LENGTH_SHORT).show();
                }
            });
            container.addView(view);

            return view;
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
