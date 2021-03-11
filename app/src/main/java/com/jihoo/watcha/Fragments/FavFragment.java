package com.jihoo.watcha.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jihoo.watcha.FavouriteAdapter;
import com.jihoo.watcha.R;
import com.jihoo.watcha.util.Helper;

import java.util.ArrayList;

public class FavFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final int NUM_COLUMNS = 2;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favourite_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.favourite_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.fav_swipe_container);

        // SwipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_pink,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        initRecyclerView();
    }

    private void initRecyclerView() {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        ArrayList<String> gifUrls = Helper.getSavedUrls();

        FavouriteAdapter favouriteAdapter = new FavouriteAdapter(gifUrls, getContext());
        mRecyclerView.setAdapter(favouriteAdapter);

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        initRecyclerView();
    }
}
