package com.solocarry.recipeez;

import android.content.Intent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

public class SearchBarManager {
    private final MainActivity activity;
    private final EditText searchEditText;
    private final LinearLayout searchByButton;
    private final LinearLayout filterButton;
    private final ImageView searchByIndicator;
    private boolean isSearchByName = true;
    private SearchFilter currentFilter;

    public SearchBarManager(MainActivity activity) {
        this.activity = activity;
        this.searchEditText = activity.findViewById(R.id.searchEditText);
        this.searchByButton = activity.findViewById(R.id.searchByButton);
        this.filterButton = activity.findViewById(R.id.filterButton);
        this.searchByIndicator = activity.findViewById(R.id.searchByIndicator);
        this.currentFilter = new SearchFilter();

        setupSearchBy();
        setupFilter();
        setupSearchAction();
    }

    private void setupSearchBy() {
        searchByButton.setOnClickListener(v -> {
            // Create popup menu
            PopupMenu popup = new PopupMenu(activity, searchByButton);
            popup.getMenuInflater().inflate(R.menu.search_menu, popup.getMenu());

            // Handle menu item clicks
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

            // Show popup and rotate indicator
            popup.setOnDismissListener(menu ->
                    searchByIndicator.setImageResource(R.drawable.triangle_down));
            searchByIndicator.setImageResource(R.drawable.triangle_up);
            popup.show();
        });
    }

    private void setupFilter() {
        filterButton.setOnClickListener(v -> {
            // Show bottom sheet dialog
            BottomSheetDialog dialog = new BottomSheetDialog(activity);
            View filterView = activity.getLayoutInflater()
                    .inflate(R.layout.filter_bottom_sheet, null);

            // Setup filter view components
            setupFilterViews(filterView, dialog);

            dialog.setContentView(filterView);
            dialog.show();
        });
    }

    private void setupFilterViews(View view, BottomSheetDialog dialog) {
        // Get filter components
        ChipGroup cuisineChipGroup = view.findViewById(R.id.cuisineChipGroup);
        ChipGroup mealTypeChipGroup = view.findViewById(R.id.mealTypeChipGroup);
        ChipGroup dietChipGroup = view.findViewById(R.id.dietChipGroup);
        MaterialButton applyButton = view.findViewById(R.id.applyFilters);

        // Set current selections if any
        if (currentFilter.getCuisine() != null) {
            for (int i = 0; i < cuisineChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) cuisineChipGroup.getChildAt(i);
                if (chip.getText().toString().equals(currentFilter.getCuisine())) {
                    chip.setChecked(true);
                    break;
                }
            }
        }
        // Similar for meal type and diet

        // Handle apply button
        applyButton.setOnClickListener(v -> {
            // Update current filter with selections
            updateFilterFromSelections(cuisineChipGroup, mealTypeChipGroup, dietChipGroup);
            dialog.dismiss();
        });
    }

    private void updateFilterFromSelections(ChipGroup cuisineGroup,
                                            ChipGroup mealTypeGroup,
                                            ChipGroup dietGroup) {
        // Update filter based on selected chips
        Chip selectedCuisine = cuisineGroup.findViewById(cuisineGroup.getCheckedChipId());
        if (selectedCuisine != null) {
            currentFilter.setCuisine(selectedCuisine.getText().toString());
        }

        // Similar for meal type and diet
    }

    private void setupSearchAction() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            currentFilter.setQuery(query);
            currentFilter.setSearchByIngredients(!isSearchByName);
            navigateToBrowser();
        }
    }

    private void navigateToBrowser() {
        Intent intent = new Intent(activity, BrowserActivity.class);
        intent.putExtra("search_filter", new Gson().toJson(currentFilter));
        activity.startActivity(intent);
    }
}