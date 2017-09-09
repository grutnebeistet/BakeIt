package com.roberts.adrian.bakeit.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roberts.adrian.bakeit.R;
import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;
import com.roberts.adrian.bakeit.fragments.DetailsStepsFragment;
import com.squareup.picasso.Picasso;

import static com.roberts.adrian.bakeit.R.drawable.defaul_baking_step;

/**
 * Created by Adrian on 30/07/2017.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {
    private static final String LOG_TAG = StepsAdapter.class.getSimpleName();
    private Context mContext;
    private Cursor mCursor;

    final private StepsAdapterOnclickHandler mOnclickHandler;


    public interface StepsAdapterOnclickHandler {
        void onClick(Bundle stepDescriptions);
    }

    public StepsAdapter(Context context, StepsAdapterOnclickHandler clickHandler) {
        mContext = context;
        mOnclickHandler = clickHandler;

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        // To let espresso know that the recyclerview is attached to the adapter
        RecipeDetailzActivity.mSyncFinished = true;
        Log.i(LOG_TAG, "RecipeDetailzActivity.mSyncFinished = true;");
    }

    @Override
    public void onBindViewHolder(StepsViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String stepShortDescription = mCursor.getString(DetailsStepsFragment.PROJECTION_INDEX_STEP_SHORT_DESC);
        String stepThumbnail = mCursor.getString(DetailsStepsFragment.PROJECTION_INDEX_STEP_IMG);

        Uri imageUri = stepThumbnail == null ? Uri.parse("") : Uri.parse(stepThumbnail);
        // Current JSON provides no images - setting a default placeholder for now
        Picasso.with(mContext)
                .load(imageUri).fit()
                .placeholder(defaul_baking_step)
                .error(defaul_baking_step)
                .centerCrop()
                .into(holder.imageViewStepThumbnail);

        holder.stepDescriptionTv.setText(stepShortDescription);

    }

    @Override
    public StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item_recipe_step, parent, false);
        return new StepsViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView stepDescriptionTv;
        ImageView imageViewStepThumbnail;

        StepsViewHolder(View view) {
            super(view);
            stepDescriptionTv = (TextView) view.findViewById(R.id.tv_step_descr);
            imageViewStepThumbnail = (ImageView)view.findViewById(R.id.image_view_step_image);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mCursor.isClosed()) return;
            mCursor.moveToPosition(getAdapterPosition());
            Log.i(LOG_TAG, "adapterPos: " + getAdapterPosition());


            Bundle stepDetailsBundle = new Bundle();
            String stepShortDescription = mCursor.getString(DetailsStepsFragment.PROJECTION_INDEX_STEP_SHORT_DESC);
            String stepDescription = mCursor.getString(DetailsStepsFragment.PROJECTION_INDEX_STEP_DESC);
            String stepVideoUrl = mCursor.getString(DetailsStepsFragment.PROJECTION_INDEX_STEP_VIDEO);
            String stepImageUrl = mCursor.getString(DetailsStepsFragment.PROJECTION_INDEX_STEP_IMG);

            stepDetailsBundle.putString(mContext.getString(R.string.steps_bundle_title), stepShortDescription);
            stepDetailsBundle.putString(mContext.getString(R.string.steps_bundle_description), stepDescription);
            stepDetailsBundle.putString(mContext.getString(R.string.steps_bundle_video_url), stepVideoUrl);
            stepDetailsBundle.putString(mContext.getString(R.string.steps_bundle_image_url), stepImageUrl);
            // TODO add thumbnail? (Non in JSON)

            //mOnclickHandler.onClick(mCursor.getString(RecipeDetailsActivity.PROJECTION_INDEX_STEP_ID));
            mOnclickHandler.onClick(stepDetailsBundle);
        }
    }
}