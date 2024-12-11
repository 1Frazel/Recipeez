package com.solocarry.recipeez;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.solocarry.recipeez.api.Recipe;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private List<List<Recipe>> pages;
    private Context context;

    public ViewPagerAdapter(Context context) {
        this.context = context;
        this.pages = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new ViewHolder(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<Recipe> pageRecipes = pages.get(position);
        RecipeAdapter adapter = new RecipeAdapter(context);
        holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        holder.recyclerView.setAdapter(adapter);
        adapter.setRecipes(pageRecipes);

        adapter.setOnRecipeClickListener(recipe -> {
            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra("recipe_id", recipe.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public void setRecipes(List<Recipe> recipes) {
        pages.clear();
        // Split recipes into pages of 6 (2x3 grid)
        for (int i = 0; i < recipes.size(); i += 6) {
            int end = Math.min(i + 6, recipes.size());
            pages.add(recipes.subList(i, end));
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        ViewHolder(@NonNull RecyclerView recyclerView) {
            super(recyclerView);
            this.recyclerView = recyclerView;
        }
    }

    // Interface for recipe click events
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    // RecipeAdapter inner class
    private static class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
        private List<Recipe> recipes;
        private Context context;
        private OnRecipeClickListener clickListener;

        public RecipeAdapter(Context context) {
            this.context = context;
            this.recipes = new ArrayList<>();
        }

        public void setOnRecipeClickListener(OnRecipeClickListener listener) {
            this.clickListener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.recipe_card_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Recipe recipe = recipes.get(position);
            holder.recipeTitleView.setText(recipe.getTitle());

            Glide.with(context)
                    .load(recipe.getImage())
                    .centerCrop()
                    .into(holder.recipeImageView);

            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onRecipeClick(recipe);
                }
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

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView recipeImageView;
            TextView recipeTitleView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                recipeImageView = itemView.findViewById(R.id.recipeImageView);
                recipeTitleView = itemView.findViewById(R.id.recipeTitleView);
            }
        }
    }
}