package com.roberts.adrian.bakeit.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.fragments.RecipeListFragment;

/**
 * Created by Adrian on 27/07/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context mContext;

    private Cursor mCursor;

    final private RecipeAdapterOnClickHandler mOnClickHandler;

    public interface RecipeAdapterOnClickHandler {
        void onClick(int recipe_id, String recipe_name);
    }

    public RecipeAdapter(Context context, RecipeAdapterOnClickHandler clickHandler) {
        mOnClickHandler = clickHandler;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String recipeName = mCursor.getString(RecipeListFragment.INDEX_RECIPE_NAME);
        Log.i("onBindViewHolder", "recipNm " + recipeName);
        holder.tvRecipeName.setText(recipeName);

    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;

        return mCursor.getCount();
    }

    public void swapCursor(Cursor newData) {
        mCursor = newData;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /* @BindView(R.id.textview_recipe_name)
         TextView tvRecipeName;*/
        final TextView tvRecipeName;

        RecipeViewHolder(View v) {
            super(v);

            tvRecipeName = (TextView) v.findViewById(R.id.textview_recipe_name);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mCursor.isClosed()) return;
            mCursor.moveToPosition(getAdapterPosition());
            mOnClickHandler.onClick(
                    mCursor.getInt(RecipeListFragment.INDEX_RECIPE_ID),
                    mCursor.getString(RecipeListFragment.INDEX_RECIPE_NAME));

        }
    }
}
