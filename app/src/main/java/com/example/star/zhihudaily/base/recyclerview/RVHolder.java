package com.example.star.zhihudaily.base.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by xiongxingxing on 15/12/6.
 */
public class RVHolder  extends RecyclerView.ViewHolder {


    private ViewHolder viewHolder;

    public RVHolder(View itemView) {
        super(itemView);
        viewHolder=ViewHolder.getViewHolder(itemView);
    }


    public ViewHolder getViewHolder() {
        return viewHolder;
    }

}