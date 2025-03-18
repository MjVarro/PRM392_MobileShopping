package com.example.test1.manager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test1.R;
import com.example.test1.dao.ProductDAO;
import com.example.test1.entity.Product;

public class UpdateProductActivity extends AppCompatActivity {

    private EditText etProductName, etProductDescription, etUnitPrice, etUnitQuantity, etImageUrl, etCategoryId;
    private ProductDAO productDAO;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        productDAO = new ProductDAO(this);
        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        etUnitPrice = findViewById(R.id.etUnitPrice);
        etUnitQuantity = findViewById(R.id.etUnitQuantity);
        etImageUrl = findViewById(R.id.etImageUrl); // Thay etImageResId bằng etImageUrl
        etCategoryId = findViewById(R.id.etCategoryId);

        loadProductData();

        Button btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(v -> updateProduct());
    }

    private void loadProductData() {
        Product product = productDAO.getProduct(productId);
        if (product != null) {
            etProductName.setText(product.getProductName());
            etProductDescription.setText(product.getProductDescription());
            etUnitPrice.setText(String.valueOf(product.getUnitPrice()));
            etUnitQuantity.setText(String.valueOf(product.getUnitQuantity()));
            etImageUrl.setText(""); // Không hiển thị imageResId cũ, để trống cho người dùng nhập URL
            etCategoryId.setText(String.valueOf(product.getCategoryId()));
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateProduct() {
        String productName = etProductName.getText().toString().trim();
        String productDescription = etProductDescription.getText().toString().trim();
        String unitPriceStr = etUnitPrice.getText().toString().trim();
        String unitQuantityStr = etUnitQuantity.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String categoryIdStr = etCategoryId.getText().toString().trim();

        if (productName.isEmpty() || unitPriceStr.isEmpty() || unitQuantityStr.isEmpty() || categoryIdStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double unitPrice = Double.parseDouble(unitPriceStr);
            int unitQuantity = Integer.parseInt(unitQuantityStr);
            int categoryId = Integer.parseInt(categoryIdStr);
            int imageResId = mapUrlToResId(imageUrl);

            Product updatedProduct = new Product(productId, categoryId, productName, productDescription, unitPrice, unitQuantity, false, imageResId, 0);
            if (productDAO.updateProduct(updatedProduct)) {
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format in price, quantity, or category ID", Toast.LENGTH_SHORT).show();
        }
    }

    private int mapUrlToResId(String imageUrl) {
        if (imageUrl.isEmpty()) {
            Product existingProduct = productDAO.getProduct(productId);
            return existingProduct != null ? existingProduct.getImageResId() : R.drawable.product2;
        } else {
            // Tạm thời trả về placeholder, bạn có thể ánh xạ URL sang resId cụ thể nếu có danh sách tài nguyên
            return R.drawable.product1;
        }
    }
}