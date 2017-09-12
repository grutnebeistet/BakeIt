package com.roberts.adrian.bakeit.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;
import com.roberts.adrian.bakeit.data.RecipeContract;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_INGREDIENTS;

/**
 * Created by Adrian on 06/09/2017.
 */

public class GridWidgetService extends RemoteViewsService {
    final String[] PROJECTION_INGREDIENTS = {
            RecipeContract.RecipeEntry.COLUMN_INGREDIENT_MEASURE,
            RecipeContract.RecipeEntry.COLUMN_INGREDIENT_NAME,
            RecipeContract.RecipeEntry.COLUMN_INGREDIENT_QUANTITY,
            RecipeContract.RecipeEntry.COLUMN_RECIPE_ID
    };

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }

    class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        final String LOG_TAG = GridWidgetService.class.getSimpleName();
        private Context mContext;
        private Cursor mRecipesCursor;
        private Cursor mIngredientsCursor;

        public GridRemoteViewsFactory(Context appContext) {
            mContext = appContext;
        }

        @Override
        public void onDataSetChanged() {
            Uri RECIPES_URI = RecipeContract.BASE_CONTENT_URI.buildUpon().appendPath(RecipeContract.PATH_RECIPE).build();

            if (mRecipesCursor != null) mRecipesCursor.close();

            final long identityToken = Binder.clearCallingIdentity();

            mRecipesCursor = mContext.getContentResolver().query(
                    RECIPES_URI,
                    null,
                    null,
                    null,
                    null
            );

            String selection = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + "=?";
            // String[] selArgs = new String[]{String.valueOf(recipeId)};
            mIngredientsCursor = mContext.getContentResolver().query(
                    CONTENT_URI_INGREDIENTS,
                    null,
                    null,
                    null,
                    RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " ASC"
            );
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if (mRecipesCursor != null)
                mRecipesCursor.close();
            if (mIngredientsCursor != null)
                mIngredientsCursor.close();
        }

        @Override
        public int getCount() {
            return mRecipesCursor == null ? 0 : mRecipesCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position == AdapterView.INVALID_POSITION ||
                    getCount() == 0) return null;


            mRecipesCursor.moveToPosition(position);

            int recipeId = mRecipesCursor.getInt(mRecipesCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID));
            String recipeName = mRecipesCursor.getString(mRecipesCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME));
            String servings = mRecipesCursor.getString(mRecipesCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS));
            String imageUrl = mRecipesCursor.getString(mRecipesCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE));

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);


            views.setTextViewText(R.id.widget_recipe_name, recipeName);
            views.setTextViewText(R.id.widget_recipe_servings, getString(R.string.recipe_servings, servings));
            if (!(imageUrl == null || imageUrl.isEmpty())) {
                Log.i(GridWidgetService.class.getSimpleName(), "imageurl OK");
                views.setImageViewUri(R.id.widget_image, Uri.parse(imageUrl));
            }

            if (mIngredientsCursor != null && mIngredientsCursor.getCount() > 0) {
                // A problem here is that getViewAt get's called twice for the first view/recipe
                // which seem skew/misplace ingredients TODO / Help
                while (mIngredientsCursor.moveToNext()) {
                    String ingredient = mIngredientsCursor.getString(mIngredientsCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_INGREDIENT_NAME));
                    int ingredientFk = mIngredientsCursor.getInt(mIngredientsCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID));

                    if (ingredientFk == recipeId && (recipeId == position + 1)) {
                        // Log.i(LOG_TAG, "ingred: " + ingredient + " FK: " + ingredientFk  + " PK: " + recipeId + " recnam: " + recipeName + " pos: " + position);
                        RemoteViews ingredientsView = new RemoteViews(getPackageName(), R.layout.widget_ingredients);
                        ingredientsView.setTextViewText(R.id.widget_ingredient_text_view, ingredient);
                        views.addView(R.id.widget_ingredient_items, ingredientsView);
                        Log.i(LOG_TAG, "added: " + ingredient + " for recipe: " + recipeName);
                    }
                }
                mIngredientsCursor.moveToFirst();
            }

            Bundle extras = new Bundle();
            extras.putInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, recipeId);
            extras.putString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, recipeName);
            extras.putBoolean(RecipeDetailzActivity.EXTRA_FROM_WIDGET, true);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

            return views;

        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onCreate() {

        }

    }

}
