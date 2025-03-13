package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.adapter.CartAdapter;
import com.example.test1.dao.CartDAO;
import com.example.test1.entity.CartItem;
import com.example.test1.manager.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCart;
    private TextView emptyCartMessage;
    private TextView textTotalPayment;
    private CartAdapter cartAdapter;

    private Button btnCheckout;
    private Button btnSelectAll;
    private SessionManager sessionManager;
    private CartDAO cartDAO;
    private List<CartItem> cartItems; // Lưu danh sách cartItems để sử dụng trong các phương thức
    private boolean isAllSelected = false; // Theo dõi trạng thái Select All

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        sessionManager = new SessionManager(this);
        cartDAO = new CartDAO(this);

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(ShoppingCartActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Please log in to view your cart", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        emptyCartMessage = findViewById(R.id.emptyCartMessage);
        textTotalPayment = findViewById(R.id.textTotalPayment);

        btnCheckout = findViewById(R.id.btnCheckout);
        btnSelectAll = findViewById(R.id.btnSelectAll); // Liên kết với nút Select All

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        cartItems = cartDAO.getCartItems(); // Lấy từ bảng Cart
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerViewCart.setAdapter(cartAdapter);

        updateTotalPayment();
        toggleCartVisibility(cartItems);



        btnSelectAll.setOnClickListener(v -> {
            isAllSelected = !isAllSelected; // Chuyển đổi trạng thái
            for (CartItem item : cartItems) {
                item.setSelected(isAllSelected);
                cartDAO.updateCartItem(item); // Cập nhật trạng thái selected vào DB
            }
            btnSelectAll.setText(isAllSelected ? "Deselect All" : "Select All"); // Cập nhật văn bản nút
            cartAdapter.notifyDataSetChanged(); // Cập nhật giao diện
            updateTotalPayment(); // Cập nhật tổng thanh toán
        });

        btnCheckout.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Intent intent = new Intent(ShoppingCartActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Please log in to proceed to checkout", Toast.LENGTH_SHORT).show();
                return;
            }

            List<CartItem> selectedItems = new ArrayList<>();
            for (CartItem item : cartItems) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }

            if (selectedItems.isEmpty()) {
                emptyCartMessage.setText("Please select at least one item to proceed to checkout");
                emptyCartMessage.setVisibility(View.VISIBLE);
                return;
            }

            double total = 0;
            for (CartItem item : selectedItems) {
                total += item.getProduct().getUnitPrice() * item.getQuantity();
            }

            Intent intent = new Intent(ShoppingCartActivity.this, CheckoutActivity.class);
            intent.putExtra("total_payment", total);
            intent.putParcelableArrayListExtra("selected_items", new ArrayList<>(selectedItems));
            startActivity(intent);
        });
    }

    public void updateTotalPayment() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getProduct().getUnitPrice() * item.getQuantity();
            }
        }
        textTotalPayment.setText(String.format("Total: $%.2f", total));
    }

    public void toggleCartVisibility(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            recyclerViewCart.setVisibility(View.GONE);
            emptyCartMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerViewCart.setVisibility(View.VISIBLE);
            emptyCartMessage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Làm mới danh sách giỏ hàng khi Activity được quay lại
        cartItems.clear();
        cartItems.addAll(cartDAO.getCartItems());
        cartAdapter.notifyDataSetChanged();
        updateTotalPayment();
        toggleCartVisibility(cartItems);
    }
}