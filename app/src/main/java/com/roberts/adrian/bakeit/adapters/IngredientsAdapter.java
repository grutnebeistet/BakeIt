package com.roberts.adrian.bakeit.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.fragments.DetailsFragment;

/**
 * Created by Adrian on 30/07/2017.
 */

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    //  final private StepsAdapterOnclickHandler mOnclickHandler;


    public IngredientsAdapter(Context context) {
        mContext = context;
        //  mOnclickHandler = clickHandler;

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

    class IngredientsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ingredientQty;
        TextView ingredientMeasure;
        TextView ingredientName;


        IngredientsViewHolder(View view) {
            super(view);
            ingredientQty = (TextView) view.findViewById(R.id.ingredient_quantity);
            ingredientMeasure = (TextView) view.findViewById(R.id.ingredient_measure);
            ingredientName = (TextView) view.findViewById(R.id.ingredient_name);
            // view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mCursor.isClosed()) return;
            mCursor.moveToPosition(getAdapterPosition());


            //mOnclickHandler.onClick(stepDetailsBundle);
        }
    }
}