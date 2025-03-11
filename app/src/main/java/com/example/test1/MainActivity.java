package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.test1.adapter.ProductAdapter;
import com.example.test1.adapter.SliderAdapter;
import com.example.test1.dao.ProductDAO;
import com.example.test1.entity.Product;
import com.example.test1.manager.SessionManager;
import com.example.test1.Model.SliderModel;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ViewPager2 mViewPager2;
    private DotsIndicator dotsIndicator;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> fullProductList;
    private ProductDAO productDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize Slider
        mViewPager2 = findViewById(R.id.viewPager2);
        dotsIndicator = findViewById(R.id.dotIndicator);
        SliderAdapter sliderAdapter = new SliderAdapter(getSliderList());
        mViewPager2.setAdapter(sliderAdapter);
        dotsIndicator.setViewPager2(mViewPager2);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProducts);
        productDAO = new ProductDAO(this);
        productDAO.insertSampleProducts(this);
        fullProductList = productDAO.getAllProducts();
        if (fullProductList == null) fullProductList = new ArrayList<>();
        productList = new ArrayList<>(fullProductList);
        productAdapter = new ProductAdapter(productList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(productAdapter);

        // Initialize SearchView
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

    private List<SliderModel> getSliderList() {
        List<SliderModel> list = new ArrayList<>();
        list.add(new SliderModel(R.drawable.slider1));
        list.add(new SliderModel(R.drawable.slider2));
        list.add(new SliderModel(R.drawable.slider3));
        list.add(new SliderModel(R.drawable.slider4));
        return list;
    }

    private void filterProducts(String query) {
        query = query.trim().toLowerCase();
        productList.clear();
        if (query.isEmpty()) {
            productList.addAll(fullProductList);
        } else {
            for (Product product : fullProductList) {
                if (product != null && product.getProductName() != null &&
                        product.getProductName().toLowerCase().contains(query)) {
                    productList.add(product);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
        if (productList.isEmpty()) {
            Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show();
        }
    }

    public void showLoggedInDialog() {
        // ... (giữ nguyên phương thức này)
    }
}