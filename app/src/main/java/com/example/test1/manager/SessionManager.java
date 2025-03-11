package com.example.test1.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.test1.dao.AccountDAO;
import com.example.test1.entity.Account;

public class SessionManager {
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_ACCOUNT_ID = "accountId";

    private SharedPreferences sharedPreferences;
    private AccountDAO accountDAO;
    private final Context context;
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        accountDAO = new AccountDAO(context);
    }
    // Get Logged in Account
    public Account getLoggedInAccount() {
        int accountId = sharedPreferences.getInt(KEY_ACCOUNT_ID, -1); // Default value is -1 if not found
        if (accountId != -1) {
            return accountDAO.getAccountById(accountId);
        }
        return null;

    }
    // Check if user is logged in
    public boolean isLoggedIn() {
        return sharedPreferences.getInt(KEY_ACCOUNT_ID, -1) != -1;
    }
    // Logout Method to clear session
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_ACCOUNT_ID);
        editor.apply();
    }





}
