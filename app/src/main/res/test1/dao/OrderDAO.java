package com.example.test1.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.test1.entity.CartItem;
import com.example.test1.entity.Product;

import java.util.ArrayList;

public class OrderDAO {
    private static final String DATABASE_NAME = "ShoppingDB";
    private static final int DATABASE_VERSION = 2; // Tăng version để tạo lại bảng nếu cần
    private static final String TABLE_ORDERS = "orders";
    private static final String TABLE_ORDER_ITEMS = "order_items";
    private static final String TAG = "OrderDAO";

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private ProductDAO productDAO;

    public OrderDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        productDAO = new ProductDAO(context);
        Log.d(TAG, "OrderDAO initialized, database writable: " + database.isOpen());
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "Creating orders table");
            db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" +
                    "order_id TEXT PRIMARY KEY," +
                    "user_id TEXT," +
                    "total_payment REAL," +
                    "shipping_address TEXT," +
                    "order_date INTEGER)");
            Log.d(TAG, "Orders table created");

            Log.d(TAG, "Creating order_items table");
            db.execSQL("CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "order_id TEXT," +
                    "product_id INTEGER," +
                    "quantity INTEGER," +
                    "unit_price REAL," +
                    "FOREIGN KEY(order_id) REFERENCES " + TABLE_ORDERS + "(order_id))");
            Log.d(TAG, "Order_items table created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
            onCreate(db);
        }
    }

    // Chèn một đơn hàng mới vào database
    public void insertOrder(String orderId, String userId, double totalPayment, String shippingAddress, ArrayList<CartItem> items) {
        Log.d(TAG, "Inserting order - orderId: " + orderId + ", userId: " + userId + ", total: " + totalPayment + ", items: " + items.size());

        // Chèn vào bảng orders
        ContentValues orderValues = new ContentValues();
        orderValues.put("order_id", orderId);
        orderValues.put("user_id", userId);
        orderValues.put("total_payment", totalPayment);
        orderValues.put("shipping_address", shippingAddress);
        orderValues.put("order_date", System.currentTimeMillis());
        long orderRowId = database.insert(TABLE_ORDERS, null, orderValues);
        if (orderRowId == -1) {
            Log.e(TAG, "Failed to insert order with orderId: " + orderId);
        } else {
            Log.d(TAG, "Order inserted successfully, rowId: " + orderRowId);
        }

        // Chèn các sản phẩm trong đơn hàng vào bảng order_items
        for (CartItem item : items) {
            ContentValues itemValues = new ContentValues();
            itemValues.put("order_id", orderId);
            itemValues.put("product_id", item.getProduct().getProductId());
            itemValues.put("quantity", item.getQuantity());
            itemValues.put("unit_price", item.getProduct().getUnitPrice());
            long itemRowId = database.insert(TABLE_ORDER_ITEMS, null, itemValues);
            if (itemRowId == -1) {
                Log.e(TAG, "Failed to insert order item for productId: " + item.getProduct().getProductId());
            } else {
                Log.d(TAG, "Order item inserted, rowId: " + itemRowId);
            }
        }
    }

    // Lấy danh sách đơn hàng của một user
    public ArrayList<OrderSummary> getOrdersByUser(String userId) {
        Log.d(TAG, "Fetching orders for userId: " + userId);
        ArrayList<OrderSummary> orders = new ArrayList<>();
        Cursor cursor = database.query(TABLE_ORDERS, null, "user_id = ?", new String[]{userId}, null, null, "order_date DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    String orderId = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
                    double totalPayment = cursor.getDouble(cursor.getColumnIndexOrThrow("total_payment"));
                    String shippingAddress = cursor.getString(cursor.getColumnIndexOrThrow("shipping_address"));
                    long orderDate = cursor.getLong(cursor.getColumnIndexOrThrow("order_date"));
                    orders.add(new OrderSummary(orderId, totalPayment, shippingAddress, orderDate));
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Error reading order data: " + e.getMessage());
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.d(TAG, "No orders found in database for userId: " + userId);
        }
        if (cursor != null) {
            cursor.close();
        }
        Log.d(TAG, "Total orders retrieved: " + orders.size());
        return orders;
    }

    // Lấy chi tiết đơn hàng (bao gồm các CartItem)
    public ArrayList<CartItem> getOrderItems(String orderId) {
        Log.d(TAG, "Fetching items for orderId: " + orderId);
        ArrayList<CartItem> items = new ArrayList<>();
        Cursor cursor = database.query(TABLE_ORDER_ITEMS, null, "order_id = ?", new String[]{orderId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                    double unitPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("unit_price"));

                    Product product = productDAO.getProduct(productId);
                    if (product != null) {
                        product.setUnitPrice(unitPrice); // Ghi đè giá từ order_items
                        items.add(new CartItem(product, quantity, false));
                    } else {
                        Log.w(TAG, "Product not found for productId: " + productId);
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Error reading order item data: " + e.getMessage());
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.d(TAG, "No items found for orderId: " + orderId);
        }
        if (cursor != null) {
            cursor.close();
        }
        Log.d(TAG, "Total items retrieved: " + items.size());
        return items;
    }

    // Class phụ để lưu thông tin cơ bản của đơn hàng
    public static class OrderSummary {
        private String orderId;
        private double totalPayment;
        private String shippingAddress;
        private long orderDate;

        public OrderSummary(String orderId, double totalPayment, String shippingAddress, long orderDate) {
            this.orderId = orderId;
            this.totalPayment = totalPayment;
            this.shippingAddress = shippingAddress;
            this.orderDate = orderDate;
        }

        public String getOrderId() { return orderId; }
        public double getTotalPayment() { return totalPayment; }
        public String getShippingAddress() { return shippingAddress; }
        public long getOrderDate() { return orderDate; }
    }

    // Đóng database khi không dùng nữa
    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}