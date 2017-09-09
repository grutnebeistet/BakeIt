package com.roberts.adrian.bakeit.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.utils.ExoPlayerVideoHandler;

/**
 * Created by Adrian on 09/09/2017.
 */

public class FullScreenVideoActivity  extends AppCompatActivity implements
        View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_fullscreen);

    }

    private boolean destroyVideo = true;
    @Override
    protected void onResume(){
        super.onResume();
        String videoUrl = getIntent().getExtras().getString("videoUri", null);
        SimpleExoPlayerView exoPlayerView =
                (SimpleExoPlayerView)findViewById(R.id.playerView);
        ExoPlayerVideoHandler.getInstance()
                .prepareExoPlayerForUri(getApplicationContext(),
                        Uri.parse(videoUrl), exoPlayerView);
        ExoPlayerVideoHandler.getInstance().goToForeground();

        findViewById(R.id.exo_fullscreen).setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        destroyVideo = false;
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed(){
        destroyVideo = false;
        super.onBackPressed();
    }

    @Override
    protected void onPause(){
        super.onPause();
        ExoPlayerVideoHandler.getInstance().goToBackground();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(destroyVideo){
            ExoPlayerVideoHandler.getInstance().releaseVideoPlayer();
        }
    }

    @Override
    public void onClick(View view) {

    }
}
