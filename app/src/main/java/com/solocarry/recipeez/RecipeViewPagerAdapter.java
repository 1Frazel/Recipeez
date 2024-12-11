package com.solocarry.recipeez;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.solocarry.recipeez.RecipeDetail;

public class RecipeViewPagerAdapter extends FragmentStateAdapter {
    private final RecipeDetail recipeDetail;

    public RecipeViewPagerAdapter(FragmentActivity activity, RecipeDetail recipeDetail) {
        super(activity);
        this.recipeDetail = recipeDetail;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return RecipeOverviewFragment.newInstance(recipeDetail);
            case 1:
                return RecipeIngredientsFragment.newInstance(recipeDetail);
            case 2:
                return RecipeDirectionsFragment.newInstance(recipeDetail);
            default:
                throw new IllegalStateException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}