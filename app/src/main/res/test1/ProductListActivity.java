package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.adapter.ProductAdapter;
import com.example.test1.dao.ProductDAO;
import com.example.test1.entity.Product;
import com.example.test1.manager.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private static final String TAG = "ProductList";
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> fullProductList;
    private ProductDAO productDAO;
    private TextView cartBadge;
    private ImageButton backButton;
    private SessionManager sessionManager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);


        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize ProductDAO
        productDAO = new ProductDAO(this);
        Log.d(TAG, "ProductDAO initialized");

        // Insert sample products (đảm bảo dữ liệu được thêm)
        productDAO.insertSampleProducts(this);
        Log.d(TAG, "Sample products inserted");

        // Nhận categoryId từ Intent
        Intent intent = getIntent();
        int categoryId = intent.getIntExtra("categoryId", -1);
        Log.d(TAG, "Received categoryId: " + categoryId);

        // Load all products
        fullProductList = productDAO.getAllProducts();
        if (fullProductList == null || fullProductList.isEmpty()) {
            Log.w(TAG, "Product list is null or empty");
            fullProductList = new ArrayList<>();
            Toast.makeText(this, "No products available", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Total products loaded: " + fullProductList.size());
        }

        // Lọc sản phẩm theo categoryId
        List<Product> filteredProductList = filterProductsByCategory(categoryId);
        Log.d(TAG, "Filtered products for categoryId " + categoryId + ": " + filteredProductList.size());
        if (filteredProductList.isEmpty() && categoryId != -1) {
            Toast.makeText(this, "No products found for this category", Toast.LENGTH_SHORT).show();
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProducts);
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView not found in layout");
            Toast.makeText(this, "RecyclerView not found", Toast.LENGTH_SHORT).show();
            return;
        }
        productAdapter = new ProductAdapter(filteredProductList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(productAdapter);

        // Initialize SearchView
        SearchView searchView = findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterProducts(query, categoryId);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterProducts(newText, categoryId);
                    return true;
                }
            });
        } else {
            Log.w(TAG, "SearchView not found in layout");
        }

        // Initialize Cart Badge
        cartBadge = findViewById(R.id.cartBadge);
        updateCartCount();

        // Initialize Back Button
        backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                startActivity(new Intent(ProductListActivity.this, CategoriesActivity.class));
                finish();
            });
        } else {
            Log.w(TAG, "BackButton not found in layout");
        }
    }

    // Lọc sản phẩm theo categoryId
    private List<Product> filterProductsByCategory(int categoryId) {
        List<Product> filteredList = new ArrayList<>();
        if (fullProductList == null || fullProductList.isEmpty()) {
            Log.w(TAG, "No products to filter by category");
            return filteredList;
        }

        if (categoryId == -1) {
            filteredList.addAll(fullProductList); // Hiển thị tất cả nếu không có categoryId
        } else {
            for (Product product : fullProductList) {
                if (product != null && product.getCategoryId() == categoryId) {
                    filteredList.add(product);
                }
            }
        }
        return filteredList;
    }

    // Lọc sản phẩm theo query và categoryId
    private void filterProducts(String query, int categoryId) {
        if (fullProductList == null || fullProductList.isEmpty()) {
            productAdapter.updateList(new ArrayList<>());
            Log.w(TAG, "No products to filter");
            return;
        }

        List<Product> filteredList = new ArrayList<>();
        for (Product product : fullProductList) {
            if (product != null && product.getProductName() != null &&
                    product.getProductName().toLowerCase().contains(query.toLowerCase().trim())) {
                if (categoryId == -1 || product.getCategoryId() == categoryId) {
                    filteredList.add(product);
                }
            }
        }
        productAdapter.updateList(filteredList);
        Log.d(TAG, "Filtered products with query '" + query + "' and categoryId " + categoryId + ": " + filteredList.size());
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No products match your search", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

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
//                sessionManager.logoutUser();
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

    public void updateCartCount() {
        int count = ShoppingCartManager.getInstance().getCartItemCount();
        if (cartBadge != null) {
            cartBadge.setText(String.valueOf(count));
            cartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProductListActivity.this, CategoriesActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
    }
}