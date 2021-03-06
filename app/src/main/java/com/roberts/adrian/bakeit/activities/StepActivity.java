package com.roberts.adrian.bakeit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.utils.ExoPlayerVideoHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = StepActivity.class.getSimpleName();
    private String mStepTitle;
    private String mStepDescription;
    private Uri mStepVideoUri;
    private String mStringVideoUrl;

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

    //VideoView mExoVideoPlayerView;
    private boolean destroyVideo = true;
    private View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);


        mRootView = findViewById(R.id.root_step_layout);
        mRootView.setOnClickListener(this);


        Bundle stepDescriptions = getIntent().getExtras();
        mStepTitle = stepDescriptions.getString(getString(R.string.steps_bundle_title));
        setTitle(mStepTitle);
        mStepDescription = stepDescriptions.getString(getString(R.string.steps_bundle_description));


        try {
            mStringVideoUrl = stepDescriptions.getString(getString(R.string.steps_bundle_video_url));
            mStepVideoUri = Uri.parse(mStringVideoUrl);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        mTitleTextView.setText(mStepTitle);
        mDescriptionTextView.setText(mStepDescription);

        if (mStepVideoUri == null ||
                Uri.EMPTY.equals(mStepVideoUri)) {
            mNoVideoImageView.setVisibility(View.VISIBLE);
            mExoVideoPlayerView.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.missing_video), Toast.LENGTH_SHORT).show();

        } else {
            mNoVideoImageView.setVisibility(View.GONE);
            mExoVideoPlayerView.setVisibility(View.VISIBLE);

        }


    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        try {
            ExoPlayerVideoHandler.getInstance()
                    .prepareExoPlayerForUri(mRootView.getContext(),
                            Uri.parse(mStringVideoUrl), mExoVideoPlayerView);
            ExoPlayerVideoHandler.getInstance().goToForeground();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        mRootView.findViewById(R.id.exo_fullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destroyVideo = false;
                Intent intent = new Intent(StepActivity.this, FullScreenVideoActivity.class);
                intent.putExtra("videoUri", mStringVideoUrl);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        ExoPlayerVideoHandler.getInstance().goToBackground();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
        if (this.isChangingConfigurations()) {
            Log.i(TAG, "configuration is changing: keep playing");
        } else if (destroyVideo) {
            ExoPlayerVideoHandler.getInstance().releaseVideoPlayer();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        destroyVideo = true;
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onClick(View v) {

    }


}
