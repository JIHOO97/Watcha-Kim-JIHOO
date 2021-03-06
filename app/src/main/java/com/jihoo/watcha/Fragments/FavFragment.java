package com.jihoo.watcha.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jihoo.watcha.R;
import com.jihoo.watcha.VideoPlayerViewAdapter;

import org.json.simple.parser.ParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FavFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final int NUM_COLUMNS = 2;
    private static final String TAG = "simpleTag";

    private RecyclerView mRecyclerView;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favourite_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.favourite_recycler_view);
        sharedPreferences = getContext().getSharedPreferences("giphy", Context.MODE_PRIVATE);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.fav_swipe_container);

        // SwipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_pink,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                // Fetching data from server
                try {
                    initRecyclerView();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            initRecyclerView();
        } catch (ParseException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() throws ParseException, InterruptedException {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        ArrayList<String> gifUrls = getSavedUrls();

        VideoPlayerViewAdapter videoPlayerViewAdapter = new VideoPlayerViewAdapter(gifUrls, getContext());
        mRecyclerView.setAdapter(videoPlayerViewAdapter);

        mSwipeRefreshLayout.setRefreshing(false);
    }

    public ArrayList<String> getSavedUrls() {
        ArrayList<String> urls = new ArrayList<>();
        // get saved urls
        Gson gson = new Gson();
        String json = sharedPreferences.getString("urls", "");
        if(!json.isEmpty()) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            urls = gson.fromJson(json, type);
        }
        return urls;
    }

    @Override
    public void onRefresh() {
        try {
            initRecyclerView();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
