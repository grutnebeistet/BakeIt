package com.roberts.adrian.bakeit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.fragments.DetailsFragment;

public class RecipeDetailzActivity extends AppCompatActivity {
    private static final String LOG_TAG = RecipeDetailzActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        // details.setArguments(getIntent().getExtras());
        long recId = getIntent().getExtras().getLong("id");
        DetailsFragment stepsDetails = new DetailsFragment().newInstance(recId);
        getFragmentManager().beginTransaction().add(R.id.steps_container, stepsDetails).commit();

        DetailsFragment ingredientsDetails = new DetailsFragment().newInstance(recId);

        getFragmentManager().beginTransaction().add(R.id.ingredients_container, ingredientsDetails).commit();


    }
}
