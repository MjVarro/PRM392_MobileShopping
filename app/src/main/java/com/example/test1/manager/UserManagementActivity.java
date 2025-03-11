package com.example.test1.manager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.adapter.AccountAdapter;
import com.example.test1.dao.AccountDAO;
import com.example.test1.dtb.DatabaseHelper;
import com.example.test1.entity.Account;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {
    private static final String TAG = "UserManagementActivity";
    private RecyclerView recyclerView;
    private AccountAdapter adapter;
    private List<Account> accountList;
    private DatabaseHelper dbHelper;
    private AccountDAO accountDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // Initialize DAO and load accounts
        dbHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(this);
        accountDAO.insertSampleAccounts(); // Thêm dữ liệu mẫu
        accountList = new ArrayList<>();
        loadAccounts();

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAccountList);
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView not found in layout");
            Toast.makeText(this, "RecyclerView not found", Toast.LENGTH_SHORT).show();
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AccountAdapter(accountList, this, this::deleteAccount); // Truyền callback để xóa
        recyclerView.setAdapter(adapter);
    }

    // Ghi đè onBackPressed để quay lại SelectManagementActivity
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SelectManagementActivity.class));
        finish(); // Kết thúc activity hiện tại
        super.onBackPressed();
    }

    private void loadAccounts() {
        if (accountList == null) {
            accountList = new ArrayList<>();
        } else {
            accountList.clear();
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Accounts", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int accountId = cursor.getInt(cursor.getColumnIndexOrThrow("accountId"));
                    String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                    String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                    String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                    int roleId = cursor.getInt(cursor.getColumnIndexOrThrow("roleId"));

                    Account account = new Account(accountId, username, password, phoneNumber, email, address, roleId);
                    accountList.add(account);
                    Log.d(TAG, "Loaded account: " + username + ", ID: " + accountId);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading account: " + e.getMessage(), e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.w(TAG, "No accounts found in database");
        }
        db.close();

        if (adapter != null) {
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Adapter updated with " + accountList.size() + " accounts");
        }
    }

    private void deleteAccount(int accountId) {
        boolean deleted = accountDAO.deleteAccount(accountId);
        if (deleted) {
            Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
            loadAccounts(); // Tải lại danh sách sau khi xóa
        } else {
            Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show();
        }
    }
}