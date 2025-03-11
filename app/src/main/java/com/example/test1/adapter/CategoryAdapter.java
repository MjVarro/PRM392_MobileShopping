package com.example.test1.adapter;


import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.MainActivity;
import com.example.test1.R;
import com.example.test1.entity.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private static final String TAG = "CategoryAdapter";
    private List<Category> categoryList;

    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
        Log.d(TAG, "CategoryAdapter initialized with " + (categoryList != null ? categoryList.size() : "null") + " items");
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        Log.d(TAG, "Creating ViewHolder for viewType: " + viewType);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        if (categoryList != null && position < categoryList.size()) {
            Category category = categoryList.get(position);
            Log.d(TAG, "Binding category at position " + position + ": " + (category != null ? category.getCategoryName() : "null"));
            if (category != null) {
                holder.textCategoryName.setText(category.getCategoryName());
                holder.textCategoryDescription.setText(category.getCategoryDescription());
                // Optional: Add an icon or image if needed (e.g., for category)
                // holder.categoryImage.setImageResource(R.drawable.some_icon);

                // Set click listener to navigate to MainActivity with category ID
                holder.itemView.setOnClickListener(v -> {
                    Log.d(TAG, "Category clicked at position " + position + ", ID: " + category.getCategoryId());
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    intent.putExtra("CATEGORY_ID", category.getCategoryId());
                    v.getContext().startActivity(intent);
                    Log.d(TAG, "Navigating to MainActivity for category ID: " + category.getCategoryId());
                });
            } else {
                Log.e(TAG, "Category at position " + position + " is null, skipping rendering");
                holder.textCategoryName.setText("No Category");
                holder.textCategoryDescription.setText("No description");
            }
        } else {
            Log.e(TAG, "Invalid position or null categoryList at position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        int count = categoryList != null ? categoryList.size() : 0;
        Log.d(TAG, "getItemCount returning: " + count);
        return count;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textCategoryName, textCategoryDescription;
        ImageView categoryImage; // Optional for category icons

        public CategoryViewHolder(View itemView) {
            super(itemView);
            textCategoryName = itemView.findViewById(R.id.textCategoryName);
            textCategoryDescription = itemView.findViewById(R.id.textCategoryDescription);
            categoryImage = itemView.findViewById(R.id.categoryImage); // Optional
            Log.d("CategoryViewHolder", "ViewHolder created for item");
            itemView.setClickable(true);
            itemView.setFocusable(true);
        }
    }
}
