package com.example.test1.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.test1.dtb.DatabaseHelper;
import com.example.test1.entity.Account;

public class AccountDAO {
    private static final String TABLE_NAME = "Accounts";
    private static final String COLUMN_ACCOUNT_ID = "accountId";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONENUMBER = "phoneNumber";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_ROLE_ID = "roleId";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    public AccountDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert sample accounts for testing
    public void insertSampleAccounts() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // Clear existing data

        ContentValues values1 = new ContentValues();
        values1.put(COLUMN_USERNAME, "admin");
        values1.put(COLUMN_PASSWORD, "admin123");
        values1.put(COLUMN_PHONENUMBER, "1234567890");
        values1.put(COLUMN_EMAIL, "admin@example.com");
        values1.put(COLUMN_ADDRESS, "123 Admin St");
        values1.put(COLUMN_ROLE_ID, 0); // Admin role
        long result1 = db.insert(TABLE_NAME, null, values1);
        Log.d("AccountDAO", "Inserted admin account, result: " + result1);

        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_USERNAME, "user");
        values2.put(COLUMN_PASSWORD, "user123");
        values2.put(COLUMN_PHONENUMBER, "0987654321");
        values2.put(COLUMN_EMAIL, "user@example.com");
        values2.put(COLUMN_ADDRESS, "456 User Rd");
        values2.put(COLUMN_ROLE_ID, 1); // User role
        long result2 = db.insert(TABLE_NAME, null, values2);
        Log.d("AccountDAO", "Inserted user account, result: " + result2);

        db.close();
    }
    //
    // Get Account By Username
    public Account getAccountByUsername(String username) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_ACCOUNT_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PHONENUMBER, COLUMN_EMAIL, COLUMN_ADDRESS, COLUMN_ROLE_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Account account = cursorToAccount(cursor);
            cursor.close();
            return account;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Get Account By ID
    public Account getAccountById(int accountId) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_ACCOUNT_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PHONENUMBER, COLUMN_EMAIL, COLUMN_ADDRESS, COLUMN_ROLE_ID};
        String selection = COLUMN_ACCOUNT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(accountId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Account account = cursorToAccount(cursor);
            cursor.close();
            return account;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
   


    // Insert a new account
    public long insertAccount(Account account) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, account.getUsername());
        values.put(COLUMN_PASSWORD, account.getPassword());
        values.put(COLUMN_PHONENUMBER, account.getPhoneNumber());
        values.put(COLUMN_EMAIL, account.getEmail());
        values.put(COLUMN_ADDRESS, account.getAddress());
        values.put(COLUMN_ROLE_ID, account.getRoleId());
        return db.insert(TABLE_NAME, null, values);
    }
    // Update Account
    public int updateAccount(Account account) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, account.getUsername());
        values.put(COLUMN_PASSWORD, account.getPassword());
        values.put(COLUMN_PHONENUMBER, account.getPhoneNumber());
        values.put(COLUMN_EMAIL, account.getEmail());
        values.put(COLUMN_ADDRESS, account.getAddress());
        values.put(COLUMN_ROLE_ID, account.getRoleId());

        String whereClause = COLUMN_ACCOUNT_ID + " = ?";
        String[] whereArgs = {String.valueOf(account.getAccountId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    // Login Method
    public Account login(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String table = TABLE_NAME;
        String[] columns = {COLUMN_ACCOUNT_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PHONENUMBER, COLUMN_EMAIL, COLUMN_ADDRESS, COLUMN_ROLE_ID};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Log.d("AccountDAO", "Login query - Username: " + username + ", Password: " + password);
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        Log.d("AccountDAO", "Login query rows found: " + cursor.getCount());
        if (cursor != null && cursor.moveToFirst()) {
            Account account = cursorToAccount(cursor);
            Log.d("AccountDAO", "Login successful for username: " + username + ", Role: " + account.getRoleId());
            cursor.close();
            return account;
        }
        if (cursor != null) {
            Log.d("AccountDAO", "No matching account found for username: " + username);
            cursor.close();
        } else {
            Log.e("AccountDAO", "Cursor is null, query failed");
        }
        return null;
    }

    // Register Method
    public long register(String username, String password, String phoneNumber, String email, String address, int roleId) {
        if (checkUsername(username)) {
            return -1; // Username already exists
        }
        Account account = new Account(0, username, password, phoneNumber, email, address, roleId);
        return insertAccount(account);
    }

    // Check Username
    private boolean checkUsername(String username) {
        String column = COLUMN_ACCOUNT_ID;
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_NAME, new String[]{column}, selection, selectionArgs, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // Method to convert a cursor to an account object
    private Account cursorToAccount(Cursor cursor) {
        Account account = new Account();
        int accountId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_ID));
        String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
        String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONENUMBER));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
        String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
        int roleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE_ID));
        return new Account(accountId, username, password, phoneNumber, email, address, roleId);
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}