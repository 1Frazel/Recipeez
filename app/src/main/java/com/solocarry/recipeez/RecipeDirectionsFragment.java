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

import java.util.List;

public class RecipeDirectionsFragment extends Fragment {
    private RecipeDetail recipeDetail;

    public static RecipeDirectionsFragment newInstance(RecipeDetail recipeDetail) {
        RecipeDirectionsFragment fragment = new RecipeDirectionsFragment();
        Bundle args = new Bundle();
        args.putString("recipe_detail", new Gson().toJson(recipeDetail));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_directions, container, false);

        if (getArguments() != null) {
            recipeDetail = new Gson().fromJson(
                    getArguments().getString("recipe_detail"),
                    RecipeDetail.class
            );

            LinearLayout directionsContainer = view.findViewById(R.id.directionsContainer);

            List<RecipeDetail.AnalyzedInstruction> instructions = recipeDetail.getAnalyzedInstructions();
            if (instructions != null && !instructions.isEmpty()) {
                List<RecipeDetail.AnalyzedInstruction.Step> steps = instructions.get(0).getSteps();

                for (RecipeDetail.AnalyzedInstruction.Step step : steps) {
                    TextView stepTV = new TextView(requireContext());
                    stepTV.setText(String.format("%d. %s", step.getNumber(), step.getStep()));
                    stepTV.setTextSize(16);
                    int padding = (int) (8 * requireContext().getResources().getDisplayMetrics().density);
                    stepTV.setPadding(0, padding, 0, padding);
                    directionsContainer.addView(stepTV);
                }
            } else {
                TextView noInstructionsTV = new TextView(requireContext());
                noInstructionsTV.setText("No instructions available for this recipe.");
                noInstructionsTV.setTextSize(16);
                directionsContainer.addView(noInstructionsTV);
            }
        }

        return view;
    }
}