package com.roberts.adrian.bakeit.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailsActivity;
import com.roberts.adrian.bakeit.adapters.RecipeAdapter;
import com.roberts.adrian.bakeit.data.RecipeContract;
import com.roberts.adrian.bakeit.utils.NetworkUtils;

import butterknife.ButterKnife;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_RECIPE;

public class RecipeListFragment extends Fragment
        implements RecipeAdapter.RecipeAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {
    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    // OnImageClickListener mCallback;

    // @BindView(R.id.recyclerViewMain)
    //RecyclerView mRecipesRecyclerView;
    RecipeAdapter mRecipeAdapter;

    RecyclerView mRecipesRecyclerView;

    private static final String LOG_TAG = RecipeListFragment.class.getSimpleName();

    private static final String[] MAIN_RECIPE_PROJECTION = {
            RecipeContract.RecipeEntry.COLUMN_RECIPE_ID,
            RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME
    };
    public static final int INDEX_RECIPE_ID = 0;
    public static final int INDEX_RECIPE_NAME = 1;

    private final int ID_LOADER = 1349;

    // OnImageClickListener interface, calls a method in the host activity named onImageSelected
//    public interface OnImageClickListener {
//        void onImageSelected(int position);
//    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ButterKnife.bind(getActivity());

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
    /*    try {
            mCallback = (OnImageClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }*/
    }


    // Mandatory empty constructor
    public RecipeListFragment() {
    }

    // Inflates the GridView of all AndroidMe images
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ButterKnife.bind(getActivity());

        final View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        mRecipesRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewMain);
        // Create the adapter
        // This adapter takes in the context and an ArrayList of ALL the image resources to display
        mRecipeAdapter = new RecipeAdapter(getActivity(), this);

        mRecipesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Set the adapter on the GridView
        mRecipesRecyclerView.setAdapter(mRecipeAdapter);
        Boolean connection = NetworkUtils.workingConnection(getActivity());

        getActivity().getSupportLoaderManager().initLoader(ID_LOADER, null, this);
     /*   if (connection) {
            RecipesSyncUtils.initialize(getActivity());
        }*/

        // Return the root view
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "onCrateLoader");
        return new CursorLoader(getActivity(),
                CONTENT_URI_RECIPE,
                MAIN_RECIPE_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecipeAdapter.swapCursor(data);
        Log.i(LOG_TAG, "Cursor.getC " + data.getCount());

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecipeAdapter.swapCursor(null);
    }

    @Override
    public void onClick(int recipe_id, String recipe_name) {
        Intent intent = new Intent(getActivity(), RecipeDetailsActivity.class);
        intent.putExtra("recipeID", recipe_id);
        intent.putExtra("recipeName", recipe_name);

        startActivity(intent);
    }

}
