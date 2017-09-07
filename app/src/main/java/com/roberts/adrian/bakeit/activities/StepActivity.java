package com.roberts.adrian.bakeit.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.roberts.adrian.bakeit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepActivity extends AppCompatActivity implements
        View.OnClickListener, ExoPlayer.EventListener {
    private static final String TAG = StepActivity.class.getSimpleName();
    private String mStepTitle;
    private String mStepDescription;
    private Uri mStepVideoUrl;
    private DefaultTrackSelector trackSelector;
    //   private TrackSelectionHelper trackSelectionHelper;
    private Handler mHandler;
    @BindView(R.id.step_details_description)
    TextView mDescriptionTextView;
    @BindView(R.id.step_details_title)
    TextView mTitleTextView;
    @BindView(R.id.image_view_no_video)
    ImageView mNoVideoImageView;
    @BindView(R.id.playerView)
    SimpleExoPlayerView mExoVideoPlayerView;

    SimpleExoPlayer mPlayer;


    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);

        View rootView = findViewById(R.id.root_step_layout);
        rootView.setOnClickListener(this);

        // mExoVideoPlayerView.setControllerVisibilityListener(this);
        //mExoVideoPlayerView.requestFocus();

        Bundle stepDescriptions = getIntent().getExtras();
        mStepTitle = stepDescriptions.getString(getString(R.string.steps_bundle_title));
        setTitle(mStepTitle);
        mStepDescription = stepDescriptions.getString(getString(R.string.steps_bundle_description));
        try {
            mStepVideoUrl = Uri.parse(stepDescriptions.getString(getString(R.string.steps_bundle_video_url)));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        //  mTitleTextView.setText(mStepTitle);
        mDescriptionTextView.setText(mStepDescription);

        Bitmap defaultArtwork = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_empty_video);
      //  mExoVideoPlayerView.setDefaultArtwork(defaultArtwork);

        // Initialize the Media Session.
        initializeMediaSession();

        if (mStepVideoUrl == null ||
                Uri.EMPTY.equals(mStepVideoUrl)) {
            mNoVideoImageView.setVisibility(View.VISIBLE);
            mExoVideoPlayerView.setVisibility(View.GONE);
            Toast.makeText(this, "Missing video for this step", Toast.LENGTH_SHORT).show();

        } else {
            mNoVideoImageView.setVisibility(View.GONE);
            mExoVideoPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(mStepVideoUrl);
            Log.i(TAG, mStepVideoUrl.toString());
        }


    }

    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(this, TAG);

        // Enable callbacks from MediaButtons and TransporControls
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS |
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }


    private void initializePlayer(Uri mediaUri) {
        Log.i(TAG, "initPlayer");
        if (mPlayer == null) {
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);

            trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
            //trackSelectionHelper = new TrackSelectionHelper(trackSelector, adaptiveTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();
            mPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mExoVideoPlayerView.setPlayer(mPlayer);

            mPlayer.addListener(this);

            String userAgent = Util.getUserAgent(this, "RecipeStepVideo");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mPlayer.prepare(mediaSource);
            mPlayer.setPlayWhenReady(true);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }


    @Override
    public void onClick(View v) {

    }


    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync.
     *
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}
