package com.example.test1.manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.adapter.AccountAdapter;
import com.example.test1.dtb.DatabaseHelper;
import com.example.test1.entity.Account;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AccountAdapter adapter;
    private List<Account> accountList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo accountList
        accountList = new ArrayList<>();

        dbHelper = new DatabaseHelper(this);
        loadAccounts();

        // Thiết lập RecyclerView
        RecyclerView recyclerViewAccountList = findViewById(R.id.recyclerViewAccountList);
        recyclerViewAccountList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AccountAdapter(accountList, this); // Khởi tạo adapter
        recyclerViewAccountList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manager_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_user_profile) {// Handle Edit User Profile click
            System.out.println("Edit User Profile clicked");
            return true;
        } else if (itemId == R.id.menu_services) {// Handle Services click
            System.out.println("Services clicked");
            return true;
        } else if (itemId == R.id.menu_setting) {// Handle Setting click
            System.out.println("Setting clicked");
            return true;
        } else if (itemId == R.id.menu_management) {// Handle Management click
            System.out.println("Management clicked");
            return true;
        } else if (itemId == R.id.menu_req_gps) {// Handle Request GPS click
            System.out.println("Request GPS clicked");
            return true;
        } else if (itemId == R.id.menu_send_notification) {// Handle Send Notification click
            System.out.println("Send Notification clicked");
            return true;
        } else if (itemId == R.id.menu_logout) {// Handle Logout click
            System.out.println("Logout clicked");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.manager_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override //To enable icon in action bar menu
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            try {
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(menu, true);
            } catch (Exception e) {
                Log.e("ViewUserProfileActivity", "onMenuOpened(): " + e.getMessage());
            }
        }
        return super.onMenuOpened(featureId, menu);
    }


    private void loadAccounts() {
        // Kiểm tra null và khởi tạo nếu cần
        if (accountList == null) {
            accountList = new ArrayList<>();
        } else {
            accountList.clear();
        }

        Cursor cursor = dbHelper.getAllAccounts();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int accountId = cursor.getInt(cursor.getColumnIndex("accountId"));
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("username"));
                @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
                @SuppressLint("Range") int roleId = cursor.getInt(cursor.getColumnIndex("roleId"));

                Account account = new Account(accountId, username, password, phoneNumber, email, address, roleId);
                accountList.add(account);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}