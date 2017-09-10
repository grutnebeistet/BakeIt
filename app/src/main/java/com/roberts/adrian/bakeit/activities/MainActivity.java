package com.roberts.adrian.bakeit.activities;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.sync.RecipesSyncUtils;
import com.roberts.adrian.bakeit.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static boolean mSyncFinished;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG, "oncreate");

        Boolean connection = NetworkUtils.workingConnection(this);
        //   getSupportLoaderManager().initLoader(ID_LOADER, null, this);
        if (connection) {
            RecipesSyncUtils.initialize(this);
        }


    }


    @VisibleForTesting
    public static boolean isSyncFinished() {
        return mSyncFinished;
    }


}
