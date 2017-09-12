package com.roberts.adrian.bakeit.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;
import com.roberts.adrian.bakeit.activities.StepActivity;
import com.roberts.adrian.bakeit.adapters.IngredientsAdapter;
import com.roberts.adrian.bakeit.adapters.StepsAdapter;
import com.roberts.adrian.bakeit.data.RecipeContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_INGREDIENTS;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_STEPS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends android.app.Fragment
        implements android.app.LoaderManager.LoaderCallbacks<Cursor>,
        StepsAdapter.StepsAdapterOnclickHandler {


    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();


    final String[] PROJECTION_INGREDIENTS = {
            RecipeContract.RecipeEntry.COLUMN_INGREDIENT_MEASURE,
            RecipeContract.RecipeEntry.COLUMN_INGREDIENT_NAME,
            RecipeContract.RecipeEntry.COLUMN_INGREDIENT_QUANTITY,
            RecipeContract.RecipeEntry.COLUMN_RECIPE_ID
    };
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

    public final static int INDEX_INGREDIENT_MEASURE = 0;
    public final static int INDEX_INGREDIENT_NAME = 1;
    public final static int INDEX_INGREDIENT_QUANTITY = 2;
    public final static int INDEX_INGREDIENT_FK = 3;

    private static final int LOADER_ID_INGREDIENTS = 1348;
    private static final int LOADER_ID_STEPS = 1349;

    private IngredientsAdapter mIngredientsAdapter;
    //private RecyclerView mRecyclerViewIngredients;

    //  TextView recipeNameTextView;
    private Activity mActivity;
    private static int mRecipeId;
    private static String mRecipeName;
    private StepsAdapter mStepsAdapter;
    @BindView(R.id.recyclerViewSteps)
    RecyclerView mRecyclerViewSteps;
    @BindView(R.id.recyclerViewIngredients)
    RecyclerView mRecyclerViewIngredients;
    @BindView(R.id.text_view_recipe_name)
    TextView recipeNameTextView;
    private ScrollView mScrollView;
    @BindView(R.id.cooking_steps_label)
    TextView mStepsLabel;

    private int mScrollPos = 0;
    private Unbinder unbinder;

    public static DetailsFragment newInstance(int id, String recipe) {
        mRecipeId = id;
        mRecipeName = recipe;
        DetailsFragment ingredientsFragment = new DetailsFragment();

        Bundle args = new Bundle();
        args.putInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, id);
        args.putString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, recipe);
        ingredientsFragment.setArguments(args);

        return ingredientsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setRetainInstance(true);

    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (getArguments() == null && savedInstanceState != null) {
            mRecipeId = savedInstanceState.getInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, 1);
            mRecipeName = savedInstanceState.getString(RecipeDetailzActivity.EXTRA_RECIPE_NAME);
            mScrollPos = savedInstanceState.getInt(RecipeDetailzActivity.EXTRA_DETAILS_SCROLL_POS, 0);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        Log.i(LOG_TAG, "onCreateView");

        if (getArguments() != null) {
            mRecipeId = getArguments().getInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, 1);
            mRecipeName = getArguments().getString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, null);
            mScrollPos = getArguments().getInt(RecipeDetailzActivity.EXTRA_DETAILS_SCROLL_POS,0);
        }

        View view = inflater.inflate(R.layout.fragment_details, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mIngredientsAdapter = new IngredientsAdapter(mActivity);
        mRecyclerViewIngredients.setLayoutManager(layoutManager);
        mRecyclerViewIngredients.setAdapter(mIngredientsAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 3);
        mRecyclerViewSteps.setLayoutManager(gridLayoutManager);
        mStepsAdapter = new StepsAdapter(mActivity, this);
        mRecyclerViewSteps.setAdapter(mStepsAdapter);

        mScrollView = view.findViewById(R.id.details_scroll_view);

        mActivity.getLoaderManager().initLoader(LOADER_ID_INGREDIENTS, null, this);
        mActivity.getLoaderManager().initLoader(LOADER_ID_STEPS, null, this);


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mRecipeName != null) recipeNameTextView.setText(mRecipeName);
        else recipeNameTextView.setVisibility(View.GONE);

        mScrollView.setScrollY(mScrollPos);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, mRecipeName);
        outState.putInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, mRecipeId);
        outState.putInt(RecipeDetailzActivity.EXTRA_DETAILS_SCROLL_POS, mScrollPos);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + "=?";
        String[] selArgs = new String[]{String.valueOf(mRecipeId)};

        if (id == LOADER_ID_INGREDIENTS) {
            return new android.content.CursorLoader(getActivity(), CONTENT_URI_INGREDIENTS,
                    PROJECTION_INGREDIENTS, selection, selArgs, null);
        }
        if (id == LOADER_ID_STEPS) {


            Log.i(LOG_TAG, "Loader: mRecipeId: " + mRecipeId);
            return new android.content.CursorLoader(getActivity(), CONTENT_URI_STEPS,
                    PROJECTION_STEPS, selection, selArgs, null);

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ID_INGREDIENTS) {
            mIngredientsAdapter.swapCursor(data);
            Log.i(LOG_TAG, "onLoadFinished");
        }
        if (loader.getId() == LOADER_ID_STEPS) {
            mStepsAdapter.swapCursor(data);
            Log.i(LOG_TAG, "data.size " + data.getCount());
            if (data.getCount() > 0)
                if (mStepsLabel != null) mStepsLabel.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mIngredientsAdapter.swapCursor(null);
        mStepsAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onresume");
        mActivity.getLoaderManager().restartLoader(LOADER_ID_INGREDIENTS, null, this);
        mActivity.getLoaderManager().restartLoader(LOADER_ID_STEPS, null, this);

    }

    @Override
    public void onClick(Bundle stepDescriptions) {
        Intent stepDetails = new Intent(getActivity(), StepActivity.class);
        stepDetails.putExtras(stepDescriptions);
        startActivity(stepDetails);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}