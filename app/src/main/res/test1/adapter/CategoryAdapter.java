package com.example.test1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.entity.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categoryList;
    private final Consumer<Category> onCategoryClick; // Callback khi nhấn vào category

    // Constructor với callback
    public CategoryAdapter(List<Category> categoryList, Consumer<Category> onCategoryClick) {
        this.categoryList = (categoryList != null) ? categoryList : new ArrayList<>(); // Khởi tạo danh sách rỗng nếu null
        this.onCategoryClick = onCategoryClick;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false); // Giả định có layout item_category.xml
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        if (category != null) { // Kiểm tra null để tránh lỗi
            holder.categoryName.setText(category.getCategoryName() != null ? category.getCategoryName() : "Unnamed Category");
            holder.itemView.setOnClickListener(v -> {
                if (onCategoryClick != null) {
                    onCategoryClick.accept(category); // Gọi callback khi nhấn
                }
            });
        } else {
            holder.categoryName.setText("Invalid Category"); // Xử lý trường hợp category null
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size(); // Đã kiểm tra null ở constructor, nên không cần kiểm tra lại
    }

    // Cập nhật danh sách category và làm mới RecyclerView
    public void updateList(List<Category> newList) {
        this.categoryList = (newList != null) ? newList : new ArrayList<>(); // Khởi tạo danh sách rỗng nếu null
        notifyDataSetChanged();
    }

    // ViewHolder để giữ các view của item
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName); // Giả định có TextView trong item_category.xml
            if (categoryName == null) {
                throw new IllegalStateException("TextView with ID 'categoryName' not found in item_category.xml");
            }
        }
    }
}