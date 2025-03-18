package com.example.test1.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Product implements Parcelable {
    private int productId;
    private int categoryId;
    private String productName;
    private String productDescription;
    private double unitPrice;
    private int unitQuantity;
    private boolean isFeatured;
    private int imageResId;
    private int sales;

    public Product(int productId, int categoryId, String productName, String productDescription, double unitPrice, int unitQuantity, boolean isFeatured, int imageResId, int sales) {
        Log.d("Product", "Creating Product with name: " + (productName != null ? productName : "null") + ", imageResId: " + imageResId);
        if (productName == null) {
            throw new IllegalArgumentException("Product name cannot be null");
        }
        this.productId = productId;
        this.categoryId = categoryId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.unitPrice = unitPrice;
        this.unitQuantity = unitQuantity;
        this.isFeatured = isFeatured;
        this.imageResId = imageResId;
        this.sales = sales;
    }

    // Parcelable constructor
    protected Product(Parcel in) {
        productId = in.readInt();
        categoryId = in.readInt();
        productName = in.readString();
        productDescription = in.readString();
        unitPrice = in.readDouble();
        unitQuantity = in.readInt();
        isFeatured = in.readByte() != 0;
        imageResId = in.readInt();
        sales = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(productId);
        dest.writeInt(categoryId);
        dest.writeString(productName);
        dest.writeString(productDescription);
        dest.writeDouble(unitPrice);
        dest.writeInt(unitQuantity);
        dest.writeByte((byte) (isFeatured ? 1 : 0));
        dest.writeInt(imageResId);
        dest.writeInt(sales);
    }

    // Getters and setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public int getUnitQuantity() { return unitQuantity; }
    public void setUnitQuantity(int unitQuantity) { this.unitQuantity = unitQuantity; }
    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public int getSales() { return sales; }
    public void setSales(int sales) { this.sales = sales; }
}