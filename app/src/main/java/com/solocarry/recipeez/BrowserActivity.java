package com.solocarry.recipeez;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.solocarry.recipeez.api.ApiClient;
import com.solocarry.recipeez.api.Recipe;
import com.solocarry.recipeez.api.RecipeResponse;
import com.solocarry.recipeez.api.SpoonacularApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowserActivity extends AppCompatActivity {
    private RecyclerView searchResultsRV;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private View searchLayout;
    private SearchFilter currentFilter;
    private FilterManager filterManager;
    private static final String TAG = "BrowserActivity";
    private SpoonacularApi spoonacularApi;
    private static final String API_KEY = "66fb3ff1a4a643298d466a07e1380f38";
    private Call<RecipeResponse> currentCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browser);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeComponents();
        setupRecyclerView();
        setupSearchFunctionality();
        setupBottomNavigation();
        getSearchFilterFromIntent();
        performSearch();
    }

    private void initializeViews() {
        searchResultsRV = findViewById(R.id.searchResultsRV);
        progressBar = findViewById(R.id.progressBar);
        emptyStateText = findViewById(R.id.emptyStateText);
        searchLayout = findViewById(R.id.searchLayout);
    }

    private void initializeComponents() {
        spoonacularApi = ApiClient.getClient().create(SpoonacularApi.class);
        filterManager = FilterManager.getInstance(this);
        currentFilter = new SearchFilter();
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        searchResultsRV.setLayoutManager(layoutManager);
        searchResultsRV.setHasFixedSize(true);

        int spacing = (int) (16 * getResources().getDisplayMetrics().density);
        searchResultsRV.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = spacing;
                outRect.right = spacing;
                outRect.top = spacing;
                outRect.bottom = spacing;
            }
        });
    }

    private void setupSearchFunctionality() {
        EditText searchEditText = searchLayout.findViewById(R.id.searchEditText);
        LinearLayout searchByButton = searchLayout.findViewById(R.id.searchByButton);
        LinearLayout filterButton = searchLayout.findViewById(R.id.filterButton);
        ImageView searchByIndicator = searchLayout.findViewById(R.id.searchByIndicator);

        searchByButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, searchByButton);
            popup.getMenuInflater().inflate(R.menu.search_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.search_by_name) {
                    currentFilter.setSearchByIngredients(false);
                    searchEditText.setHint("Search recipes by name");
                    return true;
                } else if (itemId == R.id.search_by_ingredients) {
                    currentFilter.setSearchByIngredients(true);
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
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    currentFilter = filterManager.getCurrentFilter();
                    currentFilter.setQuery(query);
                    performSearch();
                }
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

        SearchFilter savedFilter = filterManager.getCurrentFilter();
        restoreChipSelection(cuisineChipGroup, savedFilter.getCuisine());
        restoreChipSelection(mealTypeChipGroup, savedFilter.getMealType());
        restoreChipSelection(dietChipGroup, savedFilter.getDiet());

        applyButton.setOnClickListener(v -> {
            String selectedCuisine = getSelectedChipText(cuisineChipGroup);
            String selectedMealType = getSelectedChipText(mealTypeChipGroup);
            String selectedDiet = getSelectedChipText(dietChipGroup);

            filterManager.updateFilter(selectedCuisine, selectedMealType, selectedDiet);
            currentFilter = filterManager.getCurrentFilter();
            currentFilter.setQuery(((EditText) searchLayout.findViewById(R.id.searchEditText)).getText().toString());
            dialog.dismiss();
            performSearch();
        });

        if (clearButton != null) {
            clearButton.setOnClickListener(v -> {
                cuisineChipGroup.clearCheck();
                mealTypeChipGroup.clearCheck();
                dietChipGroup.clearCheck();
                filterManager.clearFilter();
                currentFilter = filterManager.getCurrentFilter();
                currentFilter.setQuery(((EditText) searchLayout.findViewById(R.id.searchEditText)).getText().toString());
                performSearch();
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

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(0); // Clear any selection

        bottomNav.setOnItemSelectedListener(item -> {
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

    private void getSearchFilterFromIntent() {
        String filterJson = getIntent().getStringExtra("search_filter");
        if (filterJson != null) {
            currentFilter = new Gson().fromJson(filterJson, SearchFilter.class);

            // Set the search text from the filter
            EditText searchEditText = searchLayout.findViewById(R.id.searchEditText);
            searchEditText.setText(currentFilter.getQuery());
        }
    }

    private void performSearch() {
        if (currentFilter.isSearchByIngredients()) {
            if (hasActiveFilters()) {
                Call<RecipeResponse> call = spoonacularApi.searchByIngredientsWithFilter(
                        API_KEY,
                        currentFilter.getQuery(),
                        currentFilter.getCuisine(),
                        currentFilter.getMealType(),
                        currentFilter.getDiet(),
                        currentFilter.getMinCalories(),
                        currentFilter.getMaxCalories(),
                        20
                );
                executeSearch(call);
            } else {
                Call<List<Recipe>> call = spoonacularApi.searchByIngredients(
                        API_KEY,
                        currentFilter.getQuery(),
                        20
                );
                executeIngredientSearch(call);
            }
        } else {
            Call<RecipeResponse> call = spoonacularApi.searchRecipes(
                    API_KEY,
                    currentFilter.getQuery(),
                    currentFilter.getCuisine(),
                    currentFilter.getMealType(),
                    currentFilter.getDiet(),
                    currentFilter.getMinCalories(),
                    currentFilter.getMaxCalories(),
                    20
            );
            executeSearch(call);
        }
    }

    private boolean hasActiveFilters() {
        return currentFilter.getCuisine() != null ||
                currentFilter.getMealType() != null ||
                currentFilter.getDiet() != null ||
                currentFilter.getMinCalories() != null ||
                currentFilter.getMaxCalories() != null;
    }

    private void executeIngredientSearch(Call<List<Recipe>> call) {
        showLoading(true);
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmpty()) {
                        showEmptyState();
                    } else {
                        BrowserRecipeAdapter adapter = new BrowserRecipeAdapter(BrowserActivity.this);
                        adapter.setRecipes(response.body());
                        searchResultsRV.setAdapter(adapter);
                    }
                } else {
                    Log.e(TAG, "Error code: " + response.code());
                    showError("Error loading recipes. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Network error", t);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void executeSearch(Call<RecipeResponse> call) {
        showLoading(true);
        currentCall = call;

        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getResults().isEmpty()) {
                        showEmptyState();
                    } else {
                        BrowserRecipeAdapter adapter = new BrowserRecipeAdapter(BrowserActivity.this);
                        adapter.setRecipes(response.body().getResults());
                        searchResultsRV.setAdapter(adapter);
                    }
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                showLoading(false);
                handleNetworkError(t);
            }
        });
    }

    private void showEmptyState() {
        emptyStateText.setVisibility(View.VISIBLE);
        searchResultsRV.setVisibility(View.GONE);
    }

    private void handleApiError(Response<RecipeResponse> response) {
        String errorMessage = "Error loading recipes. Please try again.";
        if (response.code() == 401) {
            errorMessage = "Unauthorized access. Please check your API key.";
        } else if (response.code() == 429) {
            errorMessage = "Too many requests. Please try again later.";
        }
        showError(errorMessage);
    }

    private void handleNetworkError(Throwable t) {
        showError("Network error: " + t.getMessage());
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        searchResultsRV.setVisibility(show ? View.GONE : View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentCall != null) {
            currentCall.cancel();
        }
    }
}