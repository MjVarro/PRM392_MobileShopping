package com.example.test1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.Model.SliderModel;
import com.example.test1.R;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private List<SliderModel> msliderModels;

    public SliderAdapter(List<SliderModel> msliderModels) {
        this.msliderModels = msliderModels;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_image_container, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        SliderModel sliderModel = msliderModels.get(position);
        holder.imageSlide.setImageResource(sliderModel.getUrl());
    }

    @Override
    public int getItemCount() {
        return msliderModels != null ? msliderModels.size() : 0;
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageSlide;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSlide = itemView.findViewById(R.id.imageSlide);
        }
    }
}