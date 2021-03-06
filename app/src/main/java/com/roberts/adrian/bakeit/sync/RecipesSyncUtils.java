package com.roberts.adrian.bakeit.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.roberts.adrian.bakeit.data.RecipeContract;
import com.roberts.adrian.bakeit.data.RecipeDbHelper;

/**
 * Created by Adrian on 27/07/2017.
 */

public class RecipesSyncUtils {

    private static boolean mInitialized;

    /**
     * Creates periodic sync tasks and checks whether an immediate sync is required.
     */

    synchronized public static void initialize(@NonNull final Context context) {
        // Only perform initialization once per app lifetime
        if (mInitialized) {
            return;
        }

        mInitialized = true;

        // Create a thread to check if there's any data in the contentProvider
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                RecipeDbHelper dbHelper = new RecipeDbHelper(context);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                Cursor cursor = db.query(RecipeContract.RecipeEntry.TABLE_RECIPES,null,null,null,null,null,null);
                // TODO query only id_

                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context);
                    cursor.close();
                    db.close();
                }


            }
        });
        checkForEmpty.start();
    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {

        Intent intentToSyncImmediately = new Intent(context, RecipesSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
