package com.example.test1.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.test1.R;
import com.example.test1.dtb.DatabaseHelper;
import com.example.test1.entity.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductDAO {
    private static final String TAG = "ProductDAO";
    private DatabaseHelper dbHelper;
    private static final String TABLE_PRODUCTS = "Products";

    public ProductDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        Log.d(TAG, "ProductDAO initialized");
    }

    // Thêm sản phẩm mới
    public long addProduct(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "Adding product: " + product.getProductName());

        ContentValues values = new ContentValues();
        values.put("categoryId", product.getCategoryId());
        values.put("productName", product.getProductName());
        values.put("productDescription", product.getProductDescription());
        values.put("unitPrice", product.getUnitPrice());
        values.put("unitQuantity", product.getUnitQuantity());
        values.put("isFeatured", product.isFeatured() ? 1 : 0);
        values.put("imageResId", product.getImageResId());
        values.put("sales", product.getSales());

        // Thêm sản phẩm và trả về ID được tạo tự động
        long newId = db.insert(TABLE_PRODUCTS, null, values);
        if (newId != -1) {
            Log.d(TAG, "Product added successfully with ID: " + newId);
            product.setProductId((int) newId); // Cập nhật ID cho đối tượng Product
        } else {
            Log.e(TAG, "Failed to add product: " + product.getProductName());
        }
        db.close();
        return newId; // Trả về ID mới hoặc -1 nếu thất bại
    }

    // Lấy sản phẩm theo ID
    public Product getProduct(int productId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d(TAG, "Querying product with ID: " + productId);

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE productId = ?",
                new String[]{String.valueOf(productId)});
        Product product = null;

        if (cursor != null && cursor.moveToFirst()) {
            try {
                product = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow("productId")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("productName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("productDescription")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("unitPrice")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("unitQuantity")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("isFeatured")) == 1,
                        cursor.getInt(cursor.getColumnIndexOrThrow("imageResId")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("sales"))
                );
                Log.d(TAG, "Found product: " + product.getProductName());
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Column not found in cursor: " + e.getMessage(), e);
            }
            cursor.close();
        } else {
            Log.w(TAG, "No product found with ID: " + productId);
        }
        db.close();
        return product;
    }

    // Lấy tất cả sản phẩm
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d(TAG, "Querying all products");

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow("productId"));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("categoryId"));
                    String productName = cursor.getString(cursor.getColumnIndexOrThrow("productName"));
                    String productDescription = cursor.getString(cursor.getColumnIndexOrThrow("productDescription"));
                    double unitPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("unitPrice"));
                    int unitQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("unitQuantity"));
                    boolean isFeatured = cursor.getInt(cursor.getColumnIndexOrThrow("isFeatured")) == 1;
                    int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("imageResId"));
                    int sales = cursor.getInt(cursor.getColumnIndexOrThrow("sales"));

                    // Xử lý giá trị null
                    if (productName == null) productName = "Unnamed Product";
                    if (productDescription == null) productDescription = "No description";

                    Product product = new Product(productId, categoryId, productName, productDescription,
                            unitPrice, unitQuantity, isFeatured, imageResId, sales);
                    productList.add(product);
                    Log.d(TAG, "Retrieved product: " + product.getProductName());
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Failed to create Product from cursor: " + e.getMessage(), e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.w(TAG, "No products found in database");
        }
        db.close();
        Log.d(TAG, "Total products retrieved: " + productList.size());
        return productList;
    }

    // Cập nhật sản phẩm
    public boolean updateProduct(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "Updating product with ID: " + product.getProductId());

        ContentValues values = new ContentValues();
        values.put("categoryId", product.getCategoryId());
        values.put("productName", product.getProductName());
        values.put("productDescription", product.getProductDescription());
        values.put("unitPrice", product.getUnitPrice());
        values.put("unitQuantity", product.getUnitQuantity());
        values.put("isFeatured", product.isFeatured() ? 1 : 0);
        values.put("imageResId", product.getImageResId());
        values.put("sales", product.getSales());

        int rowsAffected = db.update(TABLE_PRODUCTS, values, "productId = ?",
                new String[]{String.valueOf(product.getProductId())});
        db.close();

        if (rowsAffected > 0) {
            Log.d(TAG, "Product updated successfully");
            return true;
        } else {
            Log.e(TAG, "Failed to update product with ID: " + product.getProductId());
            return false;
        }
    }

    // Xóa sản phẩm
    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "Deleting product with ID: " + productId);

        int rowsAffected = db.delete(TABLE_PRODUCTS, "productId = ?",
                new String[]{String.valueOf(productId)});
        db.close();

        if (rowsAffected > 0) {
            Log.d(TAG, "Product deleted successfully");
            return true;
        } else {
            Log.w(TAG, "No product found to delete with ID: " + productId);
            return false;
        }
    }

    // Chèn dữ liệu mẫu
    public void insertSampleProducts(Context context) {
        Log.d(TAG, "Inserting sample products");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, null, null); // Xóa dữ liệu cũ

        List<Product> products = Arrays.asList(
                new Product(1, 1, "Sample Product", "Description", 99.99, 100, true, R.drawable.product1, 50),
                new Product(2, 1, "Another Product", "Description", 149.99, 50, false, R.drawable.product2, 30),
                new Product(3, 1, "Product 3", "Description", 199.99, 40, false, R.drawable.product3, 20),
                new Product(4, 2, "Product 4", "Description", 129.99, 60, true, R.drawable.product4, 15),
                new Product(5, 2, "Product 5", "Description", 89.99, 80, false, R.drawable.product5, 10),
                new Product(6, 2, "Product 6", "Description", 159.99, 30, true, R.drawable.product6, 25),
                new Product(7, 2, "Product 7", "Description", 109.99, 90, false, R.drawable.product7, 35),
                new Product(8, 2, "Product 8", "Description", 179.99, 20, true, R.drawable.product8, 45),
                new Product(9, 2, "Product 9", "Description", 69.99, 110, false, R.drawable.product9, 5)
        );

        for (Product product : products) {
            ContentValues values = new ContentValues();
            values.put("productId", product.getProductId());
            values.put("categoryId", product.getCategoryId());
            values.put("productName", product.getProductName());
            values.put("productDescription", product.getProductDescription());
            values.put("unitPrice", product.getUnitPrice());
            values.put("unitQuantity", product.getUnitQuantity());
            values.put("isFeatured", product.isFeatured() ? 1 : 0);
            values.put("imageResId", product.getImageResId());
            values.put("sales", product.getSales());
            db.insertWithOnConflict(TABLE_PRODUCTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }

        Log.d(TAG, "Sample products inserted successfully");
        db.close();
    }
}