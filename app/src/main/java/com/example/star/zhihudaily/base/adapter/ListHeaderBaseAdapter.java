package com.example.star.zhihudaily.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by xiongxingxing on 15/11/15.
 *
 * @desc listview 处理多ItemViewType的
 */
public class ListHeaderBaseAdapter<T> extends ListBaseAdapter<T> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTENT = 1;
    private static final int TYPE_MAX_COUNT = 2;
    protected int headerViewResourceId;

    public ListHeaderBaseAdapter(Context ctx, ArrayList<T> dataList, int theRowResourceId) {
        super(ctx, dataList, theRowResourceId);
    }

    public ListHeaderBaseAdapter(Context ctx, ArrayList<T> dataList, int theRowResourceId, int headerViewResourceId) {
        super(ctx, dataList, theRowResourceId);
        this.headerViewResourceId = headerViewResourceId;
    }

    @Override
    public void prepareViewForDisplay(View view, T dataItem) {

    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return null;
        }
        return this.dataList.get(position - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_CONTENT;
    }

    // Total number of types is the number of enum values
    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    public View getView(int position, View convertView, ViewGroup parentView) {
        View view = null;
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_HEADER:
                if (convertView == null) {
                    view = View.inflate(this.ctx, headerViewResourceId, null);
                } else {
                    view = convertView;
                }
                prepareViewForDisplay(view, null);
                break;
            case TYPE_CONTENT:
                if (convertView == null) {
                    view = View.inflate(this.ctx, this.rowViewResourceId, null);
                } else {
                    view = convertView;
                }
                // to be supplied by subclass
                T dataItem = this.dataList.get(position - 1);
                prepareViewForDisplay(view, dataItem);
                break;
        }
        return view;
    }

}
