package com.solocarry.recipeez;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import com.google.gson.Gson;

public class FilterManager {
    private static final String PREF_NAME = "filter_prefs";
    private static FilterManager instance;
    private final SharedPreferences prefs;
    private SearchFilter currentFilter;

    private FilterManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadSavedFilter();
    }

    public static synchronized FilterManager getInstance(Context context) {
        if (instance == null) {
            instance = new FilterManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadSavedFilter() {
        String filterJson = prefs.getString("current_filter", null);
        if (filterJson != null) {
            currentFilter = new Gson().fromJson(filterJson, SearchFilter.class);
        } else {
            currentFilter = new SearchFilter();
        }
    }

    public void saveFilter() {
        String filterJson = new Gson().toJson(currentFilter);
        prefs.edit().putString("current_filter", filterJson).apply();
    }

    // Add these necessary methods
    public SearchFilter getCurrentFilter() {
        if (currentFilter == null) {
            loadSavedFilter();
        }
        return currentFilter;
    }

    public void updateFilter(String cuisine, String mealType, String diet) {
        currentFilter.setCuisine(cuisine);
        currentFilter.setMealType(mealType);
        currentFilter.setDiet(diet);
        saveFilter(); // Save after updating
    }

    public void clearFilter() {
        currentFilter = new SearchFilter();
        saveFilter(); // Save after clearing
    }
}