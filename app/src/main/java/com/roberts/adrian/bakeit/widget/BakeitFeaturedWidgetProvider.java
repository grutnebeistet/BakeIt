package com.roberts.adrian.bakeit.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;

/**
 * Created by Adrian on 13/09/2017.
 */

public class BakeitFeaturedWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_single_recipe);

            Intent detailsIntent = new Intent(context, RecipeDetailzActivity.class);

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(detailsIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.widget_featured_list_view, clickPendingIntentTemplate);

            Intent intent = new Intent(context, ListWidgetService.class);

            intent.putExtra(ListWidgetService.EXTRA_WIDGET_TYPE,ListWidgetService.EXTRA_FEATURED_RECIPE_WIDGET);

            views.setRemoteAdapter(R.id.widget_featured_list_view, intent);

            ComponentName component=new ComponentName(context,ListWidgetService.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_featured_list_view);
            appWidgetManager.updateAppWidget(component, views);

            appWidgetManager.updateAppWidget(appWidgetId, views);



        }
    }
}
