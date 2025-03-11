package com.example.test1;

import com.example.test1.entity.CartItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartManager {
    private static ShoppingCartManager instance;
    private List<CartItem> cartItems;

    private ShoppingCartManager() {
        cartItems = new ArrayList<>();
    }

    public static ShoppingCartManager getInstance() {
        if (instance == null) {
            instance = new ShoppingCartManager();
        }
        return instance;
    }

    public void addCartItem(CartItem cartItem) {
        if (cartItem != null && cartItem.getProduct() != null) {
            for (CartItem existingItem : cartItems) {
                if (existingItem.getProduct().getProductId() == cartItem.getProduct().getProductId()) {
                    existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
                    return;
                }
            }
            cartItems.add(cartItem);
        }
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems); // Return a copy to prevent external modification
    }

    public void removeCartItem(CartItem cartItem) {
        if (cartItem != null) {
            cartItems.remove(cartItem);
        }
    }

    public void clearCart() {
        cartItems.clear();
    }

    public int getCartItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            if (item != null && item.getProduct() != null) {
                count += item.getQuantity();
            }
        }
        return count;
    }
}