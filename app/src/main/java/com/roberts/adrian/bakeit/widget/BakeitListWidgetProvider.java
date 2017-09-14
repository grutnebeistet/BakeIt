package com.roberts.adrian.bakeit.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;

/**
 * Implementation of App Widget functionality.
 */
public class BakeitListWidgetProvider extends AppWidgetProvider {
    private final static String LOG_TAG = BakeitListWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Log.i(BakeitListWidgetProvider.class.getSimpleName(), "onUpdate (empty now)");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);

            // Open mainactivity when title clicked TODO

            Intent detailsIntent = new Intent(context, RecipeDetailzActivity.class);

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(detailsIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntentTemplate);

            Intent intent = new Intent(context, ListWidgetService.class);
            intent.putExtra(ListWidgetService.EXTRA_WIDGET_TYPE, ListWidgetService.EXTRA_ALL_RECIPES_WIDGET);
            views.setRemoteAdapter(R.id.widget_list_view, intent);



            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
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

