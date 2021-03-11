package com.jihoo.watcha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jihoo.watcha.util.Helper;

import java.util.ArrayList;

public class TrendingAdapter extends RecyclerView.Adapter<CustomViewHolder> {
    // list에 들어가는 component들
    private final ArrayList<String> gifUrls;
    private final Context context;

    public TrendingAdapter(ArrayList<String> gifUrls, Context context) {
        this.gifUrls = gifUrls;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        // Glide option
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.white_background);

        Glide.with(context)
                .asGif()
                .load(gifUrls.get(position))
                .apply(requestOptions)
                .error(R.drawable.white_background)
                .into(holder.mGifImgView);

        // 라이크된 GIF들의 하트를 활성화 시킨다
        ArrayList<String> urls = Helper.getSavedUrls();
        String splittedUrl = Helper.splitString(gifUrls.get(position));
        if(urls.contains(splittedUrl)) {
            holder.mFavBtn.setChecked(true);
        }

        holder.mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> favUrls = Helper.getSavedUrls();

                // 좋아요 취소할 때
                if(!holder.mFavBtn.isChecked()) {
                    // delete the url from the sharedpreferences
                    favUrls.remove(splittedUrl);
                }

                // 좋아요를 누를때
                else {
                    // save the url to the sharedpreferences
                    favUrls.add(splittedUrl);
                }

                Helper.addUrl(favUrls);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(gifUrls != null) {
            return gifUrls.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
