package com.roberts.adrian.bakeit.fragments;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;
import com.roberts.adrian.bakeit.activities.StepActivity;
import com.roberts.adrian.bakeit.adapters.StepsAdapter;
import com.roberts.adrian.bakeit.data.RecipeContract;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_STEPS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsStepsFragment extends android.app.Fragment
        implements android.app.LoaderManager.LoaderCallbacks<Cursor>,
        StepsAdapter.StepsAdapterOnclickHandler {

    private static final String LOG_TAG = DetailsStepsFragment.class.getSimpleName();
    //private SimpleCursorAdapter mStepsAdapter;
    private SimpleCursorAdapter mSimpleIngrAdapt;

    private StepsAdapter mStepsAdapter;
    @BindView(R.id.recyclerViewSteps)
    RecyclerView mRecyclerViewSteps;

    @BindView(R.id.cooking_steps_label)
    TextView mStepsLabel;
/*
    @BindView(R.id.recyclerViewSteps)
    RecyclerView mRecyclerViewSteps;
*/

    private Activity mActivity;
    private static int mRecipeId;
    private static String mRecipeName;
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


    private static final int LOADER_ID_STEPS = 1349;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            Log.i(LOG_TAG, "onCreate");
        mActivity = getActivity();
        ButterKnife.bind(mActivity);


        mStepsAdapter = new StepsAdapter(mActivity, this);


        mActivity.getLoaderManager().initLoader(LOADER_ID_STEPS, null, this);
    }

    public static DetailsStepsFragment newInstance(int id, String recipe) {
        mRecipeId = id;
        mRecipeName = recipe;
        DetailsStepsFragment detailsStepsFragment = new DetailsStepsFragment(); // TODO newINstance for hva? twopane?
        // Log.i(LOG_TAG, "new instance: id: " + id + "\nNew instance name: " + recipe);
        Bundle args = new Bundle();
        args.putInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, id);
        args.putString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, recipe);
        detailsStepsFragment.setArguments(args);

        return detailsStepsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onCreateView");

        if (container != null) {
            container.removeAllViews();
        }
        if (getArguments() != null) {
            mRecipeId = getArguments().getInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, 1);

        }
        View view = inflater.inflate(R.layout.fragment_details_steps, container, false);

        mStepsLabel = (TextView)view.findViewById(R.id.cooking_steps_label);
        mRecyclerViewSteps = (RecyclerView) view.findViewById(R.id.recyclerViewSteps);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);
        mRecyclerViewSteps.setLayoutManager(gridLayoutManager);
        mRecyclerViewSteps.setAdapter(mStepsAdapter);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(LOG_TAG, "onActCr, savedInst = null: " + (savedInstanceState == null));

        //   getActivity().setTitle(getIntent().getExtras().getString("recipeName"));
        // mRecipeId = getArguments().getLong("id", 1);


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onresume");
        mActivity.getLoaderManager().restartLoader(LOADER_ID_STEPS, null, this);

    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + "=?";
        String[] selArgs = new String[]{String.valueOf(mRecipeId)};
        Log.i(LOG_TAG, "oncreateLoader");

        if (id == LOADER_ID_STEPS) {
            Log.i(LOG_TAG, "Loader: mRecipeId: " + mRecipeId);
            return new android.content.CursorLoader(getActivity(), CONTENT_URI_STEPS,
                    PROJECTION_STEPS, selection, selArgs, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ID_STEPS) {
            mStepsAdapter.swapCursor(data);
            Log.i(LOG_TAG, "data.size " + data.getCount());
            if (data.getCount() > 0)
                if(mStepsLabel != null)mStepsLabel.setVisibility(View.VISIBLE);
        }


    }

    public int getRecipeId() {
        return mRecipeId;
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mStepsAdapter.swapCursor(null);

    }


    @Override
    public void onClick(Bundle stepDescriptions) {
        Intent stepDetails = new Intent(getActivity(), StepActivity.class);
        stepDetails.putExtras(stepDescriptions);
        startActivity(stepDetails);
    }
}
