package com.example.star.zhihudaily.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.star.zhihudaily.R;

public class CustomMenuView extends LinearLayout {

    private ImageView mImageView;
    private TextView mTextView;

    public CustomMenuView(Context context) {
        super(context);
        init(context, null);
    }

    public CustomMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.menu_custom_view, this, true);
        mImageView = (ImageView) findViewById(R.id.image);
        mTextView = (TextView) findViewById(R.id.title);
    }

    public CustomMenuView setTextValue(String value) {
        mTextView.setText(value);
        return this;
    }

    public CustomMenuView setImage(@DrawableRes int resId) {
        mImageView.setImageResource(resId);
        return this;
    }

    public CustomMenuView updateMenuTansparency(float scrollRatio) {
        mImageView.setAlpha(scrollRatio);
        mTextView.setAlpha(scrollRatio);
        return this;
    }
    public CustomMenuView hideText () {
        mTextView.setVisibility(INVISIBLE);
        return this;
    }
}
