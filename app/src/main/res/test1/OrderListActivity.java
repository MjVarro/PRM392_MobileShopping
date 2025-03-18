package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.dao.OrderDAO;
import com.example.test1.entity.CartItem;
import com.example.test1.manager.SessionManager;

import java.util.ArrayList;
import java.util.Locale;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private TextView textOrderTotal;
    private ImageButton backButton;
    private Button btnContinueShopping;
    private ArrayList<CartItem> orderedItems;
    private double totalPayment;
    private String shippingAddress;
    private String orderId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize views
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        textOrderTotal = findViewById(R.id.textOrderTotal);
        backButton = findViewById(R.id.backButton);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);

        // Get data from Intent
        orderedItems = getIntent().getParcelableArrayListExtra("ordered_items");
        totalPayment = getIntent().getDoubleExtra("total_payment", 0.0);
        shippingAddress = getIntent().getStringExtra("shipping_address");
        orderId = getIntent().getStringExtra("order_id");

        OrderDAO orderDAO = new OrderDAO(this);
        if (orderedItems != null && !orderedItems.isEmpty()) {
            // Hiển thị đơn hàng vừa đặt từ Intent
            recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
            CheckoutAdapter orderAdapter = new CheckoutAdapter(orderedItems);
            recyclerViewOrders.setAdapter(orderAdapter);
            textOrderTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", totalPayment));
        } else {
            // Hiển thị tất cả đơn hàng của user
            String userId = String.valueOf(sessionManager.getLoggedInAccount().getAccountId());
            Log.d("OrderListActivity", "Fetching orders for userId: " + userId);
            ArrayList<OrderDAO.OrderSummary> orders = orderDAO.getOrdersByUser(userId);
            if (orders != null && !orders.isEmpty()) {
                Log.d("OrderListActivity", "Found " + orders.size() + " orders");
                // Hiện tại chỉ hiển thị đơn hàng đầu tiên, bạn có thể sửa để hiển thị danh sách
                orderId = orders.get(0).getOrderId();
                totalPayment = orders.get(0).getTotalPayment();
                orderedItems = orderDAO.getOrderItems(orderId);

                recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
                CheckoutAdapter orderAdapter = new CheckoutAdapter(orderedItems);
                recyclerViewOrders.setAdapter(orderAdapter);
                textOrderTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", totalPayment));
            } else {
                Log.d("OrderListActivity", "No orders found for userId: " + userId);
                recyclerViewOrders.setVisibility(View.GONE);
                textOrderTotal.setText("No orders yet");
            }
        }

        backButton.setOnClickListener(v -> finish());
        btnContinueShopping.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}