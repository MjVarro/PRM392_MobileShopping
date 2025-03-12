package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.test1.adapter.CategoryAdapter;
import com.example.test1.dao.CategoryDAO;
import com.example.test1.entity.Category;
import com.example.test1.manager.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    private static final String TAG = "CategoriesActivity";
    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private List<Category> fullCategoryList;
    private CategoryDAO categoryDAO;
    private ImageButton backBtn;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        Log.d(TAG, "onCreate called");

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize views
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        backBtn = findViewById(R.id.backBtn);

        if (categoriesRecyclerView == null) {
            Log.e(TAG, "RecyclerView not found in activity_categories.xml");
            Toast.makeText(this, "RecyclerView not found", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.d(TAG, "RecyclerView initialized successfully");
        }

        // Set up Back button
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> goBackToMain());
        }

        // Initialize CategoryDAO and load categories
        categoryDAO = new CategoryDAO(this);
        categoryDAO.insertSampleCategories(this);

        // Load all categories
        fullCategoryList = categoryDAO.getAllCategories();
        if (fullCategoryList == null) {
            fullCategoryList = new ArrayList<>();
        }

        Log.d(TAG, "Full Category List Size: " + fullCategoryList.size());

        // Initialize categoryList with a copy of fullCategoryList
        categoryList = new ArrayList<>(fullCategoryList);

        if (categoryList.isEmpty()) {
            Log.w(TAG, "Category List is empty, adding manual data");
            categoryList.add(new Category(1, "Electronics", "Electronic devices and gadgets"));
            categoryList.add(new Category(2, "Clothing", "Fashion and apparel"));
            fullCategoryList.addAll(categoryList);
            Log.d(TAG, "Added manual sample data, Category List Size: " + categoryList.size());
        }

        // Initialize CategoryAdapter with callback
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            Intent intent = new Intent(CategoriesActivity.this, ProductListActivity.class);
            intent.putExtra("categoryId", category.getCategoryId());
            startActivity(intent);
        });

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
    }

    // Thêm menu vào Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Xử lý sự kiện khi chọn item trong menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_user_profile) {
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else {
                Toast.makeText(this, "Please log in to view your profile", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
            return true;
        } else if (itemId == R.id.menu_categories) {
            startActivity(new Intent(this, CategoriesActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.menu_home){
            startActivity(new Intent(this, MainActivity.class));
            finish();

        } else if (itemId == R.id.menu_cart) {
            startActivity(new Intent(this, ShoppingCartActivity.class));
            return true;
        } else if (itemId == R.id.menu_logout) {
            if (sessionManager.isLoggedIn()) {
                // sessionManager.logoutUser();
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goBackToMain() {
        Log.d(TAG, "Back button clicked, navigating to MainActivity");
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "Back pressed, navigating to MainActivity");
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void filterCategories(String query) {
        Log.d(TAG, "Filtering categories with query: " + query);
        query = query.trim().toLowerCase();
        categoryList.clear();
        if (query.isEmpty()) {
            categoryList.addAll(fullCategoryList);
        } else {
            for (Category category : fullCategoryList) {
                if (category != null && category.getCategoryName() != null &&
                        category.getCategoryName().toLowerCase().contains(query)) {
                    categoryList.add(category);
                }
            }
        }
        categoryAdapter.notifyDataSetChanged();
        if (categoryList.isEmpty()) {
            Toast.makeText(this, "No categories found", Toast.LENGTH_SHORT).show();
        }
    }
}