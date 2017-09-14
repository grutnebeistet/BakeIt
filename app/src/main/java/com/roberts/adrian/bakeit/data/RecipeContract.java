package com.roberts.adrian.bakeit.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Adrian on 26/07/2017.
 */

public class RecipeContract {
    public static final String CONTENT_AUTHORITY = "com.roberts.adrian.bakeit";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RECIPE = "recipe";
    public static final String PATH_INGREDIENTS = "ingredients";
    public static final String PATH_STEPS = "steps";
    public static final String PATH_STEPS_WITH_INGREDIENTS = "stepsWithIngredients";

    public static final int RECIPE_OFF_TODO = 0;
    public static final int RECIPE_ON_TODO = 1;
    public static final class RecipeEntry implements BaseColumns {

        public static final Uri CONTENT_URI_RECIPE = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE).build();
        public static final Uri CONTENT_URI_INGREDIENTS = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();
        public static final Uri CONTENT_URI_STEPS = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STEPS).build();
        public static final Uri CONTENT_URI_STEPS_WITH_INGREDIENTS = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STEPS_WITH_INGREDIENTS).build();

        // DB Tables
        public static final String TABLE_RECIPES = "recipes";
        public static final String TABLE_INGREDIENTS = "ingredients";
        public static final String TABLE_STEPS = "steps";

        // Recipe columns
        public static final String COLUMN_RECIPE_ID = _ID;

        public static final String COLUMN_RECIPE_NAME = "recipe_name";

        public static final String COLUMN_RECIPE_SERVINGS = "recipe_servings";
        public static final String COLUMN_RECIPE_IMAGE = "recipe_image";
        public static final String COLUMN_RECIPE_ADDED_TODO = "recipe_added_todo";

        // Ingredients columns
        public static final String COLUMN_INGREDIENT_ID = "ingredient_id";
        public static final String COLUMN_INGREDIENT_QUANTITY = "ingredient_qty";
        public static final String COLUMN_INGREDIENT_MEASURE = "ingredient_measure";
        public static final String COLUMN_INGREDIENT_NAME = "ingredient_name";

        // Steps columns
        public static final String COLUMN_STEP_ID = "step_id";
        public static final String COLUMN_STEP_SHORT_DESCR = "step_short_descr";
        public static final String COLUMN_STEP_DESCR = "step_descr";
        public static final String COLUMN_STEP_VIDEO_URL = "step_video_url";
        public static final String COLUMN_STEP_IMAGE_URL = "step_image_url";


    }

}

