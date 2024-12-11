package com.solocarry.recipeez.api;

import java.util.List;

public class RecipeResponse {
    private List<Recipe> results;
    private int number;
    private int totalResults;

    public List<Recipe> getResults() {
        return results;
    }

    public int getNumber() {
        return number;
    }

    public int getTotalResults() {
        return totalResults;
    }
}