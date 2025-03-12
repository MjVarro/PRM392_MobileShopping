package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import com.example.test1.dao.ProductDAO;
import com.example.test1.entity.CartItem;
import com.example.test1.entity.Product;
import com.example.test1.manager.SessionManager;
import com.example.test1.ShoppingCartManager;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";
    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    private ImageView detailImageProduct;
    private TextView detailTextProductName, detailTextPrice, detailTextSales;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Log.d(TAG, "onCreate called");

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize views
        detailImageProduct = findViewById(R.id.detailImageProduct);
        detailTextProductName = findViewById(R.id.detailTextProductName);
        detailTextPrice = findViewById(R.id.detailTextPrice);
        detailTextSales = findViewById(R.id.detailTextSales);

        // Initialize SearchView (optional functionality)



        Intent intent = getIntent();
        int productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1);
        if (productId == -1) {
            Log.e(TAG, "Invalid product ID received");
            Toast.makeText(this, "Invalid product selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ProductDAO productDAO = new ProductDAO(this);
        Product product = productDAO.getProduct(productId);
        if (product == null) {
            Log.e(TAG, "Product not found for ID: " + productId);
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            displayProductDetails(product);
            Log.d(TAG, "Product details displayed for: " + product.getProductName());
        } catch (Exception e) {
            Log.e(TAG, "Failed to display product details: " + e.getMessage(), e);
            finish(); // Close if display fails
        }

        // Set up Add to Cart button
        Button addToCartButton = findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(v -> {
            CartItem item = new CartItem(product, 1, false);
            ShoppingCartManager.getInstance().addCartItem(item);
            Toast.makeText(this, "Product added to cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProductDetailActivity.this, ShoppingCartActivity.class));
        });
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
            return true;
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

    private void displayProductDetails(Product product) {
        if (product != null) {
            detailTextProductName.setText(product.getProductName());
            detailTextPrice.setText(String.valueOf(product.getUnitPrice()));
            detailTextSales.setText("Sales: " + product.getSales());
            try {
                detailImageProduct.setImageResource(product.getImageResId());
            } catch (Exception e) {
                Log.e(TAG, "Failed to set image", e);
                detailImageProduct.setImageResource(android.R.drawable.ic_menu_help);
            }
        }
    }
}