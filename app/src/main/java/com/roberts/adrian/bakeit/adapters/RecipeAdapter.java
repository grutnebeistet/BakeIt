package com.roberts.adrian.bakeit.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.MainActivity;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;
import com.roberts.adrian.bakeit.data.RecipeContract;
import com.roberts.adrian.bakeit.fragments.RecipesFragment;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.roberts.adrian.bakeit.R.drawable.default_recipe_image;

/**
 * Created by Adrian on 27/07/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private static final String LOG_TAG = RecipeAdapter.class.getSimpleName();

    private Context mContext;

    private Cursor mCursor;

    final private RecipeAdapterOnClickHandler mOnClickHandler;

    public interface RecipeAdapterOnClickHandler {
        void onClick(Bundle recipeGoodies);
    }

    public RecipeAdapter(Context context, RecipeAdapterOnClickHandler clickHandler) {
        mOnClickHandler = clickHandler;
        mContext = context;

    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String recipeName = mCursor.getString(RecipesFragment.INDEX_RECIPE_NAME);
        String recipeServings = mCursor.getString(RecipesFragment.INDEX_RECIPE_SERVINGS);
        String recipeImage = mCursor.getString(RecipesFragment.INDEX_RECIPE_IMAGE);

        int id = mCursor.getInt(RecipesFragment.INDEX_RECIPE_ID);

        holder.tvRecipeName.setText(recipeName);
        holder.tvServings.setText(mContext.getString(R.string.recipe_servings, recipeServings));

        Uri imageUri = recipeImage == null ? Uri.parse("") : Uri.parse(recipeImage);
        // Current JSON provides no images - setting a default placeholder for now
        Picasso.with(mContext)
                .load(imageUri).fit()
                .placeholder(default_recipe_image)
                .error(default_recipe_image)
                .centerCrop()
                .into(holder.ivRecipeImage);


    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_recipe_item, parent, false);
        return new RecipeViewHolder(view);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        // To let espresso know that the recyclerview is attached to the adapter
        MainActivity.mSyncFinished = true;

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

        @BindView(R.id.list_item_recipe_name)
        TextView tvRecipeName;
        @BindView(R.id.list_item_recipe_servings)
        TextView tvServings;
        @BindView(R.id.image_view_recipe_image)
        ImageView ivRecipeImage;

        RecipeViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mCursor.isClosed()) return;
            mCursor.moveToPosition(getAdapterPosition());
            Bundle args = new Bundle();
            args.putInt(RecipeDetailzActivity.EXTRA_RECIPE_ID,
                    mCursor.getInt(RecipesFragment.INDEX_RECIPE_ID));
            args.putString(RecipeDetailzActivity.EXTRA_RECIPE_NAME,
                    mCursor.getString(RecipesFragment.INDEX_RECIPE_NAME));
            args.putBoolean(RecipeDetailzActivity.EXTRA_DETAILS_ON_TODO,
                    (mCursor.getInt(RecipesFragment.INDEX_RECIPE_TODO) == RecipeContract.RECIPE_ON_TODO));
            mOnClickHandler.onClick(args);

        }
    }
}
