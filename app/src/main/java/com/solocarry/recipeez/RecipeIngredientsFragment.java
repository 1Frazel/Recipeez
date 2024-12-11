package com.solocarry.recipeez;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.solocarry.recipeez.RecipeDetail;

public class RecipeIngredientsFragment extends Fragment {
    private RecipeDetail recipeDetail;

    public static RecipeIngredientsFragment newInstance(RecipeDetail recipeDetail) {
        RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
        Bundle args = new Bundle();
        args.putString("recipe_detail", new Gson().toJson(recipeDetail));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);

        if (getArguments() != null) {
            recipeDetail = new Gson().fromJson(
                    getArguments().getString("recipe_detail"),
                    RecipeDetail.class
            );

            LinearLayout ingredientsContainer = view.findViewById(R.id.ingredientsContainer);

            for (RecipeDetail.ExtendedIngredient ingredient : recipeDetail.getExtendedIngredients()) {
                TextView ingredientTV = new TextView(requireContext());
                ingredientTV.setText(ingredient.getOriginal());
                ingredientTV.setTextSize(16);
                int padding = (int) (8 * requireContext().getResources().getDisplayMetrics().density);
                ingredientTV.setPadding(0, padding, 0, padding);
                ingredientsContainer.addView(ingredientTV);
            }
        }

        return view;
    }
}