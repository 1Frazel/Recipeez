package com.solocarry.recipeez;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFilter {
    // Basic Search
    private String query;
    private boolean searchByIngredients;

    // Category Filters
    private String cuisine;
    private String mealType;
    private String diet;

    // Nutrition Filters
    private Integer minCalories;
    private Integer maxCalories;
    private Integer minProtein;
    private Integer maxProtein;
    private Integer minCarbs;
    private Integer maxCarbs;
    private Integer minFat;
    private Integer maxFat;

    // Ingredient Filters
    private List<String> includeIngredients;
    private List<String> excludeIngredients;
    private Integer maxIngredients;

    // Getters and Setters
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isSearchByIngredients() {
        return searchByIngredients;
    }

    public void setSearchByIngredients(boolean searchByIngredients) {
        this.searchByIngredients = searchByIngredients;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public Integer getMinCalories() {
        return minCalories;
    }

    public void setMinCalories(Integer minCalories) {
        this.minCalories = minCalories;
    }

    public Integer getMaxCalories() {
        return maxCalories;
    }

    public void setMaxCalories(Integer maxCalories) {
        this.maxCalories = maxCalories;
    }

    public Integer getMinProtein() {
        return minProtein;
    }

    public void setMinProtein(Integer minProtein) {
        this.minProtein = minProtein;
    }

    public Integer getMaxProtein() {
        return maxProtein;
    }

    public void setMaxProtein(Integer maxProtein) {
        this.maxProtein = maxProtein;
    }

    public Integer getMinCarbs() {
        return minCarbs;
    }

    public void setMinCarbs(Integer minCarbs) {
        this.minCarbs = minCarbs;
    }

    public Integer getMaxCarbs() {
        return maxCarbs;
    }

    public void setMaxCarbs(Integer maxCarbs) {
        this.maxCarbs = maxCarbs;
    }

    public Integer getMinFat() {
        return minFat;
    }

    public void setMinFat(Integer minFat) {
        this.minFat = minFat;
    }

    public Integer getMaxFat() {
        return maxFat;
    }

    public void setMaxFat(Integer maxFat) {
        this.maxFat = maxFat;
    }

    public List<String> getIncludeIngredients() {
        return includeIngredients;
    }

    public void setIncludeIngredients(List<String> includeIngredients) {
        this.includeIngredients = includeIngredients;
    }

    public List<String> getExcludeIngredients() {
        return excludeIngredients;
    }

    public void setExcludeIngredients(List<String> excludeIngredients) {
        this.excludeIngredients = excludeIngredients;
    }

    public Integer getMaxIngredients() {
        return maxIngredients;
    }

    public void setMaxIngredients(Integer maxIngredients) {
        this.maxIngredients = maxIngredients;
    }

    public Map<String, String> toQueryMap() {
        Map<String, String> queryMap = new HashMap<>();

        if (query != null) {
            if (searchByIngredients) {
                queryMap.put("ingredients", query);
            } else {
                queryMap.put("query", query);
            }
        }

        if (cuisine != null) queryMap.put("cuisine", cuisine);
        if (mealType != null) queryMap.put("type", mealType);
        if (diet != null) queryMap.put("diet", diet);

        // Add nutrition filters
        if (minCalories != null) queryMap.put("minCalories", minCalories.toString());
        if (maxCalories != null) queryMap.put("maxCalories", maxCalories.toString());
        if (minProtein != null) queryMap.put("minProtein", minProtein.toString());
        if (maxProtein != null) queryMap.put("maxProtein", maxProtein.toString());
        if (minCarbs != null) queryMap.put("minCarbs", minCarbs.toString());
        if (maxCarbs != null) queryMap.put("maxCarbs", maxCarbs.toString());
        if (minFat != null) queryMap.put("minFat", minFat.toString());
        if (maxFat != null) queryMap.put("maxFat", maxFat.toString());

        // Add ingredient filters
        if (includeIngredients != null && !includeIngredients.isEmpty()) {
            queryMap.put("includeIngredients", String.join(",", includeIngredients));
        }
        if (excludeIngredients != null && !excludeIngredients.isEmpty()) {
            queryMap.put("excludeIngredients", String.join(",", excludeIngredients));
        }
        if (maxIngredients != null) {
            queryMap.put("maxIngredients", maxIngredients.toString());
        }

        return queryMap;
    }
}