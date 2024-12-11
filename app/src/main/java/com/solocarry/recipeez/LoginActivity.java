package com.solocarry.recipeez;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.solocarry.recipeez.database.UserDatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailET, loginPasswordET;
    private Button loginLoginBtn, loginRegisterBtn;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize database helper
        dbHelper = new UserDatabaseHelper(this);

        // Check if user is already logged in
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (dbHelper.isUserLoggedIn(db)) {
            String email = dbHelper.getLastLoggedInUserEmail(db);
            navigateToMain(email);
            return;
        }

        // Initialize views
        loginEmailET = findViewById(R.id.loginEmailET);
        loginPasswordET = findViewById(R.id.loginPasswordET);
        loginLoginBtn = findViewById(R.id.loginLoginBtn);
        loginRegisterBtn = findViewById(R.id.loginRegisterBtn);

        // Handle login
        loginLoginBtn.setOnClickListener(v -> {
            String email = loginEmailET.getText().toString().trim();
            String password = loginPasswordET.getText().toString().trim();

            // Validate inputs
            if (TextUtils.isEmpty(email) || !email.contains("@")) {
                Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Authenticate user
            if (authenticateUser(email, password)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                navigateToMain(email);
            } else {
                Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate to RegisterActivity
        loginRegisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean authenticateUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"email", "password"},
                "email = ? AND password = ?",
                new String[]{email, password}, null, null, null);

        boolean isAuthenticated = cursor.moveToFirst();
        cursor.close();

        if (isAuthenticated) {
            // Save user session
            dbHelper.saveUserSession(db, email);
        }

        return isAuthenticated;
    }

    private void navigateToMain(String email) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("user_email", email);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}