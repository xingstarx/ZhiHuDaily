package com.example.star.zhihudaily;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.star.zhihudaily.base.adapter.ListBaseAdapter;

import java.util.ArrayList;

/**
 * Created by xiongxingxing on 15/10/26.
 */
public class DrawMenuFragment extends Fragment {
    private View rootView;
    private Context context;
    private ListView mListView;
    private DrawAdapter drawAdapter;


    public static DrawMenuFragment newInstance() {
        Bundle args = new Bundle();
        DrawMenuFragment fragment = new DrawMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_drawer_view, null);
        initViews();
        return rootView;
    }

    private void initViews() {
        mListView = (ListView) rootView.findViewById(R.id.listview);
        ArrayList<Draw> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Draw draw = new Draw();
            draw.name = "item " + (i + 1);
            draw.resId = R.drawable.ic_menu_arrow;
        }
        drawAdapter = new DrawAdapter(context, dataList, R.layout.draw_layout_item);
        mListView.setAdapter(drawAdapter);
    }

    public void notifyDataSetChanged() {
        drawAdapter.notifyDataSetChanged();
    }

    public class Draw {
        public String name;
        public int resId;
    }

    public class DrawAdapter extends ListBaseAdapter<Draw> {

        public DrawAdapter(Context ctx, ArrayList<Draw> dataList, int theRowResourceId) {
            super(ctx, dataList, theRowResourceId);
        }

        @Override
        public void prepareViewForDisplay(View view, Draw dataItem) {
            TextView textView = (TextView) view.findViewById(R.id.draw_item_text);
            ImageView imageView = (ImageView) view.findViewById(R.id.draw_item_icon);
            textView.setText(dataItem.name);
            imageView.setImageResource(dataItem.resId);
        }
    }
}
