package com.jihoo.watcha.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jihoo.watcha.R;
import com.jihoo.watcha.VideoPlayerViewAdapter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrendingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String apiUrl = "https://api.giphy.com/v1/gifs/trending?api_key=erNnDilHipb9HQaxIXI1i6piBe4FVzu6&limit=1000";
    private static final int NUM_COLUMNS = 2;
    private static final String TAG = "simpleTag";

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<String> urls;
    private ProgressBar mLoadingBar;

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

        FetchAPI api = new FetchAPI(this);
        api.execute(apiUrl);
    }



    private void initRecyclerView(ArrayList<String> urls) throws ParseException, InterruptedException {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        VideoPlayerViewAdapter videoPlayerViewAdapter = new VideoPlayerViewAdapter(urls, getActivity());
        mRecyclerView.setAdapter(videoPlayerViewAdapter);

        mSwipeRefreshLayout.setRefreshing(false);
        mLoadingBar.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        try {
            initRecyclerView(urls);
        } catch (ParseException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // fetch url using GIPHY API
    public static class FetchAPI extends AsyncTask<String, String, ArrayList<String>> {
        // to prevent memory leak
        private final WeakReference<TrendingFragment> activityWeakReference;

        FetchAPI(TrendingFragment activity) {
            activityWeakReference = new WeakReference<TrendingFragment>(activity);
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            TrendingFragment activity = activityWeakReference.get();
            if(activity == null) return null;
            activity.urls = new ArrayList<>();

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(strings[0])
                    .build();

            String resBody = null;

            try(Response response = client.newCall(request).execute()) {
                resBody = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = null;
            try {
                jsonObject = (JSONObject) parser.parse(resBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONArray resArray = (JSONArray) jsonObject.get("data");
            for(int i = 0; i < resArray.size(); ++i) {
                JSONObject tempObject = (JSONObject) resArray.get(i);
                JSONObject imagesObject = (JSONObject) tempObject.get("images");
                JSONObject originalObject = (JSONObject) imagesObject.get("fixed_height_downsampled");
                String gifUrl = originalObject.get("url").toString();
                activity.urls.add(gifUrl);
            }

            return activity.urls;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);

            TrendingFragment activity = activityWeakReference.get();
            if(activity == null) return;
            try {
                activity.initRecyclerView(activity.urls);
            } catch (ParseException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
