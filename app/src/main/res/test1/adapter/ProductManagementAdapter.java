package com.example.test1.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test1.R;
import com.example.test1.dao.ProductDAO;
import com.example.test1.entity.Product;
import com.example.test1.manager.UpdateProductActivity;

import java.util.ArrayList;
import java.util.List;

public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.ProductManagementViewHolder> {
    private static final String TAG = "ProductManagementAdapter";
    private List<Product> productList;
    private Context context;
    private List<Integer> selectedProductIds = new ArrayList<>();
    private ProductDAO productDAO;

    public ProductManagementAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
        this.productDAO = new ProductDAO(context);
        Log.d(TAG, "ProductManagementAdapter initialized with " + productList.size() + " items");
    }

    @NonNull
    @Override
    public ProductManagementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_management, parent, false);
        return new ProductManagementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductManagementViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product != null) {
            holder.tvProductName.setText(product.getProductName());
            holder.tvProductPrice.setText(String.format("$%.2f", product.getUnitPrice()));
            holder.tvProductQuantity.setText("Qty: " + product.getUnitQuantity());
            holder.tvProductSales.setText("Sales: " + product.getSales());

            // Hiển thị ảnh từ imageResId
            if (product.getImageResId() != 0) {
                Glide.with(context)
                        .load(product.getImageResId())
                        .placeholder(R.drawable.product1)
                        .error(R.drawable.product1)
                        .into(holder.ivProductImage);
            } else {
                holder.ivProductImage.setImageResource(R.drawable.product1);
            }

            holder.cbSelectProduct.setChecked(selectedProductIds.contains(product.getProductId()));
            holder.cbSelectProduct.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedProductIds.add(product.getProductId());
                } else {
                    selectedProductIds.remove(Integer.valueOf(product.getProductId()));
                }
            });

            holder.ivDeleteProduct.setOnClickListener(v -> {
                int productIdToDelete = product.getProductId();
                if (productDAO.deleteProduct(productIdToDelete)) {
                    productList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, productList.size());
                }
            });

            holder.btnUpdateProduct.setOnClickListener(v -> {
                Intent intent = new Intent(context, UpdateProductActivity.class);
                intent.putExtra("PRODUCT_ID", product.getProductId());
                ((AppCompatActivity) context).startActivityForResult(intent, 2);
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void deleteSelectedProducts() {
        if (selectedProductIds.isEmpty()) return;

        List<Product> updatedList = new ArrayList<>(productList);
        for (Integer productId : selectedProductIds) {
            if (productDAO.deleteProduct(productId)) {
                updatedList.removeIf(product -> product.getProductId() == productId);
            }
        }
        productList.clear();
        productList.addAll(updatedList);
        selectedProductIds.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedProductIds() {
        return new ArrayList<>(selectedProductIds);
    }

    public static class ProductManagementViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductQuantity, tvProductSales;
        ImageView ivProductImage, ivDeleteProduct;
        CheckBox cbSelectProduct;
        Button btnUpdateProduct;

        public ProductManagementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvProductSales = itemView.findViewById(R.id.tvProductSales);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            cbSelectProduct = itemView.findViewById(R.id.cbSelectProduct);
            ivDeleteProduct = itemView.findViewById(R.id.ivDeleteProduct);
            btnUpdateProduct = itemView.findViewById(R.id.btnUpdateProduct);
        }
    }
}