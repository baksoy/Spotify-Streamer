package com.beraaksoy.spotifydemo;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.squareup.picasso.Picasso;

public class PlaybackActivity extends ActionBarActivity implements View.OnClickListener, View.OnTouchListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private ImageButton mButtonPlayPause;
    private SeekBar mSeekBarProgress;
    private MediaPlayer mMediaPlayer;
    private int mMediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class
    private final Handler mHandler = new Handler();
    private String mSongPreviewUrl;
    private String mPreviewAlbumImgUrl;
    private ImageView mPreviewAlbumImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toptracks_playback);
        Bundle extras = getIntent().getExtras();
        mSongPreviewUrl = extras.getString("playback_url");
        mPreviewAlbumImgUrl = extras.getString("preview_album_img");
        initView();
    }

    /**
     * Initialise all views
     */
    private void initView() {

        mPreviewAlbumImg = (ImageView) findViewById(R.id.previewAlbumImg);
        mButtonPlayPause = (ImageButton) findViewById(R.id.buttonPlayPause);
        mButtonPlayPause.setOnClickListener(this);

        mSeekBarProgress = (SeekBar) findViewById(R.id.seekBarPreview);
        mSeekBarProgress.setMax(99); // It means 100% .0-99
        mSeekBarProgress.setOnTouchListener(this);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        Log.i("PLAYBACK_URL", mPreviewAlbumImgUrl);

        Picasso.with(getApplicationContext())
                .load(mPreviewAlbumImgUrl)
                .resize(640, 640)
                .centerInside()
                .into(mPreviewAlbumImg);
    }

    /**
     * Method which updates the SeekBar primary progress by current song playing position
     */
    private void primarySeekBarProgressUpdater() {
        mSeekBarProgress.setProgress((int) (((float) mMediaPlayer.getCurrentPosition() / mMediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (mMediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            mHandler.postDelayed(notification, 1000);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonPlayPause) {
            /** ImageButton onClick event handler. Method which start/pause mediaplayer playing */
            try {
                mMediaPlayer.setDataSource(mSongPreviewUrl); // setup song to mediaplayer data source
                mMediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMediaFileLengthInMilliseconds = mMediaPlayer.getDuration(); // get the song length in milliseconds from URL

            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                mButtonPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                mMediaPlayer.pause();
                mButtonPlayPause.setImageResource(android.R.drawable.ic_media_play);
            }
            primarySeekBarProgressUpdater();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.seekBarPreview) {
            /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
            if (mMediaPlayer.isPlaying()) {
                SeekBar sb = (SeekBar) v;
                int playPositionInMillisecconds = (mMediaFileLengthInMilliseconds / 100) * sb.getProgress();
                mMediaPlayer.seekTo(playPositionInMillisecconds);
            }
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        /** MediaPlayer onCompletion event handler. Method which calls when song playing is complete*/
        mButtonPlayPause.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        /** Method which updates the SeekBar secondary progress by current song loading from URL position*/
        mSeekBarProgress.setSecondaryProgress(percent);
    }
}
