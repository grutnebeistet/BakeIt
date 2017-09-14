package com.roberts.adrian.bakeit.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.fragments.DetailsFragment;

public class RecipeDetailzActivity extends AppCompatActivity {
    private static final String LOG_TAG = RecipeDetailzActivity.class.getSimpleName();
    public static final String EXTRA_RECIPE_ID = "com.roberts.adrian.bakeit.extra.RECIPE_ID";
    public static final String EXTRA_RECIPE_NAME = "com.roberts.adrian.bakeit.extra.RECIPE_NAME";
    public static final String EXTRA_FROM_WIDGET = "com.roberts.adrian.bakeit.extra.FROM_WIDGET";
    public static final String EXTRA_DETAILS_SCROLL_POS = "com.roberts.adrian.bakeit.extra.SCROLL_POSITION";
    public static final String EXTRA_DETAILS_ON_TODO = "com.roberts.adrian.bakeit.extra.RECIPE_ON_TODO";

    // for testing with idlingResource
    public static boolean mStepsLoadingIdle;
    public static boolean mIngredientsLoadingIdle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean fromWidget = getIntent().getExtras().getBoolean(EXTRA_FROM_WIDGET, false);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && !fromWidget) {

            // If the screen is now in landscape mode and the app not opened from the widget, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;

        }

        if (savedInstanceState == null) {
            setContentView(R.layout.activity_recipe_details);
            DetailsFragment details = new DetailsFragment();
            details.setArguments(getIntent().getExtras());


            getFragmentManager().beginTransaction().
                    add(R.id.details_ingredients_container, details).
                    commit();

        }
    }


    @VisibleForTesting
    public boolean isSyncFinished() {
        return mIngredientsLoadingIdle && mStepsLoadingIdle;
    }
}
