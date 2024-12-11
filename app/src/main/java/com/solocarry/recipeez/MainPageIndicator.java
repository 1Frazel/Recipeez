package com.solocarry.recipeez;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;

public class MainPageIndicator extends LinearLayout {
    private int dotCount;
    private int currentPosition;
    private static final int DOT_SIZE = 8; // dp
    private static final int DOT_MARGIN = 4; // dp

    public MainPageIndicator(Context context) {
        super(context);
        init();
    }

    public MainPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
    }

    public void setDotCount(int count) {
        removeAllViews();
        this.dotCount = count;
        for (int i = 0; i < count; i++) {
            addDot();
        }
        setCurrentPosition(0);
    }

    public void setCurrentPosition(int position) {
        this.currentPosition = position;
        for (int i = 0; i < getChildCount(); i++) {
            View dot = getChildAt(i);
            dot.setBackgroundResource(i == position ?
                    R.drawable.tab_indicator_selected : R.drawable.tab_indicator_default);
        }
    }

    private void addDot() {
        View dot = new View(getContext());
        int size = dpToPx(DOT_SIZE);
        int margin = dpToPx(DOT_MARGIN);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(margin, 0, margin, 0);
        dot.setLayoutParams(params);
        dot.setBackgroundResource(R.drawable.tab_indicator_default);
        addView(dot);
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}