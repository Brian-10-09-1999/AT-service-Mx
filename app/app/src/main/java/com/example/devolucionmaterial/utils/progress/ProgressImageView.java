package com.example.devolucionmaterial.utils.progress;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by chenpengfei on 2016/11/9.
 */
public class ProgressImageView extends RelativeLayout {

    private ImageView mImageView;
    private TextView mProgressTextView;
    private ProgressBar progressBar;
    private int mProgress;

    public ProgressImageView(Context context) {
        super(context);
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mImageView = new ImageView(context);
            LayoutParams ivLp = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //ivLp.addRule(CENTER_IN_PARENT);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setLayoutParams(ivLp);
            mImageView.setTransitionName("shared_image");
            addView(mImageView);


        } else {

            mImageView = new ImageView(context);
            LayoutParams ivLp = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setLayoutParams(ivLp);
            addView(mImageView);

        }


        mProgressTextView = new TextView(context);
        mProgressTextView.setTextSize(10);
        mProgressTextView.setTextColor(Color.RED);
        LayoutParams tvLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLp.addRule(CENTER_IN_PARENT);
        mProgressTextView.setLayoutParams(tvLp);
        addView(mProgressTextView);


        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.INVISIBLE);
        LayoutParams params = new LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(progressBar, params);


    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        mProgressTextView.setText(mProgress + "%");
        progressBar.setVisibility(VISIBLE);
        if (mProgress == 100) {
            mProgressTextView.setVisibility(INVISIBLE);
            progressBar.setVisibility(INVISIBLE);
        }
    }

    public ImageView getImageView() {
        return mImageView;
    }
}
