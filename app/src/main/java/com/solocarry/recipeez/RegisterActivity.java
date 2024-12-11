package com.solocarry.recipeez;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.solocarry.recipeez.database.UserDatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmailET, registerPasswordET, registerConfirmET;
    private Button registerRegisterBtn;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        registerEmailET = findViewById(R.id.registerEmailET);
        registerPasswordET = findViewById(R.id.registerPasswordET);
        registerConfirmET = findViewById(R.id.registerConfirmET);
        registerRegisterBtn = findViewById(R.id.registerRegisterBtn);
        Button registerLoginBtn = findViewById(R.id.registerLoginBtn);

        // Navigate to LoginActivity
        registerLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Handle registration
        registerRegisterBtn.setOnClickListener(v -> {
            String email = registerEmailET.getText().toString().trim();
            String password = registerPasswordET.getText().toString().trim();
            String confirmPassword = registerConfirmET.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(email) || !email.contains("@")) {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password) || password.length() < 8) {
                Toast.makeText(this, "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to database
            if (saveUserToDatabase(email, password)) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                navigateToMain(email);
            } else {
                Toast.makeText(this, "Registration failed. Email might already be registered.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean saveUserToDatabase(String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);

        long result = db.insert("users", null, values);

        if (result != -1) {
            // Save user session after successful registration
            dbHelper.saveUserSession(db, email);
        }

        return result != -1;
    }

    private void navigateToMain(String email) {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
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