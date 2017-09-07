package com.roberts.adrian.bakeit.fragments;


import android.app.Activity;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;
import com.roberts.adrian.bakeit.adapters.IngredientsAdapter;
import com.roberts.adrian.bakeit.data.RecipeContract;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_INGREDIENTS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsIngredientsFragment extends android.app.Fragment
        implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailsIngredientsFragment.class.getSimpleName();
    private IngredientsAdapter mIngredientsAdapter;
    private RecyclerView mRecyclerViewIngredients;
    TextView recipeNameTextView;
    private Activity mActivity;
    private static int mRecipeId;
    private static String mRecipeName;

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

    private static final int LOADER_ID_INGREDIENTS = 1348;


    public static DetailsIngredientsFragment newInstance(int id, String recipe) {
        mRecipeId = id;
        mRecipeName = recipe;
        DetailsIngredientsFragment ingredientsFragment = new DetailsIngredientsFragment();
        Log.i(LOG_TAG, "new instance: id, name : " + id + ", " + recipe);
        Bundle args = new Bundle();
        args.putInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, id);
        args.putString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, recipe);
        ingredientsFragment.setArguments(args);

        return ingredientsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        Log.i(LOG_TAG, "onCreateView");


        if (getArguments() != null) {
            mRecipeId = getArguments().getInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, 1);

        }
        View view = inflater.inflate(R.layout.fragment_details_ingredients, container, false);

        recipeNameTextView = (TextView) view.findViewById(R.id.text_view_recipe_name);
        mRecyclerViewIngredients = (RecyclerView) view.findViewById(R.id.recyclerViewIngredients);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mIngredientsAdapter = new IngredientsAdapter(mActivity);
        mRecyclerViewIngredients.setLayoutManager(layoutManager);
        mRecyclerViewIngredients.setAdapter(mIngredientsAdapter);

        mActivity.getLoaderManager().initLoader(LOADER_ID_INGREDIENTS, null, this);

        return view;
    }

    public int getRecipeId() {
        return mRecipeId;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(LOG_TAG, "onActCreated: " + mRecipeName);
        if (savedInstanceState != null) {
            mRecipeName = savedInstanceState.getString(RecipeDetailzActivity.EXTRA_RECIPE_NAME);
            // recipeNameTextView.setText(savedInstanceState.getString("recipe_name"));
            // return;
        } else mRecipeName = getArguments().getString(RecipeDetailzActivity.EXTRA_RECIPE_NAME);
        //recipeNameTextView.setText(getArguments().getString("recipe_name"));
        if (mRecipeName != null) recipeNameTextView.setText(mRecipeName);
        else recipeNameTextView.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, mRecipeName);
        outState.putInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, mRecipeId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + "=?";
        String[] selArgs = new String[]{String.valueOf(mRecipeId)};

        if (id == LOADER_ID_INGREDIENTS) {
            return new android.content.CursorLoader(getActivity(), CONTENT_URI_INGREDIENTS,
                    PROJECTION_INGREDIENTS, selection, selArgs, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ID_INGREDIENTS) {
            mIngredientsAdapter.swapCursor(data);
            Log.i(LOG_TAG, "swpappedCursor");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mIngredientsAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.getLoaderManager().restartLoader(LOADER_ID_INGREDIENTS, null, this);
    }
}
