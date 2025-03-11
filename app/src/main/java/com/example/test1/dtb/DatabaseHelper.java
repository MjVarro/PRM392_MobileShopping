package com.example.test1.dtb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.test1.entity.Account;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EcommerceDB";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_ACCOUNTS = "Accounts";
    private static final String COLUMN_ID = "accountId";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phoneNumber";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_ROLE_ID = "roleId";

    private static final String TABLE_PRODUCTS = "Products";

    // Table creation SQL
    private static final String SQL_CREATE_ACCOUNTS =
            "CREATE TABLE Accounts (" +
                    "accountId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT, " +
                    "password TEXT, " +
                    "phoneNumber TEXT, " +
                    "email TEXT, " +
                    "address TEXT, " +
                    "roleId INTEGER)";

    private static final String SQL_CREATE_CATEGORIES =
            "CREATE TABLE Categories (" +
                    "categoryId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "categoryName TEXT, " +
                    "categoryDescription TEXT)";

    private static final String SQL_CREATE_PRODUCTS =
            "CREATE TABLE Products (" +
                    "productId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "categoryId INTEGER, " +
                    "productName TEXT, " +
                    "productDescription TEXT, " +
                    "unitPrice REAL, " +
                    "unitQuantity INTEGER, " +
                    "isFeatured BOOLEAN, " +
                    "imageResId INTEGER, " +
                    "sales INTEGER, " +
                    "FOREIGN KEY (categoryId) REFERENCES Categories(categoryId))";

    private static final String SQL_CREATE_ORDERS =
            "CREATE TABLE Orders (" +
                    "orderId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "accountId INTEGER, " +
                    "orderDate DATE, " +
                    "orderNote TEXT, " +
                    "totalAmount INTEGER, " +
                    "status INTEGER, " +
                    "paymentMethod INTEGER, " +
                    "FOREIGN KEY (accountId) REFERENCES Accounts(accountId))";

    private static final String SQL_CREATE_ORDER_DETAILS =
            "CREATE TABLE OrderDetails (" +
                    "orderDetailId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "orderId INTEGER, " +
                    "productId INTEGER, " +
                    "buyQuantity INTEGER, " +
                    "FOREIGN KEY (orderId) REFERENCES Orders(orderId), " +
                    "FOREIGN KEY (productId) REFERENCES Products(productId))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACCOUNTS);
        db.execSQL(SQL_CREATE_CATEGORIES);
        db.execSQL(SQL_CREATE_PRODUCTS);
        db.execSQL(SQL_CREATE_ORDERS);
        db.execSQL(SQL_CREATE_ORDER_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS OrderDetails");
        db.execSQL("DROP TABLE IF EXISTS Orders");
        db.execSQL("DROP TABLE IF EXISTS Products");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        db.execSQL("DROP TABLE IF EXISTS Accounts");
        onCreate(db);
    }

    // Get all accounts
    public Cursor getAllAccounts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS, null);
    }

    // Get all products
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
    }

    // Delete account
    public boolean deleteAccount(int accountId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ACCOUNTS, COLUMN_ID + "=?",
                new String[]{String.valueOf(accountId)}) > 0;
    }

    // Delete product
    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, "productId=?",
                new String[]{String.valueOf(productId)}) > 0;
    }
}