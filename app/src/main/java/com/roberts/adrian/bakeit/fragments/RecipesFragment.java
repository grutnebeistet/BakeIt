package com.roberts.adrian.bakeit.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;
import com.roberts.adrian.bakeit.adapters.RecipeAdapter;
import com.roberts.adrian.bakeit.data.RecipeContract;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ID;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_RECIPE;

public class RecipesFragment extends Fragment
        implements android.app.LoaderManager.LoaderCallbacks<Cursor>, RecipeAdapter.RecipeAdapterOnClickHandler {
    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    // OnImageClickListener mCallback;

    // @BindView(R.id.recyclerViewMain)
    //RecyclerView mRecipesRecyclerView;
    RecipeAdapter mRecipeAdapter;
    private SimpleCursorAdapter mAdapter;
    private RecyclerView mRecipesRecyclerView;

    private Activity mActivity;

    private static final String LOG_TAG = RecipesFragment.class.getSimpleName();

    private static final String[] MAIN_RECIPE_PROJECTION = {
            COLUMN_RECIPE_ID,
            RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME,
            RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE,
            RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS
    };
    public static final int INDEX_RECIPE_ID = 0;
    public static final int INDEX_RECIPE_NAME = 1;
    public static final int INDEX_RECIPE_IMAGE = 2;
    public static final int INDEX_RECIPE_SERVINGS = 3;

    private final int LOADER_ID = 1347;

    private boolean mDualPane;
    private int mCurrentRecipeId;
    private String mCurrentRecipeName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = getActivity();
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        mRecipesRecyclerView = view.findViewById(R.id.list_view_main);
        mRecipeAdapter = new RecipeAdapter(mActivity, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecipesRecyclerView.setLayoutManager(layoutManager);
        mRecipesRecyclerView.setAdapter(mRecipeAdapter);
        // mRecipesRecyclerView.smoothScrollToPosition(mCurrentRecipeId);//TODO int scrollPos
        mActivity.getLoaderManager().initLoader(LOADER_ID, null, this);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        View landFrame = mActivity.findViewById(R.id.main_landscape);
        mDualPane = (landFrame != null &&
                landFrame.getVisibility() == View.VISIBLE);
        if (savedInstanceState != null) {
            mCurrentRecipeId = savedInstanceState.getInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, 0);
            mCurrentRecipeName = savedInstanceState.getString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, null);

            Log.i(LOG_TAG, "Recycler scrolled to " + mCurrentRecipeId);
            showDetails(mCurrentRecipeId, mCurrentRecipeName);

        }




     /*   if (mDualPane) {  // Trenger vel egentlig ikke å vise noen details før klikk
            showDetails(mCurrentRecipeId, mCurrentRecipeName); // TODO
            Log.i(LOG_TAG, "showdetails for " + mCurrentRecipeName);
        }*/

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "onSaved");
        outState.putInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, mCurrentRecipeId);
        outState.putString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, mCurrentRecipeName);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        mActivity.getLoaderManager().initLoader(LOADER_ID, null, this);


    }

    @Override
    public void onClick(int recipe_id, String recipe_name) {
        showDetails(recipe_id, recipe_name);
        Log.i(LOG_TAG, "dualPane.: " + mDualPane);


    }


    private void showDetails(int recipe_id, String recipe_name) {
        mCurrentRecipeId = recipe_id;
        mCurrentRecipeName = recipe_name;

        if (mDualPane) {
            Log.i(LOG_TAG, "showdetails (dualpane): " + recipe_name);
            // Check what fragments are currently shown and replace it accordingly


            DetailsFragment ingredients = DetailsFragment.newInstance(recipe_id, recipe_name);

            FragmentTransaction ft = getFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.details_ingredients_container, ingredients);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

            //ft.commit();


        /*    DetailsStepsFragment steps = DetailsStepsFragment.newInstance(recipe_id, recipe_name);

            // Execute a transaction, replacing any existing fragment
            // with this one inside the frame.

            ft.replace(R.id.details_steps_container, steps);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);*/
            ft.commit();
            mActivity.findViewById(R.id.scrollview_details).setScrollY(0);

        } else {
            // Create an intent for starting the DetailsActivity
            Intent intent = new Intent();

            // explicitly set the activity context and class
            // associated with the intent (context, class)
            intent.setClass(mActivity, RecipeDetailzActivity.class);
            // pass the current position
            intent.putExtra(RecipeDetailzActivity.EXTRA_RECIPE_ID, recipe_id);
            intent.putExtra(RecipeDetailzActivity.EXTRA_RECIPE_NAME, recipe_name); // TODO heller sende inn  bundle slik som step?

            startActivity(intent);

        }
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, RecipeContract.RecipeEntry._ID}; // ID?

        return new android.content.CursorLoader(getActivity(), CONTENT_URI_RECIPE,
                MAIN_RECIPE_PROJECTION, null, null, null);
        //COLUMN_RECIPE_ID + " ASC");

    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        Log.i(LOG_TAG, "data size " + data.getCount());
        if (mRecipeAdapter != null) mRecipeAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        //mAdapter.swapCursor(null);
        if (mRecipeAdapter != null) mRecipeAdapter.swapCursor(null);
    }

}