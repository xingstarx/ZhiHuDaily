package com.example.star.zhihudaily;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class NewsFragment extends Fragment {
    public static final String ARG_ID = "id";
    private static final String TAG = "NewsFragment";
    private String id;

    public NewsFragment() {
        Log.e(TAG, "NewsFragment()"+this.toString());
    }


    public static NewsFragment newInstance(String id) {
        NewsFragment fragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreate"+this.toString());
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView"+this.toString());
        View root = inflater.inflate(R.layout.fragment_news, container, false);
        TextView textView = (TextView) root.findViewById(R.id.text);
        View containerView = root.findViewById(R.id.container);
        int result = (int) (Math.random() * 256);
        containerView.setBackgroundColor(Color.rgb(result, result, result));
        textView.setText("hello_blank_fragment" + id);
        return root;
    }

    @Override
    public void onDestroyView() {
        Log.e(TAG, "onDestroyView"+this.toString());
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy"+this.toString());
        super.onDestroy();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume"+this.toString());
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause"+this.toString());
        super.onPause();
    }
}
