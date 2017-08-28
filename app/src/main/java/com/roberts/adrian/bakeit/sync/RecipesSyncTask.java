package com.roberts.adrian.bakeit.sync;

import android.content.Context;
import android.util.Log;

import com.roberts.adrian.bakeit.utils.NetworkUtils;

/**
 * Created by Adrian on 27/07/2017.
 */

public class RecipesSyncTask {
    private static final String LOG_TAG = RecipesSyncTask.class.getSimpleName();

    /**
     * Performs a network request to retrieve or update recipe JSON data, parses the data,
     * and performs insertion into the contentprovider
     */
    synchronized public static void syncRecipeData(Context context) {
        try {
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.QUERY_URL);
       //     Log.i(LOG_TAG, jsonResponse);
            NetworkUtils.getRecipeContentValuesFromJson(context, jsonResponse);
            Log.i(LOG_TAG, "syncing data..");

            //context.getContentResolver().bulkInsert(CONTENT_URI_RECIPE, contentValues);

        } catch (Exception e) {
           e.printStackTrace();
        }
    }
}
