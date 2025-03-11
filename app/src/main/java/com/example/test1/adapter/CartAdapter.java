package com.example.test1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.test1.R;
import com.example.test1.ShoppingCartActivity;
import com.example.test1.entity.CartItem;
import com.example.test1.entity.Product;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;

    public CartAdapter(List<CartItem> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        holder.checkboxCartItem.setChecked(cartItem.isSelected());
        holder.textCartProductName.setText(product.getProductName());
        holder.textCartPrice.setText(String.format("$%.2f", product.getUnitPrice()));
        holder.textInventoryQuantity.setText("Inventory: " + product.getUnitQuantity());
        holder.imageCartProduct.setImageResource(product.getImageResId());
        holder.textCartQuantity.setText(String.valueOf(cartItem.getQuantity()));

        holder.btnDecreaseQuantity.setOnClickListener(v -> updateQuantity(cartItem, holder, -1));
        holder.btnIncreaseQuantity.setOnClickListener(v -> updateQuantity(cartItem, holder, 1));
        holder.btnDelete.setOnClickListener(v -> removeItem(position));

        holder.checkboxCartItem.setOnClickListener(v -> {
            cartItem.setSelected(holder.checkboxCartItem.isChecked());
            ((ShoppingCartActivity) context).updateTotalPayment();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private void updateQuantity(CartItem cartItem, CartViewHolder holder, int change) {
        int newQuantity = cartItem.getQuantity() + change;
        if (newQuantity >= 1 && newQuantity <= cartItem.getProduct().getUnitQuantity()) {
            cartItem.setQuantity(newQuantity);
            holder.textCartQuantity.setText(String.valueOf(newQuantity));
            ((ShoppingCartActivity) context).updateTotalPayment();
        }
    }

    private void removeItem(int position) {
        cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartItems.size());
        ((ShoppingCartActivity) context).updateTotalPayment();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkboxCartItem;
        ImageView imageCartProduct;
        TextView textCartProductName, textCartPrice, textInventoryQuantity, textCartQuantity;
        Button btnDecreaseQuantity, btnIncreaseQuantity, btnDelete;

        public CartViewHolder(View itemView) {
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
