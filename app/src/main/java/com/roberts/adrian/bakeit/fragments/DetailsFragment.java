package com.roberts.adrian.bakeit.fragments;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.StepActivity;
import com.roberts.adrian.bakeit.adapters.IngredientsAdapter;
import com.roberts.adrian.bakeit.adapters.StepsAdapter;
import com.roberts.adrian.bakeit.data.RecipeContract;

import butterknife.ButterKnife;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_INGREDIENTS;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_STEPS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends android.app.Fragment
        implements android.app.LoaderManager.LoaderCallbacks<Cursor>,
        StepsAdapter.StepsAdapterOnclickHandler {

    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();
    //private SimpleCursorAdapter mStepsAdapter;
    private SimpleCursorAdapter mSimpleIngrAdapt;
    private IngredientsAdapter mIngredientsAdapter;
    private StepsAdapter mStepsAdapter;
    private RecyclerView mRecyclerViewSteps;
    private RecyclerView mRecyclerViewIngredients;
/*
    @BindView(R.id.recyclerViewSteps)
    RecyclerView mRecyclerViewSteps;
*/

    private Activity mActivity;
    private static long mRecipeId;
    private Cursor mCursorStepDetails;

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

    public static DetailsFragment newInstance(long id) {
        mRecipeId = id;
        DetailsFragment detailsFragment = new DetailsFragment(); // TODO newINstance for hva? twopane?
        Log.i(LOG_TAG, "new instance: id: " + id);
        Bundle args = new Bundle();
        args.putLong("id", id);
        detailsFragment.setArguments(args);
        return detailsFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        ButterKnife.bind(mActivity);
        //   getActivity().setTitle(getIntent().getExtras().getString("recipeName"));
        // mRecipeId = getArguments().getLong("id", 1);
        Log.i(LOG_TAG, "getarguments null: " + (getArguments() == null));

        if (getArguments() != null) {
            mRecipeId = getArguments().getLong("id", 1);
        }
        String[] columns = {RecipeContract.RecipeEntry.COLUMN_STEP_SHORT_DESCR};

      /*  mStepsAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.grid_item_recipe_step, null, columns, new int[]{R.id.tv_step_descr}, 0);*/
// android.R.layout.simple_list_item_2, null, columns, new int[]{android.R.id.text1}, 0);

        mRecyclerViewSteps = (RecyclerView) mActivity.findViewById(R.id.recyclerViewSteps);
        //setListAdapter(mStepsAdapter);
        mStepsAdapter = new StepsAdapter(mActivity, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);
        mRecyclerViewSteps.setLayoutManager(gridLayoutManager);
        mRecyclerViewSteps.setAdapter(mStepsAdapter);

        mRecyclerViewIngredients = (RecyclerView) mActivity.findViewById(R.id.recyclerViewIngredients);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mIngredientsAdapter = new IngredientsAdapter(mActivity);
        mRecyclerViewIngredients.setLayoutManager(layoutManager);
        mRecyclerViewIngredients.setAdapter(mIngredientsAdapter);


        mActivity.getLoaderManager().initLoader(LOADER_ID_STEPS, null, this);
        mActivity.getLoaderManager().initLoader(LOADER_ID_INGREDIENTS, null, this);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + "=?";
        String[] selArgs = new String[]{String.valueOf(mRecipeId)};

        if (id == LOADER_ID_STEPS) {
            Log.i(LOG_TAG, "mRecipeId: " + mRecipeId);
            return new android.content.CursorLoader(getActivity(), CONTENT_URI_STEPS,
                    PROJECTION_STEPS, selection, selArgs, null);
        }
        if (id == LOADER_ID_INGREDIENTS) {
            return new android.content.CursorLoader(getActivity(), CONTENT_URI_INGREDIENTS,
                    PROJECTION_INGREDIENTS, selection, selArgs, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ID_STEPS) {
            mStepsAdapter.swapCursor(data);
        }
        if (loader.getId() == LOADER_ID_INGREDIENTS) {
            mIngredientsAdapter.swapCursor(data);
        }

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mStepsAdapter.swapCursor(null);
        mIngredientsAdapter.swapCursor(null);
    }

/*    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mCursorStepDetails.isClosed()) return;


        // TODO if klikket på et step:
        Log.i(LOG_TAG, "listView ID: " + l.getId());
        Log.i(LOG_TAG, "view ID: " + v.getId());
        mCursorStepDetails.moveToPosition(position);

        Bundle stepDetailsBundle = new Bundle();
        String stepShortDescription = mCursorStepDetails.getString(PROJECTION_INDEX_STEP_SHORT_DESC);
        String stepDescription = mCursorStepDetails.getString(PROJECTION_INDEX_STEP_DESC);
        String stepVideoUrl = mCursorStepDetails.getString(PROJECTION_INDEX_STEP_VIDEO);
        String stepImageUrl = mCursorStepDetails.getString(PROJECTION_INDEX_STEP_IMG);

        stepDetailsBundle.putString(getString(R.string.steps_bundle_title), stepShortDescription);
        stepDetailsBundle.putString(getString(R.string.steps_bundle_description), stepDescription);
        stepDetailsBundle.putString(getString(R.string.steps_bundle_video_url), stepVideoUrl);
        stepDetailsBundle.putString(getString(R.string.steps_bundle_image_url), stepImageUrl);

        Intent stepDetails = new Intent(getActivity(), StepActivity.class);
        stepDetails.putExtras(stepDetailsBundle);
        startActivity(stepDetails);
    }*/

    @Override
    public void onClick(Bundle stepDescriptions) {
        Intent stepDetails = new Intent(getActivity(), StepActivity.class);
        stepDetails.putExtras(stepDescriptions);
        startActivity(stepDetails);
    }
}
