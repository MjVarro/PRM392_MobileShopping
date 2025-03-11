package com.example.test1;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.test1.adapter.ProductAdapter;
import com.example.test1.adapter.SliderAdapter;
import com.example.test1.dao.ProductDAO;
import com.example.test1.entity.Product;
import com.example.test1.manager.SessionManager;
import com.example.test1.Model.SliderModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ViewPager2 mViewPager2;
    private DotsIndicator dotsIndicator;
    private BottomNavigationView mBottomNavigationView;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> fullProductList;
    private ProductDAO productDAO;
    private TextView cartBadge;
    private ImageButton backBtn, profileButton;
    private SessionManager sessionManager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        if (sessionManager == null) {
            Log.e(TAG, "Failed to initialize SessionManager");
            Toast.makeText(this, "Error initializing session", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize BottomNavigationView
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        if (mBottomNavigationView != null) {
            mBottomNavigationView.setSelectedItemId(R.id.bottom_favorite);
            setupBottomNavigation();
        }

        // Initialize Slider
        mViewPager2 = findViewById(R.id.viewPager2);
        dotsIndicator = findViewById(R.id.dotIndicator);
        if (mViewPager2 != null && dotsIndicator != null) {
            SliderAdapter sliderAdapter = new SliderAdapter(getSliderList());
            mViewPager2.setAdapter(sliderAdapter);
            dotsIndicator.setViewPager2(mViewPager2);
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProducts);
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView not found");
            Toast.makeText(this, "RecyclerView not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize Product DAO and data
        productDAO = new ProductDAO(this);
        productDAO.insertSampleProducts(this);
        fullProductList = productDAO.getAllProducts();
        if (fullProductList == null) fullProductList = new ArrayList<>();
        productList = new ArrayList<>(fullProductList);
        Log.d(TAG, "Product list size: " + productList.size());

        // Setup RecyclerView
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

        // Initialize Back Button
        backBtn = findViewById(R.id.backBtn);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> goBackToCategories(v));
        }

        // Initialize Cart components
        cartBadge = findViewById(R.id.cartBadge);
        ImageButton cartButton = findViewById(R.id.cartButton);
        if (cartButton != null) {
            cartButton.setOnClickListener(v -> startActivity(new Intent(this, ShoppingCartActivity.class)));
        }
        updateCartCount();

        // Initialize Profile Button with Session Check
        profileButton = findViewById(R.id.profileButton);
        if (profileButton != null) {
            profileButton.setOnClickListener(v -> {
                if (sessionManager.isLoggedIn()) {
                    startActivity(new Intent(this, ProfileActivity.class));
                } else {
                    showLoggedInDialog();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        Log.d(TAG, "Menu inflated with " + menu.size() + " items");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Log.d(TAG, "Menu item selected: " + menuItem.getTitle());
        if (menuItem.getItemId() == R.id.menu_user_profile) {
            showEditUserProfile();
        } else if (menuItem.getItemId() == R.id.menu_services) {
            showServices();
        } else if (menuItem.getItemId() == R.id.menu_setting) {
            Toast.makeText(this, "Setting menu selected", Toast.LENGTH_SHORT).show();
        } else if (menuItem.getItemId() == R.id.menu_favourite) {
            Toast.makeText(this, "Favourite menu selected", Toast.LENGTH_SHORT).show();
        } else if (menuItem.getItemId() == R.id.menu_req_gps) {
            requestGPS();
        } else if (menuItem.getItemId() == R.id.menu_send_notification) {
            sendNotification();
        } else if (menuItem.getItemId() == R.id.menu_logout) {
            Toast.makeText(this, "Logout menu selected", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void setupBottomNavigation() {
        mBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_favorite) {
                return true;
            } else if (itemId == R.id.bottom_bag) {
                startActivity(new Intent(this, ProductListActivity.class));
                return true;
            } else if (itemId == R.id.bottom_bell) {
                Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                if (sessionManager.isLoggedIn()) {
                    startActivity(new Intent(this, ProfileActivity.class));
                } else {
                    showLoggedInDialog();
                }
                return true;
            }
            return false;
        });
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
        List<Product> filteredList = new ArrayList<>();
        for (Product product : fullProductList) {
            if (product != null && product.getProductName() != null &&
                    product.getProductName().toLowerCase().contains(query.toLowerCase().trim())) {
                filteredList.add(product);
            }
        }
        productList.clear();
        productList.addAll(filteredList);
        productAdapter.notifyDataSetChanged();
    }

    public void updateCartCount() {
        int count = ShoppingCartManager.getInstance().getCartItemCount();
        if (cartBadge != null) {
            cartBadge.setText(String.valueOf(count));
            cartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    public void showLoggedInDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_not_logged_in);
        Button btnLogin = dialog.findViewById(R.id.btn_login_dialog);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
                dialog.dismiss();
            });
        }
        dialog.show();
    }

    public void goBackToCategories(View view) {
        startActivity(new Intent(this, CategoriesActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
    }

    // Placeholder methods for menu items
    private void showEditUserProfile() {
        Toast.makeText(this, "Edit User Profile selected", Toast.LENGTH_SHORT).show();
    }

    private void showServices() {
        Toast.makeText(this, "Services selected", Toast.LENGTH_SHORT).show();
    }

    private void requestGPS() {
        Toast.makeText(this, "Request GPS selected", Toast.LENGTH_SHORT).show();
    }

    private void sendNotification() {
        Toast.makeText(this, "Send Notification selected", Toast.LENGTH_SHORT).show();
    }
}