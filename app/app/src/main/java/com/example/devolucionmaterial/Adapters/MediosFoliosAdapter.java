package com.example.devolucionmaterial.Adapters;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.mediosfolios.ListMediosFolios;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.mediosfolios.MediosActivity;
import com.example.devolucionmaterial.beans.MedioFolios;
import com.example.devolucionmaterial.utils.CustomTextureVideoView;
import com.example.devolucionmaterial.utils.UIUtils;
import com.example.devolucionmaterial.utils.progress.ProgressImageView;
import com.example.devolucionmaterial.utils.progress.ProgressModelLoader;


import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by EDGAR ARANA on 15/03/2017.
 */

public class MediosFoliosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MedioFolios> medioFolioses;
    private Context context;
    private ClickListener clickListener;
    WeakReference<Activity> activityWeakReference;
    ViewHolderVideo viewHolderVideo;


    public MediosFoliosAdapter(Context context, List<MedioFolios> moviesList, Activity activity) {
        this.medioFolioses = moviesList;
        this.context = context;
        this.activityWeakReference = new WeakReference<>(activity);
    }

    private Activity getActivity() {
        return activityWeakReference.get();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_medio_folio, parent, false);

            return new ViewHolderImage(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_medio_video, parent, false);

            return new ViewHolderVideo(itemView);
        }

    }

    @Override
    public int getItemViewType(int position) {
        MedioFolios medioFolios = medioFolioses.get(position);
        if (medioFolios.getType() == 1) {
            return 1;
        } else {
            return 2;

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MedioFolios medioFolios = medioFolioses.get(position);
        if (medioFolios.getType() == 2)
            if (holder instanceof ViewHolderVideo) {

                ViewHolderVideo vhVideo = (ViewHolderVideo) holder;
                vhVideo.videoUrl = medioFolios.getUrl();
                vhVideo.imageLoaderProgressBar.setVisibility(View.INVISIBLE);
                vhVideo.videoImageView.setVisibility(View.VISIBLE);

            }

        if (medioFolios.getType() == 1)
            if (holder instanceof ViewHolderImage) {
                ViewHolderImage viewHolderImage = (ViewHolderImage) holder;
                Glide.with(context).using(new ProgressModelLoader(new MediosFoliosAdapter.ProgressHandler((ListMediosFolios) context, viewHolderImage.progressImageView))).
                        load(medioFolios.getUrl()).into(viewHolderImage.progressImageView.getImageView());
                viewHolderImage.main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null) {
                            clickListener.itemClicked(position);
                        }
                    }
                });
            }

    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof ViewHolderVideo) {
            viewHolderVideo = (ViewHolderVideo) holder;
            viewHolderVideo.stopVideo();
            //viewHolderVideo.videoView.stopPlayback();
            viewHolderVideo = null;
            super.onViewRecycled(holder);
        }
        if (holder instanceof ViewHolderImage) {
            ViewHolderImage viewHolderVideo = (ViewHolderImage) holder;
            super.onViewRecycled(viewHolderVideo);
        }


    }

    public void onScrolled(RecyclerView recyclerView) {
        if (viewHolderVideo != null) {
            viewHolderVideo.onScrolled(recyclerView);
        }
    }


    class ViewHolderImage extends RecyclerView.ViewHolder {
        private ProgressImageView progressImageView;
        FrameLayout main;

        ViewHolderImage(View view) {
            super(view);
            progressImageView = (ProgressImageView) view.findViewById(R.id.almf_iv_image);
            main = (FrameLayout) view.findViewById(R.id.almf_root);
        }
    }

    class ViewHolderVideo extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextureVideoView videoView;
        FrameLayout main;
        ImageView videoPlayImageButton;
        ProgressBar imageLoaderProgressBar;
        ImageView videoImageView;
        ImageView ivFullScren;

        String videoUrl;

        public String getVideoUrl() {
            return videoUrl;
        }


        ViewHolderVideo(View view) {
            super(view);
            videoView = (CustomTextureVideoView) view.findViewById(R.id.video_feed_item_video);
            main = (FrameLayout) view.findViewById(R.id.almf_root);
            videoPlayImageButton = (ImageView) view.findViewById(R.id.video_play_img_btn);
            imageLoaderProgressBar = (ProgressBar) view.findViewById(R.id.lyt_image_loader_progress_bar);
            videoImageView = (ImageView) view.findViewById(R.id.video_feed_item_video_image);
            ivFullScren = (ImageView) view.findViewById(R.id.imv_iv_full_screem);
            ivFullScren.setOnClickListener(this);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    Log.v("Video", "onPrepared" + videoView.getVideoPath());
                    int width = mp.getVideoWidth();
                    int height = mp.getVideoHeight();
                    videoView.setIsPrepared(true);
                    UIUtils.resizeView(videoView, UIUtils.getScreenWidth(getActivity()), UIUtils.getScreenWidth(getActivity()) * height / width);
                    if (viewHolderVideo == ViewHolderVideo.this) {
                        videoImageView.setVisibility(View.GONE);
                        imageLoaderProgressBar.setVisibility(View.INVISIBLE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.seekTo(0);
                        videoView.start();
                    }
                }
            });
            videoView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.v("Video", "onFocusChange" + hasFocus);
                    if (!hasFocus && viewHolderVideo == ViewHolderVideo.this) {
                        stopVideo();
                    }

                }
            });
            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    Log.v("Video", "onInfo" + what + " " + extra);

                    return false;
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.v("Video", "onCompletion");

                    videoImageView.setVisibility(View.VISIBLE);
                    videoPlayImageButton.setVisibility(View.VISIBLE);
                    ivFullScren.setVisibility(View.GONE);
                    if (videoView.getVisibility() == View.VISIBLE)
                        videoView.setVisibility(View.INVISIBLE);


                    imageLoaderProgressBar.setVisibility(View.INVISIBLE);
                    viewHolderVideo = null;
                }
            });
            videoPlayImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolderVideo != null && viewHolderVideo != ViewHolderVideo.this) {
                        viewHolderVideo.videoView.pause();
                        viewHolderVideo.videoImageView.setVisibility(View.INVISIBLE);
                        viewHolderVideo.videoPlayImageButton.setVisibility(View.VISIBLE);
                        viewHolderVideo.imageLoaderProgressBar.setVisibility(View.INVISIBLE);
                        if (viewHolderVideo.videoView.getVisibility() == View.VISIBLE)
                            viewHolderVideo.videoView.setVisibility(View.INVISIBLE);


                        viewHolderVideo = null;
                    }

                    viewHolderVideo = ViewHolderVideo.this;

                    videoPlayImageButton.setVisibility(View.INVISIBLE);
                    imageLoaderProgressBar.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    videoImageView.setVisibility(View.INVISIBLE);
                    ivFullScren.setVisibility(View.VISIBLE);
                    if (!getVideoUrl().equals(videoView.getVideoPath())) {
                        videoView.setIsPrepared(false);
                        videoView.setVideoPath(getVideoUrl());
                        videoView.requestFocus();
                    } else {
                        if (videoView.isPrepared()) {
                            imageLoaderProgressBar.setVisibility(View.INVISIBLE);
                        } else {
                            imageLoaderProgressBar.setVisibility(View.VISIBLE);
                        }

                        videoView.requestFocus();
                        videoView.seekTo(0);
                        videoView.start();
                    }
                }
            });
        }

        public void stopVideo() {
            Log.v("Video", "stopVideo");
            //imageView is within the visible window
            videoView.pause();
            if (videoView.getVisibility() == View.VISIBLE) {
                videoView.setVisibility(View.INVISIBLE);
            }
            videoImageView.setVisibility(View.VISIBLE);
            videoPlayImageButton.setVisibility(View.VISIBLE);
            imageLoaderProgressBar.setVisibility(View.INVISIBLE);
            viewHolderVideo = null;
        }

        public void onScrolled(RecyclerView recyclerView) {
            if (isViewNotVisible(videoPlayImageButton, recyclerView) || isViewNotVisible(imageLoaderProgressBar, recyclerView)) {
                //imageView is within the visible window
                stopVideo();
            }
        }

        public boolean isViewNotVisible(View view, RecyclerView recyclerView) {
            Rect scrollBounds = new Rect();
            recyclerView.getHitRect(scrollBounds);
            return view.getVisibility() == View.VISIBLE && !view.getLocalVisibleRect(scrollBounds);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imv_iv_full_screem:
                    Intent iMedios = new Intent(getActivity(), MediosActivity.class);
                    iMedios.putExtra("url", videoUrl);
                    context.startActivity(iMedios);
                    break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return medioFolioses.size();
    }

    public interface ClickListener {
        void itemClicked(int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private static class ProgressHandler extends Handler {

        private final WeakReference<Activity> mActivity;
        private final ProgressImageView mProgressImageView;


        public ProgressHandler(Activity activity, ProgressImageView progressImageView) {
            super(Looper.getMainLooper());
            mActivity = new WeakReference<>(activity);
            mProgressImageView = progressImageView;

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Activity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        int percent = msg.arg1 * 100 / msg.arg2;
                        mProgressImageView.setProgress(percent);
                        break;
                    default:
                        break;
                }
            }
        }
    }

}