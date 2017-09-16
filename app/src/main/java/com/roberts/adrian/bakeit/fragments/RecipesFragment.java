package com.roberts.adrian.bakeit.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;
import com.roberts.adrian.bakeit.adapters.RecipeAdapter;
import com.roberts.adrian.bakeit.data.RecipeContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ADDED_TODO;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ID;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_RECIPE;

public class RecipesFragment extends Fragment
        implements android.app.LoaderManager.LoaderCallbacks<Cursor>, RecipeAdapter.RecipeAdapterOnClickHandler {


    private Activity mActivity;

    private static final String LOG_TAG = RecipesFragment.class.getSimpleName();

    private static final String[] MAIN_RECIPE_PROJECTION = {
            COLUMN_RECIPE_ID,
            COLUMN_RECIPE_NAME,
            COLUMN_RECIPE_IMAGE,
            COLUMN_RECIPE_SERVINGS,
            COLUMN_RECIPE_ADDED_TODO
    };
    public static final int INDEX_RECIPE_ID = 0;
    public static final int INDEX_RECIPE_NAME = 1;
    public static final int INDEX_RECIPE_IMAGE = 2;
    public static final int INDEX_RECIPE_SERVINGS = 3;
    public static final int INDEX_RECIPE_TODO = 4;

    private final int LOADER_ID = 1347;

    RecipeAdapter mRecipeAdapter;
    private boolean mDualPane;
    private int mCurrentRecipeId;
    private String mCurrentRecipeName;
    private int mCurrentScrollPos = 0;

    @Nullable
    @BindView(R.id.details_scroll_view)
    ScrollView mScrollView;
    @BindView(R.id.list_view_main)
    RecyclerView mRecipesRecyclerView;

    private Unbinder unbinder;
    boolean mRecipeTodo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        Log.i(LOG_TAG, "oncreateView");
/*        if (savedInstanceState != null && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                && mCurrentRecipeId >= 0 && savedInstanceState.getBoolean("wasLand")) {
            // inflate fragment
            mDualPane = false;
            showDetails(savedInstanceState);
            //  return null;
        }*/

        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        mRecipeAdapter = new RecipeAdapter(mActivity, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecipesRecyclerView.setLayoutManager(layoutManager);
        mRecipesRecyclerView.setAdapter(mRecipeAdapter);

        mActivity.getLoaderManager().initLoader(LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(LOG_TAG, "onActCerated");
        View landFrame = mActivity.findViewById(R.id.main_landscape);
        mDualPane = (landFrame != null &&
                landFrame.getVisibility() == View.VISIBLE);
        if (savedInstanceState != null) {

            mCurrentRecipeId = savedInstanceState.getInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, 0);
            mCurrentRecipeName = savedInstanceState.getString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, null);
            mCurrentScrollPos = savedInstanceState.getInt(RecipeDetailzActivity.EXTRA_DETAILS_SCROLL_POS, 0);
            mRecipeTodo = savedInstanceState.getBoolean(RecipeDetailzActivity.EXTRA_DETAILS_ON_TODO, false);
            mRecipesRecyclerView.smoothScrollToPosition(0);

            if (mCurrentRecipeName != null)
                showDetails(savedInstanceState);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "onSave");
        outState.putInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, mCurrentRecipeId);
        outState.putString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, mCurrentRecipeName);
        outState.putInt(RecipeDetailzActivity.EXTRA_DETAILS_SCROLL_POS, mCurrentScrollPos);
        outState.putBoolean(RecipeDetailzActivity.EXTRA_DETAILS_ON_TODO, mRecipeTodo);

        boolean land = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        outState.putBoolean("wasLand", land);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        mActivity.getLoaderManager().initLoader(LOADER_ID, null, this);


    }

    @Override
    public void onClick(Bundle args) {
        showDetails(args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(LOG_TAG, "onDestroyView");
        unbinder.unbind();
    }


    private void showDetails(Bundle recipeArgs) {
        Log.i(LOG_TAG, "showDetails for: " + recipeArgs.getString(RecipeDetailzActivity.EXTRA_RECIPE_NAME) +
                "\nid: " + recipeArgs.getInt(RecipeDetailzActivity.EXTRA_RECIPE_ID));
        if (mDualPane) {
            // Check what fragments are currently shown and replace it accordingly
            DetailsFragment ingredients = DetailsFragment.newInstance(recipeArgs);

            FragmentTransaction ft = getFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.details_fragment_container, ingredients);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

            ft.commit();
            mRecipesRecyclerView.smoothScrollToPosition(mCurrentRecipeId);

        } else {
            // Create an intent for starting the DetailsActivity
            Intent intent = new Intent();
            intent.setClass(mActivity, RecipeDetailzActivity.class);
            intent.putExtras(recipeArgs);
            startActivity(intent);

        }
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new android.content.CursorLoader(getActivity(), CONTENT_URI_RECIPE,
                MAIN_RECIPE_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        if (data == null || !data.moveToFirst()) return;

        if (mRecipeAdapter != null) mRecipeAdapter.swapCursor(data);

        mRecipeTodo = data.getInt(INDEX_RECIPE_TODO) == RecipeContract.RECIPE_ON_TODO;
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        //mAdapter.swapCursor(null);
        if (mRecipeAdapter != null) mRecipeAdapter.swapCursor(null);
    }

}