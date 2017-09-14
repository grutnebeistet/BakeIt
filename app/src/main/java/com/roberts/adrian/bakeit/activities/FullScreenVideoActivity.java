package com.roberts.adrian.bakeit.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.utils.ExoPlayerVideoHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Adrian on 09/09/2017.
 */


public class FullScreenVideoActivity extends AppCompatActivity implements
        View.OnClickListener {
    final String LOG_TAG = FullScreenVideoActivity.class.getSimpleName();

    @BindView(R.id.playerView)
    SimpleExoPlayerView mExoplayerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_fullscreen);
        ButterKnife.bind(this);

    }

    private boolean destroyVideo = true;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        String videoUrl = getIntent().getExtras().getString("videoUri", null);
        ExoPlayerVideoHandler.getInstance()
                .prepareExoPlayerForUri(getApplicationContext(),
                        Uri.parse(videoUrl), mExoplayerView);
        ExoPlayerVideoHandler.getInstance().goToForeground();

        Log.i(LOG_TAG, "Handler INstance: " + ExoPlayerVideoHandler.getInstance().toString());

        findViewById(R.id.exo_fullscreen).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        destroyVideo = false;
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        destroyVideo = false;
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        ExoPlayerVideoHandler.getInstance().goToBackground();
    }

    @Override
    protected void onStop() {
        if (destroyVideo) {
            ExoPlayerVideoHandler.getInstance().releaseVideoPlayer();
        }
        super.onStop();
    }


    @Override
    public void onClick(View view) {

    }
}
