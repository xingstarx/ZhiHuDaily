package com.example.star.zhihudaily.widget;

import android.support.annotation.LayoutRes;

import com.example.star.zhihudaily.R;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

public class CustomPrimaryDrawerItem extends PrimaryDrawerItem {

    @Override
    @LayoutRes
    public int getLayoutRes() {
        return R.layout.custom_material_drawer_item_primary;
    }

}
