package com.jihoo.watcha;

import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// RecyclerView가 가지고 있는 list에 들어가는 Views
public class CustomViewHolder extends RecyclerView.ViewHolder {

    ImageView mGifImgView;
    ToggleButton mFavBtn;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
        mGifImgView = (ImageView) itemView.findViewById(R.id.gifImgView);
        mFavBtn = (ToggleButton) itemView.findViewById(R.id.fav_btn);
    }
}
