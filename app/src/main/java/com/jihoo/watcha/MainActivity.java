package com.jihoo.watcha;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jihoo.watcha.Fragments.FavFragment;
import com.jihoo.watcha.Fragments.TrendingFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "simpleTag";
    private static final String trendingFragTag = "trendingFragment";
    private static final String favFragTag = "favouriteFragment";

    private boolean isTrendingFragVisible = true;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;
    private TrendingFragment trendingFragment = new TrendingFragment();
    private FavFragment favouriteFragment = new FavFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_RecyclerView);  // for splash screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // customize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add icon to the top left part of the action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.giphy_icon);

        // customize bottom navigation view
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        sharedPreferences = getSharedPreferences("giphy", Context.MODE_PRIVATE);
        String currentItem = sharedPreferences.getString("curSelectedItem", "");
        if(currentItem.equals("favouriteFragment")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, favouriteFragment, favFragTag).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, trendingFragment, trendingFragTag).commit();
        }
    }

    // navigate bottomnavigationview when selected (trend or fav)
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    TrendingFragment trendingFragment = (TrendingFragment) getSupportFragmentManager().findFragmentByTag(trendingFragTag);

                    switch (item.getItemId()) {
                        case R.id.bottom_nav_trending:
                            if(trendingFragment != null && trendingFragment.isVisible()) return true;
                            addCurrentNavigationItem("trendingFragment");
                            isTrendingFragVisible = true;
                            selectedFragment = new TrendingFragment();
                            break;
                        case R.id.bottom_nav_heart:
                            if(trendingFragment == null) return true;
                            addCurrentNavigationItem("favouriteFragment");
                            isTrendingFragVisible = false;
                            selectedFragment = new FavFragment();
                            break;
                    }

                    if(isTrendingFragVisible) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, trendingFragTag).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, favFragTag).commit();
                    }

                    return true; // we want to show the selected tab as selected
                }
            };

    private void addCurrentNavigationItem(String curItem) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("curSelectedItem", curItem);
        editor.apply();
    }
}
