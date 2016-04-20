package com.example.star.zhihudaily.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.star.zhihudaily.R;

public abstract class SingleFragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Fragment fragment = onCreatePage();

            Bundle arguments = new Bundle();
            if (getIntent().getExtras() != null) {
                arguments.putAll(getIntent().getExtras());
                fragment.setArguments(arguments);
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    public abstract Fragment onCreatePage();

}
