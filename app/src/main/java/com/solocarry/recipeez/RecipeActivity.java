package com.solocarry.recipeez;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.solocarry.recipeez.api.ApiClient;
import com.solocarry.recipeez.api.SpoonacularApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeActivity extends AppCompatActivity {
    private static final String API_KEY = "66fb3ff1a4a643298d466a07e1380f38";
    private SpoonacularApi spoonacularApi;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView recipeImageView;
    private TextView recipeTitleTV;
    private ProgressBar progressBar;
    private RecipeDetail recipeDetail;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        initializeViews();
        setupSpoonacularApi();
        setupBottomNavigation();
        loadRecipeDetail();
    }

    private void initializeViews() {
        recipeImageView = findViewById(R.id.recipeImageView);
        recipeTitleTV = findViewById(R.id.recipeTitleTV);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void setupSpoonacularApi() {
        spoonacularApi = ApiClient.getClient().create(SpoonacularApi.class);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(0); // Clear any selection

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else if (itemId == R.id.navigation_cookbook) {
                // TODO: Implement navigation
            } else if (itemId == R.id.navigation_cart) {
                // TODO: Implement navigation
            } else if (itemId == R.id.navigation_meal_planner) {
                // TODO: Implement navigation
            } else if (itemId == R.id.navigation_settings) {
                startActivity(new Intent(this, SettingActivity.class));
                finish();
            }
            return false; // Always return false to prevent selection
        });
    }

    private void loadRecipeDetail() {
        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        if (recipeId == -1) {
            Toast.makeText(this, "Invalid recipe", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showLoading(true);
        spoonacularApi.getRecipeDetail(recipeId, API_KEY).enqueue(new Callback<RecipeDetail>() {
            @Override
            public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    recipeDetail = response.body();
                    updateUI();
                } else {
                    Toast.makeText(RecipeActivity.this,
                            "Error loading recipe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeDetail> call, Throwable t) {
                showLoading(false);
                Toast.makeText(RecipeActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        recipeTitleTV.setText(recipeDetail.getTitle());

        Glide.with(this)
                .load(recipeDetail.getImage())
                .into(recipeImageView);

        setupViewPager();
    }

    private void setupViewPager() {
        RecipeViewPagerAdapter pagerAdapter = new RecipeViewPagerAdapter(this, recipeDetail);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Overview");
                    break;
                case 1:
                    tab.setText("Ingredients");
                    break;
                case 2:
                    tab.setText("Directions");
                    break;
            }
        }).attach();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        viewPager.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}