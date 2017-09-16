package com.roberts.adrian.bakeit.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;
import com.roberts.adrian.bakeit.data.RecipeContract;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ADDED_TODO;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_INGREDIENTS;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.CONTENT_URI_RECIPE;

/**
 * Created by Adrian on 06/09/2017.
 */

public class ListWidgetService extends RemoteViewsService {
    public static final String EXTRA_WIDGET_TYPE = "widget_type";
    public static final int EXTRA_FEATURED_RECIPE_WIDGET = 0;
    public static final int EXTRA_ALL_RECIPES_WIDGET = 1;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        final String TAG = ListWidgetService.class.getSimpleName();
        switch (intent.getExtras().getInt(EXTRA_WIDGET_TYPE)) {
            case EXTRA_ALL_RECIPES_WIDGET:
                //return new FullRecipeListRemoteViewsFactory(this.getApplicationContext());
            case EXTRA_FEATURED_RECIPE_WIDGET:
                return new FeaturedRecipeRemoteViewsFactory(this.getApplicationContext());
            default:
                return null;
        }
    }

    class FeaturedRecipeRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        final String LOG_TAG = FeaturedRecipeRemoteViewsFactory.class.getSimpleName();
        Context mContext;
        private String mRecipeName;
        private int mRecipeId;
        private Cursor mIngredientsCursor;
        Cursor mRecipesCursor;

        FeaturedRecipeRemoteViewsFactory(Context context) {
            mContext = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public int getCount() {
            return mIngredientsCursor == null ? 0 : mIngredientsCursor.getCount();
        }

        @Override
        public void onDataSetChanged() {

            final long identityToken = Binder.clearCallingIdentity();

            mRecipesCursor = mContext.getContentResolver().query(
                    CONTENT_URI_RECIPE,
                    new String[]{RecipeContract.RecipeEntry.COLUMN_RECIPE_ID, COLUMN_RECIPE_NAME},
                    COLUMN_RECIPE_ADDED_TODO + "=?",
                    new String[]{String.valueOf(RecipeContract.RECIPE_ON_TODO)}, null);

            if (mRecipesCursor == null || !mRecipesCursor.moveToFirst()) {
                return;
            }
            Log.i(LOG_TAG, "cursor not null");
            mRecipeId = mRecipesCursor.getInt(0);
            mRecipeName = mRecipesCursor.getString(1);
            mRecipesCursor.close();


            String selection = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + "=?";
            String[] selArgs = new String[]{String.valueOf(mRecipeId)};
            mIngredientsCursor = mContext.getContentResolver().query(
                    CONTENT_URI_INGREDIENTS,
                    null,
                    selection,
                    selArgs,
                    RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " ASC"
            );
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_item_ingredient);
            if (i == AdapterView.INVALID_POSITION ||
                    getCount() == 0 || !mRecipesCursor.moveToFirst()) {
                views.setTextViewText(R.id.ingredient_quantity, "");
                views.setTextViewText(R.id.ingredient_measure, "");
                views.setTextViewText(R.id.ingredient_name, "");
                views.setTextViewText(R.id.widget_single_recipe_name, "");

                return views;
            }

            mIngredientsCursor.moveToPosition(i);

            int quantity = mIngredientsCursor.getInt(mIngredientsCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_INGREDIENT_QUANTITY));
            String measure = mIngredientsCursor.getString(mIngredientsCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_INGREDIENT_MEASURE));
            String name = mIngredientsCursor.getString(mIngredientsCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_INGREDIENT_NAME));


            views.setTextViewText(R.id.ingredient_quantity, String.valueOf(quantity));
            views.setTextViewText(R.id.ingredient_measure, measure);
            views.setTextViewText(R.id.ingredient_name, name);
            views.setTextViewText(R.id.widget_single_recipe_name, mRecipeName);

            //views.setTextViewText(R.id.widget_single_recipe_name, mRecipeName);
            // views.setTextViewText(R.id.widget_recipe_servings, getString(R.string.recipe_servings, servings));

            Bundle extras = new Bundle();
            extras.putInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, mRecipeId);
            extras.putString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, mRecipeName);
            extras.putBoolean(RecipeDetailzActivity.EXTRA_FROM_WIDGET, true);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.widget_single_recipe_ll, fillInIntent);

            return views;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public void onDestroy() {
            Log.i(LOG_TAG, "onDestroy");
            if (mIngredientsCursor != null)
                mIngredientsCursor.close();
        }


        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }


}
