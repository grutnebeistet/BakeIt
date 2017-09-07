package com.roberts.adrian.bakeit;

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

import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;
import com.roberts.adrian.bakeit.data.RecipeContract;

/**
 * Created by Adrian on 06/09/2017.
 */

public class GridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mContext;
        private Cursor mCursor;

        public GridRemoteViewsFactory(Context appContext, Intent intent) {
            mContext = appContext;
        }

        @Override
        public void onDataSetChanged() {
            Uri RECIPES_URI = RecipeContract.BASE_CONTENT_URI.buildUpon().appendPath(RecipeContract.PATH_RECIPE).build();

            if (mCursor != null) mCursor.close();

            final long identityToken = Binder.clearCallingIdentity();

            mCursor = mContext.getContentResolver().query(
                    RECIPES_URI,
                    null,
                    null,
                    null,
                    null
            );
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if (mCursor != null)
                mCursor.close();
        }

        @Override
        public int getCount() {
            return mCursor == null ? 0 : mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position == AdapterView.INVALID_POSITION ||
                    mCursor == null || mCursor.getCount() == 0) return null;


            mCursor.moveToPosition(position);

            int recipeId = mCursor.getInt(mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID));
            String recipeName = mCursor.getString(mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME));
            String servings = mCursor.getString(mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS));
            String imageUrl = mCursor.getString(mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE));

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.bakeit_widget_grid_item);

            views.setTextViewText(R.id.list_item_recipe_name, recipeName);
            views.setTextViewText(R.id.widget_recipe_servings, getString(R.string.recipe_servings, servings));
            if (!(imageUrl == null || imageUrl.isEmpty())) {
                Log.i(GridWidgetService.class.getSimpleName(), "imageurl OK");
                views.setImageViewUri(R.id.widget_image, Uri.parse(imageUrl));
            }

    /*        Picasso.with(mContext)
                    .load(Uri.parse(imageUrl))
                    .placeholder(default_recipe_image)
                    .error(default_recipe_image)
                    .centerCrop()
                    .into();*/
            Bundle extras = new Bundle();
            extras.putInt(RecipeDetailzActivity.EXTRA_RECIPE_ID, recipeId);
            extras.putString(RecipeDetailzActivity.EXTRA_RECIPE_NAME, recipeName);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.widget_image, fillInIntent);

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
