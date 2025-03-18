package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test1.dao.AccountDAO;
import com.example.test1.validation.Validation;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etRepeatPassword, etPhone, etEmail, etAddress;
    private ImageView ivPasswordVisibility, ivRepeatPasswordVisibility;
    private Button btnRegister;
    private TextView tvLoginLink;
    private AccountDAO accountDAO;
    private boolean isPasswordVisible = false;
    private boolean isRepeatPasswordVisible = false;
    private Validation validation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize views
        etUsername = findViewById(R.id.et_register_username);
        etPassword = findViewById(R.id.et_register_password);
        etRepeatPassword = findViewById(R.id.et_register_repeat_password);
        etPhone = findViewById(R.id.et_register_phone);
        etEmail = findViewById(R.id.et_register_email);
        etAddress = findViewById(R.id.et_register_address);
        ivPasswordVisibility = findViewById(R.id.iv_register_password_visibility);
        ivRepeatPasswordVisibility = findViewById(R.id.iv_register_repeat_password_visibility);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);

        // Initialize DAO
        accountDAO = new AccountDAO(this);
        validation = new Validation();

        // Password visibility toggle
        ivPasswordVisibility.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivPasswordVisibility.setImageResource(R.drawable.ic_visibility);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivPasswordVisibility.setImageResource(R.drawable.ic_visibility);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Repeat Password visibility toggle
        ivRepeatPasswordVisibility.setOnClickListener(v -> {
            isRepeatPasswordVisible = !isRepeatPasswordVisible;
            if (isRepeatPasswordVisible) {
                etRepeatPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivRepeatPasswordVisibility.setImageResource(R.drawable.ic_visibility);
            } else {
                etRepeatPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivRepeatPasswordVisibility.setImageResource(R.drawable.ic_visibility);
            }
            etRepeatPassword.setSelection(etRepeatPassword.getText().length());
        });

        // Set click listener for Register button
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String repeatPassword = etRepeatPassword.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            int roleId = 1; // Hardcode roleId (e.g., 1 for regular user)

            // Validation
            if (username.isEmpty()) {
                etUsername.setError("Username is required");
                etUsername.requestFocus();
                return;
            }
            if(username.equals(accountDAO.getAccountByUsername(username))) {
                etUsername.setError("Username already exists");
                etUsername.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            }
            if (repeatPassword.isEmpty()) {
                etRepeatPassword.setError("Repeat Password is required");
                etRepeatPassword.requestFocus();
                return;
            }
            if (!password.equals(repeatPassword)) {
                etRepeatPassword.setError("Passwords do not match");
                etRepeatPassword.requestFocus();
                return;
            }
            if (phone.isEmpty()) {
                etPhone.setError("Phone number is required");
                etPhone.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }
            if(validation.isValidEmail(email) == false){
                etEmail.setError("Email is not valid");
                etEmail.requestFocus();
                return;
            }

            if (address.isEmpty()) {
                etAddress.setError("Address is required");
                etAddress.requestFocus();
                return;
            }

            // Register the account
            long newAccountId = accountDAO.register(username, password, phone, email, address, roleId);
            if (newAccountId != -1) {
                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                // Navigate to LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Registration failed: Username already taken", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for Login link
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accountDAO != null) {
            accountDAO.close();
        }
    }

    public void onTermsClick(View view) {
        Toast.makeText(this, "Terms and Conditions clicked", Toast.LENGTH_SHORT).show();
        // Optionally navigate to a TermsActivity or show a dialog
    }
}