package com.roberts.adrian.bakeit.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.roberts.adrian.bakeit.data.RecipeContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_INGREDIENT_MEASURE;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_INGREDIENT_NAME;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_INGREDIENT_QUANTITY;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ID;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_STEP_DESCR;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_STEP_ID;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_STEP_IMAGE_URL;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_STEP_SHORT_DESCR;
import static com.roberts.adrian.bakeit.data.RecipeContract.RecipeEntry.COLUMN_STEP_VIDEO_URL;

/**
 * Created by Adrian on 26/07/2017.
 */

public class NetworkUtils {
    private static final int DELAY_MILLIS = 3000;

    private final static String LOG_TAG = NetworkUtils.class.getSimpleName();
    public static final String QUERY_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";


    public static void getRecipeContentValuesFromJson(Context context, String jsonResponse)
            throws JSONException {
        JSONArray baseResponse = new JSONArray(jsonResponse);

        for (int i = 0; i < baseResponse.length(); i++) {
            ContentValues recipeValues = new ContentValues();
            ContentValues ingredientsValues = new ContentValues();
            ContentValues stepsValues = new ContentValues();

            JSONObject recipeJsonObject = baseResponse.getJSONObject(i);

            // Recipe main info
            int recipe_id = recipeJsonObject.getInt("id");
            String recipe_name = recipeJsonObject.getString("name");
            int recipe_servings = recipeJsonObject.getInt("servings");
            String recipe_image = recipeJsonObject.getString("image");
            if(recipe_image.isEmpty()) recipe_image = null;
            Log.i(LOG_TAG, "rec name " + recipe_name);
            recipeValues.put(COLUMN_RECIPE_ID, recipe_id);
            recipeValues.put(COLUMN_RECIPE_NAME, recipe_name);
            recipeValues.put(COLUMN_RECIPE_IMAGE,recipe_image);
            recipeValues.put(COLUMN_RECIPE_SERVINGS, recipe_servings);

            // TODO spør om effektiviteten av insert i hver tab vs evt Bulkinsert
            // if(recipeValues != null && recipeValues.)
            context.getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI_RECIPE, recipeValues);
            // Fetch ingredient w/ measure and quantity
            JSONArray ingredientsArray = recipeJsonObject.getJSONArray("ingredients");
            for (int j = 0; j < ingredientsArray.length(); j++) {

                JSONObject ingredientObject = ingredientsArray.getJSONObject(j);

                int ingredient_quantity = ingredientObject.getInt("quantity");
                // Log.i(LOG_TAG, "QTY = " + ingredient_quantity);

                String ingredient_measure = ingredientObject.getString("measure");
                String ingredient_name = ingredientObject.getString("ingredient");

                ingredientsValues.put(COLUMN_INGREDIENT_QUANTITY, ingredient_quantity);
                ingredientsValues.put(COLUMN_INGREDIENT_MEASURE, ingredient_measure);
                ingredientsValues.put(COLUMN_INGREDIENT_NAME, ingredient_name);
                ingredientsValues.put(COLUMN_RECIPE_ID, recipe_id);

                context.getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI_INGREDIENTS, ingredientsValues);
            }

            // Fetch baking steps
            JSONArray stepsArray = recipeJsonObject.getJSONArray("steps");
            for (int k = 0; k < stepsArray.length(); k++) {
                JSONObject stepsObject = stepsArray.getJSONObject(k);

                // Combining recipe ID with stepID to give each step a unique ID
                String step_id = String.valueOf(recipe_id) + String.valueOf(stepsObject.getInt("id"));
                //Log.i(LOG_TAG, "STEPS ID: " + step_id);

                String shortDescription = stepsObject.isNull("shortDescription") ? null : stepsObject.getString("shortDescription");
                String description = stepsObject.isNull("description") ? null : stepsObject.getString("description");
                //Log.i(LOG_TAG, "DESC parsing " + description);
                String videoURL = stepsObject.isNull("videoURL") ? null : stepsObject.getString("videoURL");
                String imageURL = stepsObject.isNull("thumbnailURL") ? null : stepsObject.getString("thumbnailURL");

                if(imageURL.isEmpty()) imageURL = null;
                if(videoURL.isEmpty()) videoURL = null;
                stepsValues.put(COLUMN_STEP_ID, step_id);
                stepsValues.put(COLUMN_STEP_SHORT_DESCR, shortDescription);
                stepsValues.put(COLUMN_STEP_DESCR, description);
                stepsValues.put(COLUMN_STEP_VIDEO_URL, videoURL);
                stepsValues.put(COLUMN_STEP_IMAGE_URL, imageURL);
                stepsValues.put(COLUMN_RECIPE_ID, recipe_id); // FK
                context.getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI_STEPS, stepsValues);

            }



            // contentValues[i] = recipeValues;
        }
        // return contentValues;
    }

    public static String getResponseFromHttpUrl(String url) throws IOException {


        URL queryUrl = createUrlFromString(url);
        String jsonResponse = null;
        HttpURLConnection httpURLConnection = (HttpURLConnection) queryUrl.openConnection();
        try {
            InputStream inputStream = httpURLConnection.getInputStream();

            // Log any unsuccessful connection
            if (httpURLConnection.getResponseCode() != 200) {
                Log.e(LOG_TAG, "Response code " + httpURLConnection.getResponseCode());
            }
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");


            while (scanner.hasNext()) {
                jsonResponse = scanner.next();
            }
            scanner.close();
            return jsonResponse;
        } finally {
            httpURLConnection.disconnect();

        }
        //Log.i(LOG_TAG, jsonResponse);

    }

    private static URL createUrlFromString(String urlString) {
        URL queryURL = null;
        try {
            queryURL = new URL(urlString);
        } catch (MalformedURLException mfe) {
            Log.i(LOG_TAG, mfe.getMessage());
        }
        return queryURL;
    }

    /**
     * Checks internet connection status
     *
     * @param context
     * @return true if the user has a internet connection, false otherwise
     */
    public static boolean workingConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
}
