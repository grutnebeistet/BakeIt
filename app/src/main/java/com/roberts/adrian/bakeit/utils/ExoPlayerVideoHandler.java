package com.roberts.adrian.bakeit.utils;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by Adrian on 09/09/2017.
 * copied from https://medium.com/tall-programmer/fullscreen-functionality-with-android-exoplayer-5fddad45509f
 */

public class ExoPlayerVideoHandler {
    final static String LOG_TAG = ExoPlayerVideoHandler.class.getSimpleName();
    private static ExoPlayerVideoHandler instance;

    public static ExoPlayerVideoHandler getInstance() {
        if (instance == null) {
            instance = new ExoPlayerVideoHandler();
        }
        return instance;
    }

    private SimpleExoPlayer mPlayer;
    private Uri playerUri;
    private boolean isPlayerPlaying;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private ExoPlayerVideoHandler() {
    }


    public void prepareExoPlayerForUri(Context context, Uri uri,
                                       SimpleExoPlayerView exoPlayerView) {
        if (context != null && uri != null && exoPlayerView != null) {
            if (!uri.equals(playerUri) || mPlayer == null) {
                TrackSelection.Factory adaptiveTrackSelectionFactory =
                        new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);
                LoadControl loadControl = new DefaultLoadControl();

                DefaultTrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);

                mPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
                // Create a new mPlayer if the mPlayer is null or
                // we want to play a new video
                playerUri = uri;

                String userAgent = Util.getUserAgent(context, "RecipeStepVideo");
                MediaSource mediaSource = new ExtractorMediaSource(playerUri, new DefaultDataSourceFactory(
                        context, userAgent), new DefaultExtractorsFactory(), null, null);

                mPlayer.prepare(mediaSource);

            }
            mPlayer.clearVideoSurface();
            mPlayer.setVideoSurfaceView(
                    (SurfaceView) exoPlayerView.getVideoSurfaceView());
            mPlayer.seekTo(mPlayer.getCurrentPosition() + 1);
            exoPlayerView.setPlayer(mPlayer);
            isPlayerPlaying = true;
        }
    }


    public void releaseVideoPlayer() {
        if (mPlayer != null) {
            mPlayer.release();
        }
        mPlayer = null;
    }

    public void goToBackground() {
        if (mPlayer != null) {
            isPlayerPlaying = mPlayer.getPlayWhenReady();
            mPlayer.setPlayWhenReady(false);
        }
    }

    public void goToForeground() {
        if (mPlayer != null) {
            mPlayer.setPlayWhenReady(isPlayerPlaying);
        }
    }
}