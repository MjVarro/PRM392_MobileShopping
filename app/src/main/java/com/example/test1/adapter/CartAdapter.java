package com.example.test1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.ShoppingCartActivity;
import com.example.test1.dao.CartDAO;
import com.example.test1.entity.CartItem;
import com.example.test1.entity.Product;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private CartDAO cartDAO;

    public CartAdapter(List<CartItem> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
        this.cartDAO = new CartDAO(context); // Khởi tạo CartDAO
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        holder.checkboxCartItem.setChecked(cartItem.isSelected());
        holder.textCartProductName.setText(product.getProductName());
        holder.textCartPrice.setText(String.format("$%.2f", product.getUnitPrice()));
        holder.textInventoryQuantity.setText("Inventory: " + product.getUnitQuantity());
        holder.textCartQuantity.setText(String.valueOf(cartItem.getQuantity()));
        try {
            holder.imageCartProduct.setImageResource(product.getImageResId());
        } catch (Exception e) {
            holder.imageCartProduct.setImageResource(android.R.drawable.ic_menu_help);
        }

        // Checkbox listener
        holder.checkboxCartItem.setOnClickListener(v -> {
            cartItem.setSelected(holder.checkboxCartItem.isChecked());
            updateCartItem(cartItem); // Cập nhật trạng thái selected vào DB
            if (context instanceof ShoppingCartActivity) {
                ((ShoppingCartActivity) context).updateTotalPayment();
            }
        });

        // Increase quantity
        holder.btnIncreaseQuantity.setOnClickListener(v -> updateQuantity(cartItem, holder, 1));

        // Decrease quantity
        holder.btnDecreaseQuantity.setOnClickListener(v -> updateQuantity(cartItem, holder, -1));

        // Delete button
        holder.btnDelete.setOnClickListener(v -> removeItem(position));
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    private void updateQuantity(CartItem cartItem, CartViewHolder holder, int change) {
        int newQuantity = cartItem.getQuantity() + change;
        if (newQuantity >= 1 && newQuantity <= cartItem.getProduct().getUnitQuantity()) {
            cartItem.setQuantity(newQuantity);
            holder.textCartQuantity.setText(String.valueOf(newQuantity));
            updateCartItem(cartItem); // Lưu số lượng mới vào DB
            if (context instanceof ShoppingCartActivity) {
                ((ShoppingCartActivity) context).updateTotalPayment();
            }
        } else if (newQuantity < 1) {
            Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Quantity cannot exceed inventory", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeItem(int position) {
        CartItem cartItem = cartItems.get(position);
        cartDAO.removeFromCart(cartItem.getProduct().getProductId()); // Xóa khỏi DB
        cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartItems.size());
        Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
        if (context instanceof ShoppingCartActivity) {
            ((ShoppingCartActivity) context).toggleCartVisibility(cartItems);
            ((ShoppingCartActivity) context).updateTotalPayment();
        }
    }

    private void updateCartItem(CartItem cartItem) {
        // Cập nhật số lượng và trạng thái selected vào DB
        cartDAO.updateCartItem(cartItem);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkboxCartItem;
        ImageView imageCartProduct;
        TextView textCartProductName, textCartPrice, textInventoryQuantity, textCartQuantity;
        Button btnDecreaseQuantity, btnIncreaseQuantity, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxCartItem = itemView.findViewById(R.id.checkboxCartItem);
            imageCartProduct = itemView.findViewById(R.id.imageCartProduct);
            textCartProductName = itemView.findViewById(R.id.textCartProductName);
            textCartPrice = itemView.findViewById(R.id.textCartPrice);
            textInventoryQuantity = itemView.findViewById(R.id.textInventoryQuantity);
            textCartQuantity = itemView.findViewById(R.id.textCartQuantity);
            btnDecreaseQuantity = itemView.findViewById(R.id.btnDecreaseQuantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btnIncreaseQuantity);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}