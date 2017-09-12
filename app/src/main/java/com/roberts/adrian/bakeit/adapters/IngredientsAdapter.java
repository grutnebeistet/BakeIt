package com.roberts.adrian.bakeit.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;
import com.roberts.adrian.bakeit.fragments.DetailsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Adrian on 30/07/2017.
 */

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {
    private static final String LOG_TAG = IngredientsAdapter.class.getSimpleName();
    private Context mContext;
    private Cursor mCursor;

    public IngredientsAdapter(Context context) {
        mContext = context;

    }

    @Override
    public void onViewAttachedToWindow(IngredientsViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        // For testing - ingredients are attached to screen
        RecipeDetailzActivity.mIngredientsLoadingIdle = true;

    }

    @Override
    public void onBindViewHolder(IngredientsViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String ingredientQty = mCursor.getString(DetailsFragment.INDEX_INGREDIENT_QUANTITY);
        String ingredientMeasure = mCursor.getString(DetailsFragment.INDEX_INGREDIENT_MEASURE);
        String ingredientName = mCursor.getString(DetailsFragment.INDEX_INGREDIENT_NAME);
        holder.ingredientQty.setText(ingredientQty);
        holder.ingredientMeasure.setText(ingredientMeasure);
        holder.ingredientName.setText(ingredientName);


    }

    @Override
    public IngredientsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_ingredient, parent, false);
        return new IngredientsViewHolder(view);

    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;

        return mCursor.getCount();
    }

    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    public class IngredientsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredient_quantity) TextView ingredientQty;
        @BindView(R.id.ingredient_measure) TextView ingredientMeasure;
        @BindView(R.id.ingredient_name) TextView ingredientName;


        IngredientsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}