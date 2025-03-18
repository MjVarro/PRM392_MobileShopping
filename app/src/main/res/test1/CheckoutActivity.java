package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.dao.OrderDAO;
import com.example.test1.entity.CartItem;
import com.example.test1.manager.SessionManager;

import java.util.ArrayList;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private double baseTotalPayment = 0.0;
    private double shippingCost = 0.0;
    private double paymentMethodFee = 0.0;
    private TextView textTotalCheckoutPayment;
    private TextView textFinalTotalPayment;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to place an order", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        EditText editTextAddress = findViewById(R.id.editTextAddress);
        RecyclerView recyclerViewCheckoutItems = findViewById(R.id.recyclerViewCheckoutItems);
        textTotalCheckoutPayment = findViewById(R.id.textTotalCheckoutPayment);
        textFinalTotalPayment = findViewById(R.id.textFinalTotalPayment);
        Spinner spinnerShippingMethod = findViewById(R.id.spinnerShippingMethod);
        RadioGroup radioGroupPaymentMethod = findViewById(R.id.radioGroupPaymentMethod);
        Button btnOrderNow = findViewById(R.id.btnOrderNow);

        // Populate spinner with shipping methods
        String[] shippingMethods = {"Standard Shipping", "Express Shipping", "Fast Shipping"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shippingMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShippingMethod.setAdapter(adapter);

        // Get data from Intent
        baseTotalPayment = getIntent().getDoubleExtra("total_payment", 0.0);
        ArrayList<CartItem> selectedItems = getIntent().getParcelableArrayListExtra("selected_items");

        // Set up RecyclerView for cart items
        recyclerViewCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        CheckoutAdapter checkoutAdapter = new CheckoutAdapter(selectedItems);
        recyclerViewCheckoutItems.setAdapter(checkoutAdapter);

        // Initial total payment
        updateTotalPayment();

        // Shipping method selection listener
        spinnerShippingMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: shippingCost = 5.0; break;
                    case 1: shippingCost = 10.0; break;
                    case 2: shippingCost = 15.0; break;
                    default: shippingCost = 0.0;
                }
                updateTotalPayment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                shippingCost = 0.0;
                updateTotalPayment();
            }
        });

        // Back button click listener
        backButton.setOnClickListener(v -> finish());

        // Order now button click listener
        btnOrderNow.setOnClickListener(v -> {
            String address = editTextAddress.getText().toString().trim();
            if (address.isEmpty()) {
                editTextAddress.setError("Please enter your address");
                return;
            }

            int selectedPaymentId = radioGroupPaymentMethod.getCheckedRadioButtonId();
            if (selectedPaymentId == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            if (spinnerShippingMethod.getSelectedItem() == null) {
                Toast.makeText(this, "Please select a shipping method", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy accountId từ SessionManager
            int accountId = sessionManager.getLoggedInAccount().getAccountId();
            String userId = String.valueOf(accountId); // Chuyển thành String để dùng trong OrderDAO
            String orderId = generateOrderId(userId);

            // Lưu đơn hàng vào database
            OrderDAO orderDAO = new OrderDAO(this);
            orderDAO.insertOrder(orderId, userId, baseTotalPayment + shippingCost + paymentMethodFee, address, selectedItems);

            // Prepare data for OrderListActivity
            Intent intent = new Intent(CheckoutActivity.this, OrderListActivity.class);
            intent.putParcelableArrayListExtra("ordered_items", selectedItems);
            intent.putExtra("total_payment", baseTotalPayment + shippingCost + paymentMethodFee);
            intent.putExtra("shipping_address", address);
            intent.putExtra("order_id", orderId);

            Toast.makeText(this, "Order placed successfully! Order ID: " + orderId, Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        });
    }

    private void updateTotalPayment() {
        double totalPayment = baseTotalPayment + shippingCost + paymentMethodFee;
        textTotalCheckoutPayment.setText(String.format(Locale.getDefault(), "$%.2f", totalPayment));
        textFinalTotalPayment.setText(String.format(Locale.getDefault(), "Total Payment: $%.2f", totalPayment));
    }

    private String generateOrderId(String userId) {
        long timestamp = System.currentTimeMillis();
        return "ORD-" + userId + "-" + timestamp;
    }
}