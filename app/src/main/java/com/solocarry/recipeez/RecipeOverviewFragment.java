package com.solocarry.recipeez;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.solocarry.recipeez.RecipeDetail;

public class RecipeOverviewFragment extends Fragment {
    private RecipeDetail recipeDetail;

    public static RecipeOverviewFragment newInstance(RecipeDetail recipeDetail) {
        RecipeOverviewFragment fragment = new RecipeOverviewFragment();
        Bundle args = new Bundle();
        args.putString("recipe_detail", new Gson().toJson(recipeDetail));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_overview, container, false);

        if (getArguments() != null) {
            recipeDetail = new Gson().fromJson(
                    getArguments().getString("recipe_detail"),
                    RecipeDetail.class
            );

            TextView servingsTV = view.findViewById(R.id.servingsTV);
            TextView readyTimeTV = view.findViewById(R.id.readyTimeTV);
            TextView summaryTV = view.findViewById(R.id.summaryTV);

            // Set servings
            servingsTV.setText(String.valueOf(recipeDetail.getServings()));

            // Set ready time
            String readyTime = recipeDetail.getReadyInMinutes() + " mins";
            readyTimeTV.setText(readyTime);

            // Set summary (remove HTML tags)
            String summary = recipeDetail.getSummary();
            if (summary != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    summary = Html.fromHtml(summary, Html.FROM_HTML_MODE_COMPACT).toString();
                } else {
                    summary = Html.fromHtml(summary).toString();
                }
                summaryTV.setText(summary);
            }
        }

        return view;
    }
}