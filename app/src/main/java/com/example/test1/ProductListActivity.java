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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.test1.adapter.ProductAdapter;
import com.example.test1.R;
import com.example.test1.dao.ProductDAO;
import com.example.test1.entity.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private static final String TAG = "ProductList";
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> fullProductList;
    private ProductDAO productDAO;
    private TextView cartBadge;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Initialize ProductDAO first
        productDAO = new ProductDAO(this);
        Log.d(TAG, "ProductDAO initialized");

        // Now safe to call insertSampleProducts
        productDAO.insertSampleProducts(this);
        Log.d(TAG, "Sample products inserted");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_bag);
        Log.d(TAG, "onCreate called");

        recyclerView = findViewById(R.id.recyclerViewProducts);
        fullProductList = productDAO.getAllProducts();
        if (fullProductList == null) fullProductList = new ArrayList<>();

        productAdapter = new ProductAdapter(fullProductList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(productAdapter);

        SearchView searchView = findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterProducts(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterProducts(newText);
                    return true;
                }
            });
        }

        cartBadge = findViewById(R.id.cartBadge);
        updateCartCount();

        ImageButton cartButton = findViewById(R.id.cartButton);
        if (cartButton != null) {
            cartButton.setOnClickListener(v -> startActivity(new Intent(this, ShoppingCartActivity.class)));
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_favorite) {
                startActivity(new Intent(ProductListActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.bottom_bell) {
                Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void filterProducts(String query) {
        if (fullProductList == null || fullProductList.isEmpty()) {
            productAdapter.updateList(new ArrayList<>());
            return;
        }

        List<Product> filteredList = new ArrayList<>();
        for (Product product : fullProductList) {
            if (product.getProductName().toLowerCase().contains(query.toLowerCase().trim())) {
                filteredList.add(product);
            }
        }
        productAdapter.updateList(filteredList);
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
        startActivity(new Intent(ProductListActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
    }
}