package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.adapter.CategoryAdapter;
import com.example.test1.dao.CategoryDAO;
import com.example.test1.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    private static final String TAG = "CategoriesActivity";
    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private CategoryDAO categoryDAO;
    private ImageButton categoriesCartIcon;
    private TextView categoriesCartBadge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        Log.d(TAG, "onCreate called");

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        if (categoriesRecyclerView == null) {
            Log.e(TAG, "RecyclerView not found in activity_categories.xml");
            Toast.makeText(this, "RecyclerView not found", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.d(TAG, "RecyclerView initialized successfully");
        }

        // Initialize cart icon, badge, and cart
        categoriesCartIcon = findViewById(R.id.categoriesCartIcon);
        categoriesCartBadge = findViewById(R.id.categoriesCartBadge);


        categoryDAO = new CategoryDAO(this);
        // Insert sample categories for testing
        categoryDAO.insertSampleCategories(this);

        // Load all categories
        categoryList = categoryDAO.getAllCategories();
        if (categoryList == null) {
            categoryList = new ArrayList<>();
        }

        Log.d(TAG, "Category List Size: " + (categoryList != null ? categoryList.size() : "null"));

        if (categoryList.isEmpty()) {
            Log.w(TAG, "Category List is empty, adding manual data");
            categoryList.add(new Category(1, "Electronics", "Electronic devices and gadgets"));
            categoryList.add(new Category(2, "Clothing", "Fashion and apparel"));
            Log.d(TAG, "Added manual sample data, Category List Size: " + categoryList.size());
        }

        categoryAdapter = new CategoryAdapter(categoryList);
        if (categoryAdapter == null) {
            Log.e(TAG, "CategoryAdapter is null");
        } else {
            Log.d(TAG, "CategoryAdapter initialized with " + categoryList.size() + " items");
        }

        // Use GridLayoutManager for a 3-column grid
        categoriesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        Log.d(TAG, "GridLayoutManager set with 3 columns");
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // Set up SearchView
        SearchView searchView = findViewById(R.id.categoriesSearchView);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d(TAG, "Search query submitted: " + query);
                    filterCategories(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.d(TAG, "Search query changing: " + newText);
                    filterCategories(newText);
                    return true;
                }
            });
            Log.d(TAG, "SearchView initialized successfully");
        } else {
            Log.e(TAG, "SearchView not found in activity_categories.xml");
        }

        categoriesCartBadge = findViewById(R.id.categoriesCartBadge);
        categoriesCartIcon = findViewById(R.id.categoriesCartIcon);
        if (categoriesCartIcon != null) {
            categoriesCartIcon.setOnClickListener(v -> {
                startActivity(new Intent(this, ShoppingCartActivity.class));
            });
        }
        updateCartCount();
    }


    public void goBack(View view) {
        Log.d(TAG, "Back button clicked, navigating back");
        onBackPressed(); // Call the default back press behavior
    }

    private void filterCategories(String query) {
        Log.d(TAG, "Filtering categories with query: " + query);
        if (categoryList == null || categoryList.isEmpty()) {
            Log.w(TAG, "Category list is empty or null, cannot filter");
            categoryList.clear();
            categoryAdapter.notifyDataSetChanged();
            return;
        }

        List<Category> filteredList = new ArrayList<>();
        for (Category category : categoryList) {
            if (category != null && category.getCategoryName() != null &&
                    category.getCategoryName().toLowerCase().contains(query.toLowerCase().trim())) {
                filteredList.add(category);
            }
        }

        categoryList.clear();
        categoryList.addAll(filteredList);
        categoryAdapter.notifyDataSetChanged();
        Log.d(TAG, "Filtered category list size: " + filteredList.size());
    }
    public void updateCartCount() {
        int count = ShoppingCartManager.getInstance().getCartItemCount();
        if (categoriesCartBadge != null) {
            categoriesCartBadge.setText(String.valueOf(count));
            categoriesCartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }
}
