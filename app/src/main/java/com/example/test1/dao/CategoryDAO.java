    package com.example.test1.dao;

// CategoryDAO.java
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.test1.dtb.DatabaseHelper;
import com.example.test1.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private static final String TAG = "CategoryDAO";
    private DatabaseHelper dbHelper;

    public CategoryDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        Log.d(TAG, "CategoryDAO initialized");
    }

    public void addCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "Adding category: " + category.getCategoryName());
        ContentValues values = new ContentValues();
        values.put("categoryName", category.getCategoryName());
        values.put("categoryDescription", category.getCategoryDescription());
        long result = db.insert("Categories", null, values);
        if (result != -1) {
            Log.d(TAG, "Category added successfully with ID: " + result);
        } else {
            Log.e(TAG, "Failed to add category: " + category.getCategoryName());
        }
        db.close();
    }

    public Category getCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d(TAG, "Querying category with ID: " + categoryId);
        Cursor cursor = db.rawQuery("SELECT * FROM Categories WHERE categoryId = ?", new String[]{String.valueOf(categoryId)});
        Category category = null;
        if (cursor.moveToFirst()) {
            category = new Category(
                    cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")),
                    cursor.getString(cursor.getColumnIndexOrThrow("categoryName")),
                    cursor.getString(cursor.getColumnIndexOrThrow("categoryDescription"))
            );
            Log.d(TAG, "Found category: " + category.getCategoryName());
        } else {
            Log.w(TAG, "No category found with ID: " + categoryId);
        }
        cursor.close();
        db.close();
        return category;
    }

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d(TAG, "Querying all categories");
        Cursor cursor = db.rawQuery("SELECT * FROM Categories", null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("categoryId"));
                    String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("categoryName"));
                    String categoryDescription = cursor.getString(cursor.getColumnIndexOrThrow("categoryDescription"));

                    Log.d(TAG, "Raw data - categoryId: " + categoryId + ", categoryName: " + (categoryName != null ? categoryName : "null"));

                    if (categoryName == null) categoryName = "Unnamed Category";
                    if (categoryDescription == null) categoryDescription = "No description";

                    Category category = new Category(categoryId, categoryName, categoryDescription);
                    categoryList.add(category);
                    Log.d(TAG, "Retrieved category: " + category.getCategoryName());
                } catch (Exception e) {
                    Log.e(TAG, "Failed to create Category from cursor: " + e.getMessage(), e);
                }
            } while (cursor.moveToNext());
        } else {
            Log.w(TAG, "No categories found in database");
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Total categories retrieved: " + categoryList.size());
        return categoryList;
    }

    public void updateCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "Updating category: " + category.getCategoryName());
        ContentValues values = new ContentValues();
        values.put("categoryName", category.getCategoryName());
        values.put("categoryDescription", category.getCategoryDescription());
        int rowsAffected = db.update("Categories", values, "categoryId = ?", new String[]{String.valueOf(category.getCategoryId())});
        if (rowsAffected > 0) {
            Log.d(TAG, "Category updated successfully");
        } else {
            Log.e(TAG, "Failed to update category with ID: " + category.getCategoryId());
        }
        db.close();
    }

    public void deleteCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "Deleting category with ID: " + categoryId);
        int rowsAffected = db.delete("Categories", "categoryId = ?", new String[]{String.valueOf(categoryId)});
        if (rowsAffected > 0) {
            Log.d(TAG, "Category deleted successfully");
        } else {
            Log.w(TAG, "No category found to delete with ID: " + categoryId);
        }
        db.close();
    }

    public void insertSampleCategories(Context context) {
        Log.d(TAG, "Inserting sample categories");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Categories", null, null);

        Category category1 = new Category(1, "Electronics", "Electronic devices and gadgets");
        Category category2 = new Category(2, "Clothing", "Fashion and apparel");
        addCategory(category1);
        addCategory(category2);

        Log.d(TAG, "Sample categories inserted successfully");
        db.close();
    }
}
