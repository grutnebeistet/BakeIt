package com.roberts.adrian.bakeit.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.*;


/**
 * Created by Adrian on 26/07/2017.
 */

public class RecipeDbHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "recipes.db";
    private static final int DB_VERSION = 31;

    public RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("ONCREATE DB", "ONCREATEDB");

        final String SQL_CREATE_RECIPES_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPES + " (" +
                        COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                        COLUMN_RECIPE_SERVINGS + " INTEGER, " +
                        COLUMN_RECIPE_IMAGE + " TEXT, " +
                        COLUMN_RECIPE_ADDED_TODO + " INTEGER" +
                        ");";
        final String SQL_CREATE_INGREDIENTS_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_INGREDIENTS + " (" +
                        COLUMN_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, " +
                        COLUMN_INGREDIENT_MEASURE + " TEXT NOT NULL, " +
                        COLUMN_INGREDIENT_QUANTITY + " INTEGER NOT NULL, " +
                        COLUMN_RECIPE_ID + " INTEGER, " +
                        "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ")" +
                        ");";
        final String SQL_CREATE_STEPS_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_STEPS + " (" +
                        COLUMN_STEP_ID + " TEXT PRIMARY KEY, " +
                        COLUMN_STEP_DESCR + " TEXT, " +
                        COLUMN_STEP_SHORT_DESCR + " TEXT, " +
                        COLUMN_STEP_VIDEO_URL + " TEXT, " +
                        COLUMN_STEP_IMAGE_URL + " TEXT, " +
                        COLUMN_RECIPE_ID + " INTEGER, " +
                        "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ")" +
                        ");";

        db.execSQL(SQL_CREATE_RECIPES_TABLE);
        db.execSQL(SQL_CREATE_INGREDIENTS_TABLE);
        db.execSQL(SQL_CREATE_STEPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        Log.i("ONUPGRADE", "DROPPED TABLS");

        onCreate(db);
    }
}
