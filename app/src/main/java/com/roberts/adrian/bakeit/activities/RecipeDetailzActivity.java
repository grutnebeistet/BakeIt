package com.roberts.adrian.bakeit.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.fragments.DetailsIngredientsFragment;
import com.roberts.adrian.bakeit.fragments.DetailsStepsFragment;

public class RecipeDetailzActivity extends AppCompatActivity {
    private static final String LOG_TAG = RecipeDetailzActivity.class.getSimpleName();
    public static final String EXTRA_RECIPE_ID = "com.roberts.adrian.bakeit.extra.RECIPE_ID";
    public static final String EXTRA_RECIPE_NAME = "com.roberts.adrian.bakeit.extra.RECIPE_NAME";
    public static final String EXTRA_FROM_WIDGET = "com.roberts.adrian.bakeit.extra.FROM_WIDGET";

    // for testing with idlingResource
    public static boolean mSyncFinished;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        boolean fromWidget = getIntent().getExtras().getBoolean(EXTRA_FROM_WIDGET,false);


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && !fromWidget) {
            // If the screen is now in landscape mode and the app not opened from the widget, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }


        if (savedInstanceState == null) {
            setContentView(R.layout.activity_recipe_details);
            Log.i(LOG_TAG, "setting up frags..");

            DetailsIngredientsFragment ingredientsDetails = new DetailsIngredientsFragment();
            ingredientsDetails.setArguments(getIntent().getExtras());


            getFragmentManager().beginTransaction().
                    add(R.id.details_ingredients_container, ingredientsDetails).
                    commit();

            DetailsStepsFragment stepsDetails = new DetailsStepsFragment();
            //if (stepsDetails )
            stepsDetails.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().
                    add(R.id.details_steps_container, stepsDetails).
                    commit();


        }

    }
    @VisibleForTesting
    public boolean isSyncFinished(){
        return mSyncFinished;
    }
}
