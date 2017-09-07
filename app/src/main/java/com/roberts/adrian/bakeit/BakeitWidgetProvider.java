package com.roberts.adrian.bakeit;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;

/**
 * Implementation of App Widget functionality.
 */
public class BakeitWidgetProvider extends AppWidgetProvider {
    private final static String LOG_TAG = BakeitWidgetProvider.class.getSimpleName();
    private int mRecipeId;
    private String mRecipeName;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras() != null) {
            mRecipeId = intent.getExtras().getInt(RecipeDetailzActivity.EXTRA_RECIPE_ID);
            mRecipeName = intent.getExtras().getString(RecipeDetailzActivity.EXTRA_RECIPE_NAME);
            //TODO funke?
            Log.i(LOG_TAG, "onReceive recipe: " + mRecipeName + "\n intent.getclass: " + intent.getClass().toString());
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Log.i(BakeitWidgetProvider.class.getSimpleName(), "onUpdate (empty now)");
        // There may be multiple widgets active, so update all of them

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);

            // Open mainactivity when title clicked TODO

            Intent detailsIntent = new Intent(context, RecipeDetailzActivity.class);

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(detailsIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_grid_view, clickPendingIntentTemplate);

            Intent intent = new Intent(context, GridWidgetService.class);
            views.setRemoteAdapter(R.id.widget_grid_view, intent);
            appWidgetManager.updateAppWidget(appWidgetId, views);


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private static RemoteViews getGridRecipeRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.id.widget_grid_view);
        Intent intent = new Intent(context, GridWidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);
        // Set the detailactivity to launch upon click
        Intent appIntent = new Intent(context, RecipeDetailzActivity.class);
        appIntent.putExtra(RecipeDetailzActivity.EXTRA_FROM_WIDGET, true);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);
        // TODO handle empty
        return views;
    }

    private static RemoteViews getSingleRecipeRemoteView(Context context, int recipeId, String recipeName) {
        Intent intent = new Intent(context, RecipeDetailzActivity.class);
        intent.putExtra(RecipeDetailzActivity.EXTRA_FROM_WIDGET, true);
        intent.putExtra(RecipeDetailzActivity.EXTRA_RECIPE_ID, recipeId);
        intent.putExtra(RecipeDetailzActivity.EXTRA_RECIPE_NAME, recipeName);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
        //TODO set text and image?
        return views;

    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int recipeId, String recipeName) {
        // get current width - single recipe or grid
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        RemoteViews views;
        if (width < 300) {
            views = getSingleRecipeRemoteView(context, recipeId, recipeName);
        } else {
            views = getGridRecipeRemoteView(context);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

