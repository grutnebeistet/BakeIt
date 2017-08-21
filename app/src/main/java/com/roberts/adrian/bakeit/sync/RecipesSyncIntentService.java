package com.roberts.adrian.bakeit.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Adrian on 27/07/2017.
 */

public class RecipesSyncIntentService extends IntentService {
    public RecipesSyncIntentService() {
        super("RecipesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        RecipesSyncTask.syncRecipeData(this);
    }
}
