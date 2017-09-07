package com.roberts.adrian.bakeit.activities;

import android.content.res.Configuration;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        boolean fromWidget = getIntent().getExtras().getBoolean(EXTRA_FROM_WIDGET,false);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && !fromWidget) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }


        if (savedInstanceState == null) {
            setContentView(R.layout.activity_recipe_details);
            // details.setArguments(getIntent().getExtras());
       /*     int recId = getIntent().getExtras().getInt(EXTRA_RECIPE_ID);
            String recName = getIntent().getExtras().getString(EXTRA_RECIPE_NAME);
            Log.i(LOG_TAG, "details ID: " + recId + " name: " + recName);
            Bundle args = new Bundle();
            args.putString(EXTRA_RECIPE_NAME, recName);
            args.putLong(EXTRA_RECIPE_ID, recId);*/

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
}
