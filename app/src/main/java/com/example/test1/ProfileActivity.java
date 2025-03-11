package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test1.entity.Account;
import com.example.test1.manager.SessionManager;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private TextView tvAccountNameHeader, tvAccountName, tvEmail, tvPhoneNumber, tvEditProfile;
    private Button btnLogout;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        //Initialize views
        tvAccountNameHeader = findViewById(R.id.tv_account_name_header);
        tvAccountName = findViewById(R.id.tv_account_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        btnLogout = findViewById(R.id.btn_logout);
        tvEditProfile = findViewById(R.id.tv_edit_profile);
        //Initialize session manager
        sessionManager = new SessionManager(this);
        // Get the user's account information from the session manager
        Account loggedInAccount = sessionManager.getLoggedInAccount();
        if (loggedInAccount != null) {
            tvAccountNameHeader.setText(loggedInAccount.getUsername());
            tvAccountName.setText(loggedInAccount.getUsername());
            tvEmail.setText(loggedInAccount.getEmail());
            tvPhoneNumber.setText(loggedInAccount.getPhoneNumber());
        } else {
            // This should never happen, but just in case
            Toast.makeText(this, "Error retrieving account information", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        // Set Click listener for the "Edit User Profile" text
        tvEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangeProfleActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}