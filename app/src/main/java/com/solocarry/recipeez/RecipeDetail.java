package com.solocarry.recipeez;

import android.content.Context;
import android.content.Intent;

import java.util.List;

public class RecipeDetail {
    private int id;
    private String title;
    private String image;
    private int servings;
    private int readyInMinutes;
    private List<ExtendedIngredient> extendedIngredients;
    private String summary;
    private List<AnalyzedInstruction> analyzedInstructions;

    // Getters and setters

    public String getTitle() { return title; }
    public String getImage() { return image; }
    public int getServings() { return servings; }
    public int getReadyInMinutes() { return readyInMinutes; }
    public List<ExtendedIngredient> getExtendedIngredients() { return extendedIngredients; }
    public String getSummary() { return summary; }
    public List<AnalyzedInstruction> getAnalyzedInstructions() { return analyzedInstructions; }

    public static class ExtendedIngredient {
        private String original;
        private double amount;
        private String unit;

        public String getOriginal() { return original; }
        public double getAmount() { return amount; }
        public String getUnit() { return unit; }
    }

    public static class AnalyzedInstruction {
        private List<Step> steps;

        public List<Step> getSteps() { return steps; }

        public static class Step {
            private int number;
            private String step;

            public int getNumber() { return number; }
            public String getStep() { return step; }
        }
    }

    // Navigation helper method
    public static void navigate(Context context, int recipeId) {
        Intent intent = new Intent(context, RecipeActivity.class);
        intent.putExtra("recipe_id", recipeId);
        context.startActivity(intent);
    }
}
