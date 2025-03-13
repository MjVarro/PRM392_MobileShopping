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

import com.example.test1.dao.CartDAO;
import com.example.test1.dao.ProductDAO;
import com.example.test1.entity.CartItem;
import com.example.test1.entity.Product;
import com.example.test1.manager.SessionManager;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";
    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    private ImageView detailImageProduct;
    private TextView detailTextProductName, detailTextPrice, detailTextSales;
    private SessionManager sessionManager;
    private ProductDAO productDAO;
    private CartDAO cartDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Log.d(TAG, "onCreate called");

        // Initialize SessionManager, ProductDAO, and CartDAO
        sessionManager = new SessionManager(this);
        productDAO = new ProductDAO(this);
        cartDAO = new CartDAO(this);

        // Initialize views
        detailImageProduct = findViewById(R.id.detailImageProduct);
        detailTextProductName = findViewById(R.id.detailTextProductName);
        detailTextPrice = findViewById(R.id.detailTextPrice);
        detailTextSales = findViewById(R.id.detailTextSales);

        Intent intent = getIntent();
        int productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1);
        if (productId == -1) {
            Log.e(TAG, "Invalid product ID received");
            Toast.makeText(this, "Invalid product selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
            finish();
        }

        // Set up Add to Cart button
        Button addToCartButton = findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Log.d(TAG, "User not logged in, redirecting to LoginActivity");
                Toast.makeText(this, "Please log in to add items to your cart", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                return;
            }

            CartItem item = new CartItem(product, 1, false);
            cartDAO.addToCart(item); // Thêm vào bảng Cart
            Toast.makeText(this, "Product added to cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProductDetailActivity.this, ShoppingCartActivity.class));
        });
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
        } else if (itemId == R.id.menu_home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.menu_cart) {
            startActivity(new Intent(this, ShoppingCartActivity.class));
            return true;
        } else if (itemId == R.id.menu_logout) {
            if (sessionManager.isLoggedIn()) {
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
            detailTextPrice.setText(String.format("$%.2f", product.getUnitPrice()));
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