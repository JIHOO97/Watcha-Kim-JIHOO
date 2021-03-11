package com.jihoo.watcha.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jihoo.watcha.R;
import com.jihoo.watcha.TrendingAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrendingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String apiUrl = "https://api.giphy.com/v1/gifs/trending?api_key=erNnDilHipb9HQaxIXI1i6piBe4FVzu6&limit=1000";
    private static final int NUM_COLUMNS = 2;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<String> urls;
    private ProgressBar mLoadingBar;
    private RequestQueue mQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.trending_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.trending_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.trending_swipe_view);
        mLoadingBar = (ProgressBar) getView().findViewById(R.id.loading_bar);

        // SwipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_pink,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        urls = new ArrayList<>();

        // first add redundant adapter
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        TrendingAdapter trendingAdapter = new TrendingAdapter(urls, getActivity());
        mRecyclerView.setAdapter(trendingAdapter);

        mQueue = Volley.newRequestQueue(getActivity());
        init();
    }

    private void init() {
        // make a volley request
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, apiUrl, null,
                new com.android.volley.Response.Listener<org.json.JSONObject>() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");

                            for(int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject tempObject = jsonArray.getJSONObject(i);
                                JSONObject imagesObject = tempObject.getJSONObject("images");
                                JSONObject urlObject = imagesObject.getJSONObject("fixed_height_downsampled");
                                String gifUrl = urlObject.getString("url");
                                urls.add(gifUrl);
                            }
                            initRecyclerView(urls);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    private void initRecyclerView(ArrayList<String> urls) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        TrendingAdapter trendingAdapter = new TrendingAdapter(urls, getActivity());
        mRecyclerView.setAdapter(trendingAdapter);

        mSwipeRefreshLayout.setRefreshing(false);
        mLoadingBar.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        initRecyclerView(urls);
    }
}
