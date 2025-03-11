package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.adapter.CartAdapter;
import com.example.test1.entity.CartItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCart;
    private TextView emptyCartMessage;
    private TextView textTotalPayment;
    private CartAdapter cartAdapter;
    private ImageButton backBtn;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        emptyCartMessage = findViewById(R.id.emptyCartMessage);
        textTotalPayment = findViewById(R.id.textTotalPayment);
        backBtn = findViewById(R.id.backButton);
        btnCheckout = findViewById(R.id.btnCheckout); // Initialize btnCheckout
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        List<CartItem> cartItems = ShoppingCartManager.getInstance().getCartItems();

        cartAdapter = new CartAdapter(cartItems, this);
        recyclerViewCart.setAdapter(cartAdapter);

        updateTotalPayment();
        toggleCartVisibility(cartItems);
        backBtn.setOnClickListener(v -> onBackPressed());

        // Checkout button click listener
        btnCheckout.setOnClickListener(v -> {
            // Get selected cart items
            List<CartItem> selectedItems = new ArrayList<>();
            for (CartItem item : cartItems) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }

            // Check if any items are selected
            if (selectedItems.isEmpty()) {
                emptyCartMessage.setText("Please select at least one item to proceed to checkout");
                emptyCartMessage.setVisibility(View.VISIBLE);
                return;
            }

            // Calculate total payment for selected items
            double total = 0;
            for (CartItem item : selectedItems) {
                total += item.getProduct().getUnitPrice() * item.getQuantity();
            }

            // Start CheckoutActivity and pass the selected items and total payment
            Intent intent = new Intent(ShoppingCartActivity.this, CheckoutActivity.class);
            intent.putExtra("total_payment", total);
            intent.putParcelableArrayListExtra("selected_items", new ArrayList<>(selectedItems));
            startActivity(intent);
        });
    }

    public void updateTotalPayment() {
        double total = 0;
        for (CartItem item : ShoppingCartManager.getInstance().getCartItems()) {
            if (item.isSelected()) {
                total += item.getProduct().getUnitPrice() * item.getQuantity();
            }
        }
        textTotalPayment.setText(String.format("Total: $%.2f", total));
    }

    private void toggleCartVisibility(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            recyclerViewCart.setVisibility(View.GONE);
            emptyCartMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerViewCart.setVisibility(View.VISIBLE);
            emptyCartMessage.setVisibility(View.GONE);
        }
    }
}