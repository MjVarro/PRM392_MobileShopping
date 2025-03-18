package com.example.test1.manager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test1.R;
import com.example.test1.dao.ProductDAO;
import com.example.test1.entity.Product;

public class AddProductActivity extends AppCompatActivity {

    private EditText etProductName, etProductDescription, etUnitPrice, etUnitQuantity, etImageUrl, etCategoryId;
    private ProductDAO productDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productDAO = new ProductDAO(this);

        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        etUnitPrice = findViewById(R.id.etUnitPrice);
        etUnitQuantity = findViewById(R.id.etUnitQuantity);
        etImageUrl = findViewById(R.id.etImageUrl); // Thay etImageResId bằng etImageUrl
        etCategoryId = findViewById(R.id.etCategoryId);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> addProduct());
    }

    private void addProduct() {
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

            // Ánh xạ URL sang imageResId (tạm thời sử dụng một giá trị mặc định nếu không có URL)
            int imageResId = mapUrlToResId(imageUrl);

            Product product = new Product(0, categoryId, productName, productDescription, unitPrice, unitQuantity, false, imageResId, 0);
            long newId = productDAO.addProduct(product);
            if (newId != -1) {
                Toast.makeText(this, "Product added successfully with ID: " + newId, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format in price, quantity, or category ID", Toast.LENGTH_SHORT).show();
        }
    }

    private int mapUrlToResId(String imageUrl) {
        // Logic đơn giản: Nếu có URL, trả về một resource mặc định; nếu không, dùng placeholder
        if (imageUrl.isEmpty()) {
            return R.drawable.product1; // Đảm bảo có ảnh placeholder trong res/drawable
        } else {
            // Ở đây bạn có thể ánh xạ URL sang một resource cục bộ nếu cần
            // Ví dụ: return R.drawable.some_image;
            return R.drawable.product2;// Tạm thời dùng placeholder
            // Lưu ý: Để thực sự tải ảnh từ URL, bạn cần lưu URL riêng và hiển thị bằng Glide trong Adapter
        }
    }
}