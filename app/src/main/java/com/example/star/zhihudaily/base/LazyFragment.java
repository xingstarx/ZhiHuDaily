package com.example.star.zhihudaily.base;

import android.support.v4.app.Fragment;

/**
 * Created by xiongxingxing on 16/5/2.
 */
public abstract class LazyFragment extends Fragment{

    protected boolean isVisible = false;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible(){
        lazyLoad();
    }
    protected abstract void lazyLoad();
    protected void onInvisible(){}
}
