package com.roberts.adrian.bakeit.IdlingResource;

import android.support.test.espresso.IdlingResource;

import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;

/**
 * Created by Adrian on 08/09/2017.
 */

public class RecipeDetailsIdlingResource implements IdlingResource {

    private RecipeDetailzActivity activity;
    private ResourceCallback callback;

    public RecipeDetailsIdlingResource(RecipeDetailzActivity activity) {
        this.activity = activity;
    }

    @Override
    public String getName() {
        return "DetailsActivityIdleName";
    }

    @Override
    public boolean isIdleNow() {
        Boolean idle = isIdle();
        if (idle) callback.onTransitionToIdle();
        return idle;
    }

    public boolean isIdle() {
        return activity != null && callback != null && activity.isSyncFinished();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.callback = resourceCallback;
    }
}