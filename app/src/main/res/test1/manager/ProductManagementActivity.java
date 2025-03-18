package com.example.test1.manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.adapter.ProductManagementAdapter;
import com.example.test1.dao.ProductDAO;
import com.example.test1.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductManagementAdapter adapter;
    private List<Product> productList;
    private ProductDAO productDAO;

    private static final int REQUEST_CODE_ADD = 1;
    private static final int REQUEST_CODE_UPDATE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo productList và ProductDAO
        productList = new ArrayList<>();
        productDAO = new ProductDAO(this);

        // Tải danh sách sản phẩm
        loadProducts();

        // Thiết lập RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProductList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductManagementAdapter(productList, this);
        recyclerView.setAdapter(adapter);

        // Nút "Add Product"
        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ProductManagementActivity.this, AddProductActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD);
        });

        // Nút "Delete Selected Products"
        TextView tvDelete = findViewById(R.id.tvDelete);
        tvDelete.setOnClickListener(v -> {
            if (adapter.getSelectedProductIds().isEmpty()) {
                Toast.makeText(this, "Please select at least one product to delete", Toast.LENGTH_SHORT).show();
            } else {
                adapter.deleteSelectedProducts();
                loadProducts(); // Làm mới danh sách sau khi xóa
                Toast.makeText(this, "Selected products deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        // Kiểm tra null và khởi tạo nếu cần
        if (productList == null) {
            productList = new ArrayList<>();
        } else {
            productList.clear();
        }

        // Sử dụng ProductDAO để tải danh sách sản phẩm
        productList.addAll(productDAO.getAllProducts());

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_ADD || requestCode == REQUEST_CODE_UPDATE) && resultCode == RESULT_OK) {
            loadProducts(); // Làm mới danh sách sau khi thêm hoặc cập nhật
        }
    }
}