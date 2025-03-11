package com.example.test1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test1.dao.AccountDAO;
import com.example.test1.entity.Account;
import com.example.test1.manager.SessionManager;
import com.example.test1.validation.Validation;

public class ChangeProfleActivity extends AppCompatActivity {
    private static final String TAG = "ChangeProfleActivity";
    private EditText etEmail, etPhoneNumber;
    private Button btnSave;
    private SessionManager sessionManager;
    private Account loggedInAccount;
    private AccountDAO accountDAO;
    private Validation validation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_profle);
        // Initialize views
        etEmail = findViewById(R.id.et_email);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnSave = findViewById(R.id.btn_save);
        accountDAO = new AccountDAO(this);
        // Initialize session manager
        sessionManager = new SessionManager(this);
        // Initialize validation
        validation = new Validation();
        // Get the user's account information from the session manager
        loggedInAccount = sessionManager.getLoggedInAccount();
        // Load the account information into the EditText fields
        loadAccountInfo();
        // Set button click listener
        btnSave.setOnClickListener(v -> saveAccountInfo() );
            // Get the updated email and phone number from the EditText fields
           ;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    // Load the user's account information into the EditText fields
    private void loadAccountInfo() {
        if(sessionManager.isLoggedIn()) {

            int accountId = sessionManager.getLoggedInAccount().getAccountId();
            loggedInAccount = accountDAO.getAccountById(accountId);
            if (loggedInAccount != null) {
                etEmail.setText(loggedInAccount.getEmail());
                etPhoneNumber.setText(loggedInAccount.getPhoneNumber());
            }
            else {
                Toast.makeText(this, "Error retrieving account information", Toast.LENGTH_SHORT).show();
            }



        }
        else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
        }
    }
    // Save the updated account information
    private void saveAccountInfo() {
        if (loggedInAccount == null) {

            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the updated email and phone number from the EditText fields
        String updatedEmail = etEmail.getText().toString();
        String updatedPhoneNumber = etPhoneNumber.getText().toString();
        // Update the account
        if(updatedEmail.isEmpty() || updatedPhoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;

        }
        if(!validation.isValidEmail(updatedEmail)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }
        loggedInAccount.setEmail(updatedEmail);
        loggedInAccount.setPhoneNumber(updatedPhoneNumber);
        accountDAO.updateAccount(loggedInAccount);
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

}