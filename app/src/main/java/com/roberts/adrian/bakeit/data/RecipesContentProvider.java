package com.roberts.adrian.bakeit.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_INGREDIENT_ID;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ID;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.TABLE_INGREDIENTS;

/**
 * Created by Adrian on 27/07/2017.
 */

public class RecipesContentProvider extends ContentProvider {
    private static final String LOG_TAG = RecipesContentProvider.class.getSimpleName();

    public static final int CODE_RECIPE = 100;
    public static final int CODE_RECIPE_ID = 101;
    public static final int CODE_INGREDIENTS = 200;
    public static final int CODE_INGREDIENTS_ID = 201;
    public static final int CODE_STEPS = 300;
    public static final int CODE_STEPS_ID = 301;
    public static final int CODE_STEPS_WITH_INGREDIENTS = 400;


    private RecipeDbHelper mDbHelper;
    private final static UriMatcher mUriMatcher = builUriMatcher();

    public static UriMatcher builUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecipeContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, RecipeContract.PATH_RECIPE, CODE_RECIPE);
        matcher.addURI(authority, RecipeContract.PATH_RECIPE + "/#", CODE_RECIPE_ID);

        matcher.addURI(authority, RecipeContract.PATH_INGREDIENTS, CODE_INGREDIENTS);
        matcher.addURI(authority, RecipeContract.PATH_INGREDIENTS + "/#", CODE_INGREDIENTS_ID);

        matcher.addURI(authority, RecipeContract.PATH_STEPS, CODE_STEPS);
        matcher.addURI(authority, RecipeContract.PATH_STEPS + "/#", CODE_STEPS_ID);

        return matcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (mUriMatcher.match(uri)) {
            case CODE_RECIPE:
                cursor = mDbHelper.getReadableDatabase().query(
                        RecipeContract.RecipeEntry.TABLE_RECIPES, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_INGREDIENTS:
                cursor = mDbHelper.getReadableDatabase().query(
                        TABLE_INGREDIENTS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_STEPS:
                cursor = mDbHelper.getReadableDatabase().query(
                        RecipeContract.RecipeEntry.TABLE_STEPS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_RECIPE_ID:
                selection = COLUMN_RECIPE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = mDbHelper.getReadableDatabase().query(
                        RecipeContract.RecipeEntry.TABLE_RECIPES, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_INGREDIENTS_ID:
                selection = COLUMN_INGREDIENT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = mDbHelper.getReadableDatabase().query(
                        TABLE_INGREDIENTS, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case CODE_STEPS_ID:
                selection = RecipeContract.RecipeEntry.COLUMN_STEP_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = mDbHelper.getReadableDatabase().query(
                        RecipeContract.RecipeEntry.TABLE_STEPS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                mDbHelper.close();
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new RecipeDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long newRowId;
        switch (mUriMatcher.match(uri)) {
            case CODE_RECIPE:
                newRowId = db.insert(RecipeContract.RecipeEntry.TABLE_RECIPES, null, values);
                Log.e(LOG_TAG, "inserted REC");
                break;
            case CODE_INGREDIENTS:
                newRowId = db.insert(TABLE_INGREDIENTS, null, values);
                Log.e(LOG_TAG, "inserted ING");
                break;
            case CODE_STEPS:
                newRowId = db.insert(RecipeContract.RecipeEntry.TABLE_STEPS, null, values);
                Log.i(LOG_TAG, "insertion Descr: " + values.getAsString(RecipeContract.RecipeEntry.COLUMN_STEP_DESCR));
                Log.e(LOG_TAG, "inserted STE");
                break;
            default:
                throw new IllegalArgumentException("Failed to insert: " + uri);
        }
        if (newRowId == -1) {
            Log.e(LOG_TAG, "insertion failed for " + uri);
            return null;
        }
        // Return Uri for newly added data
        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
