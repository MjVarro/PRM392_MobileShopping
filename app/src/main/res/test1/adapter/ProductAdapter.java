package com.example.test1.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.ProductDetailActivity;
import com.example.test1.R;
import com.example.test1.entity.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private static final String TAG = "ProductAdapter";
    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
        Log.d(TAG, "ProductAdapter initialized with " + (productList != null ? productList.size() : "null") + " items");
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        Log.d(TAG, "Creating ViewHolder for viewType: " + viewType);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        if (productList != null && position < productList.size()) {
            Product product = productList.get(position);
            if (product != null) {
                holder.textProductName.setText(product.getProductName());
                holder.textPrice.setText(String.format("$%.2f", product.getUnitPrice()));
                if (holder.imageProduct != null) {
                    int imageResId = product.getImageResId();
                    Log.d(TAG, "Attempting to set image for product: " + product.getProductName() + ", ResId: " + imageResId);
                    try {
                        holder.imageProduct.setImageResource(imageResId);
                        Log.d(TAG, "Image set successfully for product: " + product.getProductName());
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to set image for product: " + product.getProductName() + ", ResId: " + imageResId, e);
                        holder.imageProduct.setImageResource(android.R.drawable.ic_menu_help);
                    }
                } else {
                    Log.e(TAG, "imageProduct is null for product: " + product.getProductName());
                }

                holder.itemView.setOnClickListener(v -> {
                    Log.d(TAG, "Product clicked at position " + position + ", ID: " + product.getProductId());
                    if (product.getProductId() > 0) {
                        Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
                        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getProductId());
                        try {
                            v.getContext().startActivity(intent);
                            Log.d(TAG, "Navigating to ProductDetailActivity for product ID: " + product.getProductId());
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to start ProductDetailActivity: " + e.getMessage(), e);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = productList != null ? productList.size() : 0;
        Log.d(TAG, "getItemCount returning: " + count);
        return count;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textProductName, textPrice;
        ImageView imageProduct;

        public ProductViewHolder(View itemView) {
            super(itemView);
            textProductName = itemView.findViewById(R.id.textProductName);
            textPrice = itemView.findViewById(R.id.textPrice);
            imageProduct = itemView.findViewById(R.id.productImage); // Fixed ID
            if (imageProduct == null) {
                Log.e("ProductViewHolder", "ImageView productImage not found");
            }
            Log.d("ProductViewHolder", "ViewHolder created for item");
            itemView.setClickable(true);
            itemView.setFocusable(true);
        }
    }

    public void updateList(List<Product> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }
}