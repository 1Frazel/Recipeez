package com.solocarry.recipeez;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.solocarry.recipeez.database.UserDatabaseHelper;

public class SettingActivity extends AppCompatActivity {
    private UserDatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private TextView userEmailTV;
    private MaterialButton logoutButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initializeViews();
        setupDatabase();
        loadUserData();
        setupListeners();
        setupBottomNavigation();
    }

    private void initializeViews() {
        userEmailTV = findViewById(R.id.userEmailTV);
        logoutButton = findViewById(R.id.logoutButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupDatabase() {
        dbHelper = new UserDatabaseHelper(this);
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
    }

    private void loadUserData() {
        String userEmail = getUserEmail();
        userEmailTV.setText(userEmail);

    }

    private String getUserEmail() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String email = dbHelper.getLastLoggedInUserEmail(db);
        return email != null ? email : "No user logged in";
    }

    private void setupListeners() {
        logoutButton.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("No", null)
                .show();
    }

    private void performLogout() {
        // Clear user session
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.clearUserSession(db);

        // Clear preferences if needed
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user_email");
        editor.apply();

        // Navigate to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_settings);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_cookbook) {
                // TODO: Navigate to cookbook
                return true;
            } else if (itemId == R.id.navigation_cart) {
                // TODO: Navigate to cart
                return true;
            } else if (itemId == R.id.navigation_meal_planner) {
                // TODO: Navigate to meal planner
                return true;
            } else if (itemId == R.id.navigation_settings) {
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}