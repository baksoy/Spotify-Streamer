package com.beraaksoy.spotifydemo;


import android.content.Context;
import android.media.AudioManager;
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

public class PlaybackActivity extends ActionBarActivity implements View.OnTouchListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private SeekBar mSeekBarProgress;
    private MediaPlayer mMediaPlayer;
    private final Handler mHandler = new Handler();
    private String mSongPreviewUrl;
    private String mPreviewAlbumImgUrl;
    private ImageButton mPlaypauseButton;
    private int mMediaFileLengthInMilliseconds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.toptracks_playback);
        Bundle extras = getIntent().getExtras();
        mSongPreviewUrl = extras.getString("playback_url");
        mPreviewAlbumImgUrl = extras.getString("preview_album_img");
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        initView();

        mPlaypauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupMedia();
                if (!mMediaPlayer.isPlaying()) {
                    playMedia();
                } else {
                    pauseMedia();
                }
            }
        });


        audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {
                if (i == AudioManager.AUDIOFOCUS_LOSS) {
                    mMediaPlayer.stop();
                    audioManager.abandonAudioFocus(this);
                }
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    /**
     * Initialise all views
     */
    private void initView() {

        ImageView previewAlbumImg = (ImageView) findViewById(R.id.previewAlbumImg);
        mPlaypauseButton = (ImageButton) findViewById(R.id.buttonPlayPause);

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
                .into(previewAlbumImg);
    }

    /**
     * Method which updates the SeekBar primary progress by current song playing position
     */
//    private void seekBarProgressUpdater() {
//        mSeekBarProgress.setProgress((int) (((float) mMediaPlayer.getCurrentPosition() / mMediaPlayer.getDuration()) * 100)); // This math construction give a percentage of "was playing"/"song length"
//        Runnable notification = new Runnable() {
//            public void run() {
//                seekBarProgressUpdater();
//            }
//        };
//        mHandler.postDelayed(notification, 1000);
//    }
    private void primarySeekBarProgressUpdater() {
        mMediaFileLengthInMilliseconds = mMediaPlayer.getDuration(); // get the song length in milliseconds from URL
        mSeekBarProgress.setProgress((int) (((float) mMediaPlayer.getCurrentPosition() / mMediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"

        if (mMediaPlayer.isPlaying() || mMediaPlayer.getCurrentPosition() < mMediaFileLengthInMilliseconds - 1000) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            mHandler.postDelayed(notification, 1000);
        } else {
            //Reset seekbar to the beginning
            mSeekBarProgress.setProgress(0);
        }
    }

    private void pauseMedia() {
        mMediaPlayer.pause();
        mPlaypauseButton.setImageResource(android.R.drawable.ic_media_play);
    }

    private void playMedia() {
        mMediaPlayer.start();
        mPlaypauseButton.setImageResource(android.R.drawable.ic_media_pause);
        //seekBarProgressUpdater();
        primarySeekBarProgressUpdater();
    }

    private void setupMedia() {
        /* Setting up the media to be played */
        try {
            mMediaPlayer.setDataSource(mSongPreviewUrl); // setup song to mediaplayer data source
            mMediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.seekBarPreview) {
            /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
            if (mMediaPlayer.isPlaying()) {
                SeekBar sb = (SeekBar) v;
                int playPositionInMillisecconds = (mMediaPlayer.getDuration() / 100) * sb.getProgress();
                mMediaPlayer.seekTo(playPositionInMillisecconds);
            }
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        /** MediaPlayer onCompletion event handler. Method which calls when song playing is complete*/
        mPlaypauseButton.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        /** Method which updates the SeekBar secondary progress by current song loading from URL position*/
        mSeekBarProgress.setSecondaryProgress(percent);
    }

}
