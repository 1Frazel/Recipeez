package com.solocarry.recipeez.api;

import com.solocarry.recipeez.RecipeDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularApi {
    @GET("recipes/complexSearch")
    Call<RecipeResponse> searchRecipes(
            @Query("apiKey") String apiKey,
            @Query("number") int number
    );

    @GET("recipes/complexSearch")
    Call<RecipeResponse> searchRecipes(
            @Query("apiKey") String apiKey,
            @Query("query") String query,
            @Query("cuisine") String cuisine,
            @Query("type") String type,
            @Query("diet") String diet,
            @Query("minCalories") Integer minCalories,
            @Query("maxCalories") Integer maxCalories,
            @Query("number") int number
    );

    @GET("recipes/findByIngredients")
    Call<List<Recipe>> searchByIngredients(
            @Query("apiKey") String apiKey,
            @Query("ingredients") String ingredients,
            @Query("number") int number
    );

    @GET("recipes/complexSearch")
    Call<RecipeResponse> searchByIngredientsWithFilter(
            @Query("apiKey") String apiKey,
            @Query("includeIngredients") String ingredients,
            @Query("cuisine") String cuisine,
            @Query("type") String type,
            @Query("diet") String diet,
            @Query("minCalories") Integer minCalories,
            @Query("maxCalories") Integer maxCalories,
            @Query("number") int number
    );

    @GET("recipes/{id}/information")
    Call<RecipeDetail> getRecipeDetail(
            @Path("id") int recipeId,
            @Query("apiKey") String apiKey
    );
}
