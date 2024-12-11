package com.solocarry.recipeez;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.solocarry.recipeez.api.Recipe;

import java.util.ArrayList;
import java.util.List;

public class BrowserRecipeAdapter extends RecyclerView.Adapter<BrowserRecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipes;
    private Context context;

    public BrowserRecipeAdapter(Context context) {
        this.context = context;
        this.recipes = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.browser_recipe_card_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.recipeTitleView.setText(recipe.getTitle());

        Glide.with(context)
                .load(recipe.getImage())
                .centerCrop()
                .into(holder.recipeImageView);

        // Click listener for the whole card
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra("recipe_id", recipe.getId());
            context.startActivity(intent);
        });

        // Cart button click listener
        holder.cartButton.setOnClickListener(v -> {
            // TODO: Implement shopping cart functionality
            // For now, you can show a toast message
            Toast.makeText(context, "Added to cart: " + recipe.getTitle(),
                    Toast.LENGTH_SHORT).show();
        });

        // Cookbook button click listener
        holder.cookbookButton.setOnClickListener(v -> {
            // TODO: Implement cookbook functionality
            // For now, you can show a toast message
            Toast.makeText(context, "Added to cookbook: " + recipe.getTitle(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImageView;
        TextView recipeTitleView;
        ImageButton cartButton;
        ImageButton cookbookButton;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.recipeImageView);
            recipeTitleView = itemView.findViewById(R.id.recipeTitleView);
            cartButton = itemView.findViewById(R.id.cartButton);
            cookbookButton = itemView.findViewById(R.id.cookbookButton);
        }
    }
}