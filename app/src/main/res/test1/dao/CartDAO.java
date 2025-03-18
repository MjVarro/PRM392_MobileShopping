package com.example.test1.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.test1.dtb.DatabaseHelper;
import com.example.test1.entity.CartItem;
import com.example.test1.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private static final String TAG = "CartDAO";
    private DatabaseHelper dbHelper;
    private static final String TABLE_CART = "Cart";

    // Các cột trong bảng Cart
    private static final String COLUMN_CART_ID = "cartId";
    private static final String COLUMN_PRODUCT_ID = "productId";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_SELECTED = "selected";

    private ProductDAO productDAO;

    public CartDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        productDAO = new ProductDAO(context);
        Log.d(TAG, "CartDAO initialized");
    }

    // Thêm sản phẩm vào giỏ hàng (bảng Cart)
    public void addToCart(CartItem cartItem) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "Adding to cart: " + cartItem.getProduct().getProductName());

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_ID, cartItem.getProduct().getProductId());
        values.put(COLUMN_QUANTITY, cartItem.getQuantity());
        values.put(COLUMN_SELECTED, cartItem.isSelected() ? 1 : 0);

        long result = db.insert(TABLE_CART, null, values);
        if (result == -1) {
            Log.e(TAG, "Failed to add item to cart: " + cartItem.getProduct().getProductName());
        } else {
            Log.d(TAG, "Item added to cart: " + cartItem.getProduct().getProductName());
        }
        db.close();
    }

    // Xóa sản phẩm khỏi giỏ hàng (bảng Cart)
    public boolean removeFromCart(int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "Removing from cart with productId: " + productId);

        int rowsAffected = db.delete(TABLE_CART, COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)});
        db.close();

        if (rowsAffected > 0) {
            Log.d(TAG, "Item removed from cart successfully");
            return true;
        } else {
            Log.w(TAG, "No item found in cart with productId: " + productId);
            return false;
        }
    }

    // Cập nhật số lượng và trạng thái selected của sản phẩm trong giỏ hàng
    public boolean updateCartItem(CartItem cartItem) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "Updating cart item with productId: " + cartItem.getProduct().getProductId());

        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, cartItem.getQuantity());
        values.put(COLUMN_SELECTED, cartItem.isSelected() ? 1 : 0);

        int rowsAffected = db.update(TABLE_CART, values, COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(cartItem.getProduct().getProductId())});
        db.close();

        if (rowsAffected > 0) {
            Log.d(TAG, "Cart item updated successfully");
            return true;
        } else {
            Log.e(TAG, "Failed to update cart item with productId: " + cartItem.getProduct().getProductId());
            return false;
        }
    }

    // Lấy danh sách sản phẩm trong giỏ hàng (bảng Cart)
    public List<CartItem> getCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d(TAG, "Querying all cart items");

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CART, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                boolean selected = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SELECTED)) == 1;

                // Lấy thông tin sản phẩm từ bảng Products
                Product product = productDAO.getProduct(productId);
                if (product != null) {
                    CartItem cartItem = new CartItem(product, quantity, selected);
                    cartItems.add(cartItem);
                    Log.d(TAG, "Retrieved cart item: " + product.getProductName());
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.w(TAG, "No items found in cart");
        }
        db.close();
        Log.d(TAG, "Total cart items retrieved: " + cartItems.size());
        return cartItems;
    }
}