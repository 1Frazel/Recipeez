package com.solocarry.recipeez;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.solocarry.recipeez.api.ApiClient;
import com.solocarry.recipeez.api.Recipe;
import com.solocarry.recipeez.api.RecipeResponse;
import com.solocarry.recipeez.api.SpoonacularApi;
import com.solocarry.recipeez.database.UserDatabaseHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView mainWelcomeTV;
    private UserDatabaseHelper dbHelper;
    private BottomNavigationView bottomNavigationView;
    private SpoonacularApi spoonacularApi;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private MainPageIndicator pageIndicator;

    // Search-related views
    private View searchLayout;
    private EditText searchEditText;
    private LinearLayout searchByButton;
    private LinearLayout filterButton;
    private ImageView searchByIndicator;
    private boolean isSearchByName = true;
    private SearchFilter currentFilter;
    private FilterManager filterManager;

    private static final String API_KEY = "your_api_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeComponents();
        setupViewPager();
        setupSearchFunctionality();
        setupBottomNavigation();
        loadRecipes();
        handleUserWelcome();
    }

    private void initializeViews() {
        mainWelcomeTV = findViewById(R.id.mainWelcomeTV);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.viewPager);
        pageIndicator = findViewById(R.id.pageIndicator);

        // Initialize search-related views
        searchLayout = findViewById(R.id.searchLayout);
        searchEditText = searchLayout.findViewById(R.id.searchEditText);
        searchByButton = searchLayout.findViewById(R.id.searchByButton);
        filterButton = searchLayout.findViewById(R.id.filterButton);
        searchByIndicator = searchLayout.findViewById(R.id.searchByIndicator);
    }

    private void initializeComponents() {
        dbHelper = new UserDatabaseHelper(this);
        spoonacularApi = ApiClient.getClient().create(SpoonacularApi.class);
        viewPagerAdapter = new ViewPagerAdapter(this);
        filterManager = FilterManager.getInstance(this);
        currentFilter = filterManager.getCurrentFilter();
    }

    private void setupSearchFunctionality() {
        searchByButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, searchByButton);
            popup.getMenuInflater().inflate(R.menu.search_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.search_by_name) {
                    isSearchByName = true;
                    searchEditText.setHint("Search recipes by name");
                    return true;
                } else if (itemId == R.id.search_by_ingredients) {
                    isSearchByName = false;
                    searchEditText.setHint("Enter ingredients separated by comma");
                    return true;
                }
                return false;
            });

            popup.setOnDismissListener(menu ->
                    searchByIndicator.setImageResource(R.drawable.triangle_down));
            searchByIndicator.setImageResource(R.drawable.triangle_up);
            popup.show();
        });

        filterButton.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            View filterView = getLayoutInflater().inflate(R.layout.filter_bottom_sheet, null);
            setupFilterViews(filterView, dialog);
            dialog.setContentView(filterView);
            dialog.show();
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void setupFilterViews(View view, BottomSheetDialog dialog) {
        ChipGroup cuisineChipGroup = view.findViewById(R.id.cuisineChipGroup);
        ChipGroup mealTypeChipGroup = view.findViewById(R.id.mealTypeChipGroup);
        ChipGroup dietChipGroup = view.findViewById(R.id.dietChipGroup);
        MaterialButton applyButton = view.findViewById(R.id.applyFilters);
        MaterialButton clearButton = view.findViewById(R.id.clearFilters);

        // Restore previous selections if any
        SearchFilter savedFilter = filterManager.getCurrentFilter();
        restoreChipSelection(cuisineChipGroup, savedFilter.getCuisine());
        restoreChipSelection(mealTypeChipGroup, savedFilter.getMealType());
        restoreChipSelection(dietChipGroup, savedFilter.getDiet());

        applyButton.setOnClickListener(v -> {
            String selectedCuisine = getSelectedChipText(cuisineChipGroup);
            String selectedMealType = getSelectedChipText(mealTypeChipGroup);
            String selectedDiet = getSelectedChipText(dietChipGroup);

            filterManager.updateFilter(selectedCuisine, selectedMealType, selectedDiet);
            dialog.dismiss();
        });

        if (clearButton != null) {
            clearButton.setOnClickListener(v -> {
                cuisineChipGroup.clearCheck();
                mealTypeChipGroup.clearCheck();
                dietChipGroup.clearCheck();
                filterManager.clearFilter();
            });
        }
    }

    private void restoreChipSelection(ChipGroup chipGroup, @Nullable String value) {
        if (value != null) {
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.getText().toString().equals(value)) {
                    chip.setChecked(true);
                    break;
                }
            }
        }
    }

    @Nullable
    private String getSelectedChipText(ChipGroup chipGroup) {
        int selectedId = chipGroup.getCheckedChipId();
        if (selectedId != View.NO_ID) {
            Chip selectedChip = chipGroup.findViewById(selectedId);
            return selectedChip != null ? selectedChip.getText().toString() : null;
        }
        return null;
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            currentFilter = filterManager.getCurrentFilter();
            currentFilter.setQuery(query);
            currentFilter.setSearchByIngredients(!isSearchByName);
            navigateToBrowser();
        }
    }

    private void navigateToBrowser() {
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra("search_filter", new Gson().toJson(currentFilter));
        startActivity(intent);
    }

    private void setupViewPager() {
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                pageIndicator.setCurrentPosition(position);
            }
        });

        viewPager.setUserInputEnabled(true);
        viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }

    private void loadRecipes() {
        Call<RecipeResponse> call = spoonacularApi.searchRecipes(API_KEY, 18);
        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    viewPagerAdapter.setRecipes(response.body().getResults());
                    pageIndicator.setDotCount(viewPagerAdapter.getItemCount());
                } else {
                    Toast.makeText(MainActivity.this, "Error loading recipes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to load recipes: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_cookbook) {
                return true;
            } else if (itemId == R.id.navigation_cart) {
                return true;
            } else if (itemId == R.id.navigation_meal_planner) {
                return true;
            } else if (itemId == R.id.navigation_settings) {
                startActivity(new Intent(this, SettingActivity.class));
                finish();
            }
            return false;
        });
    }

    private void handleUserWelcome() {
        String userEmail = getIntent().getStringExtra("user_email");
        if (userEmail != null) {
            mainWelcomeTV.setText(userEmail);
        } else {
            String lastEmail = getLastRegisteredUser();
            if (lastEmail != null) {
                mainWelcomeTV.setText(lastEmail);
            } else {
                mainWelcomeTV.setText("User is not found");
            }
        }
    }

    private String getLastRegisteredUser() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"email"},
                null, null, null, null, "id DESC", "1");

        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            cursor.close();
            return email;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewPager != null) {
            viewPager.setUserInputEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewPager != null) {
            viewPager.setUserInputEnabled(true);
        }
    }
}
