package com.roberts.adrian.bakeit.fragments;

import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;
import com.roberts.adrian.bakeit.adapters.RecipeAdapter;
import com.roberts.adrian.bakeit.data.RecipeContract;

import static com.roberts.adrian.bakeit.R.id.textview_recipe_name;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ID;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_RECIPE;

public class RecipeListFragment extends ListFragment
        implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    // OnImageClickListener mCallback;

    // @BindView(R.id.recyclerViewMain)
    //RecyclerView mRecipesRecyclerView;
    RecipeAdapter mRecipeAdapter;
    private SimpleCursorAdapter mAdapter;
    RecyclerView mRecipesRecyclerView;

    private static final String LOG_TAG = RecipeListFragment.class.getSimpleName();

    private static final String[] MAIN_RECIPE_PROJECTION = {
            COLUMN_RECIPE_ID,
            RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME
    };
    public static final int INDEX_RECIPE_ID = 0;
    public static final int INDEX_RECIPE_NAME = 1;

    private final int ID_LOADER = 1349;

    private Cursor mData;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        String[] columns = {RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME};

        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_recipe_item, null, columns, new int[]{textview_recipe_name}, 0);  // android.R.layout.simple_list_item_2, android.R.id.text1

        setListAdapter(mAdapter);

        getActivity().getLoaderManager().initLoader(ID_LOADER, null, this);
       /* setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1));

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);*/
        // getListView().setItemChecked(mCurCheckPosition, true);

    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // super.onListItemClick(l, v, position, id);
        Log.i(LOG_TAG, "position: " + position + "  id: " + id);
        showDetails(id);
    }

    private void showDetails(long id) {
        DetailsFragment details = new DetailsFragment();
        Bundle args = new Bundle();
        args.putLong("id", id);
        details.setArguments(args);

//        getActivity().setContentView(R.layout.activity_recipe_details);

        //getFragmentManager().beginTransaction().add(R.id.containter_steps, details).commit();

        // TODO : twopane etc

        // Create an intent for starting the DetailsActivity
        Intent intent = new Intent();

        // explicitly set the activity context and class
        // associated with the intent (context, class)
        intent.setClass(getActivity(), RecipeDetailzActivity.class);

        // pass the current position
        intent.putExtra("id", id);

        startActivity(intent);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, RecipeContract.RecipeEntry._ID}; // ID?

        return new android.content.CursorLoader(getActivity(), CONTENT_URI_RECIPE,
                projection, null, null, null);
        //COLUMN_RECIPE_ID + " ASC");

    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

 /*    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, RecipeContract.RecipeEntry._ID}; // ID?

        return new CursorLoader(getActivity(),CONTENT_URI_RECIPE,
                projection, null, null, null);
                //COLUMN_RECIPE_ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }*/
}
