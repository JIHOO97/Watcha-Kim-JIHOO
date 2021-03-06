package com.jihoo.watcha;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jihoo.watcha.Fragments.TrendingFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class VideoPlayerViewAdapter extends RecyclerView.Adapter<VideoPlayerViewHolder> {

    private static final String TAG = "simpleTag";
    private static final String trendingFragTag = "trendingFragment";

    // list에 들어가는 component들
    private ArrayList<String> gifUrls;
    private Context context;
    private SharedPreferences sharedPreferences;

    public VideoPlayerViewAdapter(ArrayList<String> gifUrls, Context context) {
        this.gifUrls = gifUrls;
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("giphy", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public VideoPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder, viewType is: " + viewType);
        return new VideoPlayerViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VideoPlayerViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder, cur position is: " + position);

        // Glide option
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.white_background);

        Glide.with(context)
                .asGif()
                .load(gifUrls.get(position))
                .apply(requestOptions)
                .error(R.drawable.white_background)
                .into(holder.mGifImgView);

        // 라이크된 GIF들의 하트를 활성화 시킨다
        ArrayList<String> urls = getSavedUrls();
        String splittedUrl = splitString(gifUrls.get(position));
        if(urls.contains(splittedUrl)) {
            holder.mFavBtn.setChecked(true);
        }

        holder.mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curGifUrl = gifUrls.get(position);
                String splittedCurGifUrl = splitString(curGifUrl);  // split the url excluding cid
                ArrayList<String> urls = getSavedUrls();  // fetch the saved urls from sharedpreferences

                // 좋아요 취소할 때
                if(!holder.mFavBtn.isChecked()) {
                    // delete the url from the sharedpreferences
                    urls.remove(splittedCurGifUrl);

                    // Favourite Fragment일 때, selected item을 없앤다
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    Fragment trendingFragment = fragmentManager.findFragmentByTag(trendingFragTag);
                    Log.d(TAG, trendingFragment + "");
                    if(trendingFragment == null) {
                        removeItem(holder.getAdapterPosition());
                    }
                }

                // 좋아요를 누를때
                else {
                    // save the url to the sharedpreferences
                    urls.add(splittedCurGifUrl);
                }
                addUrl(urls);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gifUrls.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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

    public void addUrl(ArrayList<String> urls) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("urls", "");
        gson = new Gson();
        json = gson.toJson(urls);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("urls", json);
        editor.apply();
    }

    public String splitString(String url) {
        String[] results = url.split("\\?");
        return results[0];
    }

    public void removeItem(int position) {
        try {
            gifUrls.remove(position);
            notifyItemRemoved(position);
        } catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
