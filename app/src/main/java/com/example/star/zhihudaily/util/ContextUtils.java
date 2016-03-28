package com.example.star.zhihudaily.util;

import android.content.Context;

/**
 * Created by xiongxingxing on 16/3/27.
 */
public class ContextUtils {

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
