package com.roberts.adrian.bakeit.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.adapters.IngredientsAdapter;
import com.roberts.adrian.bakeit.adapters.StepsAdapter;
import com.roberts.adrian.bakeit.data.RecipeContract;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_INGREDIENTS;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_STEPS;

public class RecipeDetailsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        StepsAdapter.StepsAdapterOnclickHandler{
    static final String LOG_TAG = RecipeDetailsActivity.class.getSimpleName();

    final String[] PROJECTION_STEPS = {
            RecipeContract.RecipeEntry.COLUMN_STEP_ID,
            RecipeContract.RecipeEntry.COLUMN_STEP_SHORT_DESCR,
            RecipeContract.RecipeEntry.COLUMN_STEP_DESCR,
            RecipeContract.RecipeEntry.COLUMN_STEP_IMAGE_URL,
            RecipeContract.RecipeEntry.COLUMN_STEP_VIDEO_URL,
            RecipeContract.RecipeEntry.COLUMN_RECIPE_ID
    };
    public final static int PROJECTION_INDEX_STEP_ID = 0;
    public final static int PROJECTION_INDEX_STEP_SHORT_DESC = 1;
    public final static int PROJECTION_INDEX_STEP_DESC = 2;
    public final static int PROJECTION_INDEX_STEP_IMG = 3;
    public final static int PROJECTION_INDEX_STEP_VIDEO = 4;
    public final static int PROJECTION_INDEX_STEP_FK = 5;

    final String[] PROJECTION_INGREDIENTS = {
            RecipeContract.RecipeEntry.COLUMN_INGREDIENT_MEASURE,
            RecipeContract.RecipeEntry.COLUMN_INGREDIENT_NAME,
            RecipeContract.RecipeEntry.COLUMN_INGREDIENT_QUANTITY,
            RecipeContract.RecipeEntry.COLUMN_RECIPE_ID
    };
    public final static int INDEX_INGREDIENT_MEASURE = 0;
    public final static int INDEX_INGREDIENT_NAME = 1;
    public final static int INDEX_INGREDIENT_QUANTITY = 2;
    public final static int INDEX_INGREDIENT_FK = 3;

    private static final int LOADER_ID_STEPS = 1349;
    private static final int LOADER_ID_INGREDIENTS = 1348;
    private StepsAdapter mStepsAdapter;
    private IngredientsAdapter mIngredientsAdapter;
    @BindView(R.id.ingredients_tv)
    TextView mIngredients;

    @BindView(R.id.recycler_view_steps_descriptions)
    RecyclerView mRecyclerViewSteps;

    @BindView(R.id.recycler_view_ingredients)
    RecyclerView mRecyclerViewIngredients;

    private int mRecipe_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);

        mRecipe_id = getIntent().getExtras().getInt("recipeID");
        setTitle(getIntent().getExtras().getString("recipeName"));

        mStepsAdapter = new StepsAdapter(this, this);
        mIngredientsAdapter = new IngredientsAdapter(this);

        mRecyclerViewSteps.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerViewSteps.setAdapter(mStepsAdapter);
        mRecyclerViewIngredients.setAdapter(mIngredientsAdapter);

        getSupportLoaderManager().initLoader(LOADER_ID_STEPS, null, this);
        getSupportLoaderManager().initLoader(LOADER_ID_INGREDIENTS, null, this);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ID_STEPS) {
            mStepsAdapter.swapCursor(data);
        }
        if (loader.getId() == LOADER_ID_INGREDIENTS) {
            mIngredientsAdapter.swapCursor(data);
        }

        Log.i(LOG_TAG, "data.getlengt " + data.getCount());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = CONTENT_URI_STEPS;
        String[] projection = PROJECTION_STEPS;

        //if (id == LOADER_ID_STEPS) uri = CONTENT_URI_STEPS;
        if (id == LOADER_ID_INGREDIENTS) {
            uri = CONTENT_URI_INGREDIENTS;
            projection = PROJECTION_INGREDIENTS;
        }
        //  Uri stepsWithIngredientsUri = ContentUris.withAppendedId(CONTENT_URI_STEPS_WITH_INGREDIENTS, mRecipe_id);
        Log.i(LOG_TAG, "onCreateLoader");
        String selection = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + "=?";
        // where recipe_id FK == mRecipe_id
        String[] selArgs = new String[]{String.valueOf(mRecipe_id)};

        return new CursorLoader(this, uri, projection, selection, selArgs, null);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStepsAdapter.swapCursor(null);
        mIngredientsAdapter.swapCursor(null);
    }


    // onClick a recipe step list item
    @Override
    public void onClick(Bundle stepDescriptions) {
        // activating the frame in which the details appear

        Intent stepIntent = new Intent(RecipeDetailsActivity.this, StepActivity.class);

        stepIntent.putExtras(stepDescriptions);


        startActivity(stepIntent);

//        findViewById(R.id.step_details_container).setVisibility(View.VISIBLE);

//        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
//                .beginTransaction();
//        Fragment stepDetailsFragment = new StepDetailsFragment();//the fragment you want to show
//        stepDetailsFragment.setArguments(stepDescriptions);
//
//        getLayoutInflater().from(this).inflate(R.layout.fragment_step_details,null);
//
//        fragmentTransaction
//                .add(R.id.step_details_container, stepDetailsFragment);//R.id.content_frame is the layout you want to replace
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();

    }

}
