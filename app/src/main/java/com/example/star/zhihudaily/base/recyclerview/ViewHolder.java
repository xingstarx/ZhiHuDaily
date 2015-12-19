package com.example.star.zhihudaily.base.recyclerview;

import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xiongxingxing on 15/12/6.
 */
public class ViewHolder {
    private SparseArray<View> viewHolder;
    private View view;

    public static  ViewHolder getViewHolder(View view){
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        return viewHolder;
    }
    private ViewHolder(View view) {
        this.view = view;
        viewHolder = new SparseArray<>();
        view.setTag(viewHolder);
    }
    public <T extends View> T get(int id) {
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

    public View getConvertView() {
        return view;
    }

    public TextView getTextView(int id) {

        return get(id);
    }
    public Button getButton(int id) {

        return get(id);
    }

    public ImageView getImageView(int id) {
        return get(id);
    }

    public void setTextView(int  id,CharSequence charSequence){
        getTextView(id).setText(charSequence);
    }

}
