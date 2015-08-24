package com.beraaksoy.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

public class PlaybackActivity extends Activity implements View.OnTouchListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener {

    private SeekBar mSeekBarProgress;
    private MediaPlayer mMediaPlayer;
    private final Handler mHandler = new Handler();
    private String mCurrentSongUrl;
    private String mPreviewAlbumImgUrl;
    private ImageButton mPlayPrevButton;
    private ImageButton mPlayPauseButton;
    private ImageButton mPlayNextButton;
    private int mMediaFileLengthInMilliseconds;
    private AudioManager audioManager;
    private String mArtistName;
    private String mAlbumName;
    private String mTrackName;
    private List<Track> mTopTracks;
    private int mTrackPosition;
    private TextView mBeginTrackTime;
    private TextView mEndTrackTime;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toptracks_playback);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        //Receive Top Tracks info via Intent
        Bundle extras = getIntent().getExtras();
        mTopTracks = extras.getParcelableArrayList("topTracks");
        mTrackPosition = extras.getInt("track_position");

        //Set up and initialize activity
        setupMedia();
        initView();
        playMedia();

        for (int i = 0; i < mTopTracks.size(); i++) {
            Track track = mTopTracks.get(i);
            Log.i("TRACK_NAME", i + ": " + track.name);
        }

        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mMediaPlayer.isPlaying()) {
                    playMedia();
                } else {
                    pauseMedia();
                }
            }
        });

        mPlayPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlayer.stop();
                if (mTrackPosition > 0) {
                    mTrackPosition = mTrackPosition - 1;
                } else {
                    mTrackPosition = mTopTracks.size() - 1;
                }
                mMediaPlayer.start();
                setupMedia();
                initView();
                playMedia();
            }
        });

        mPlayNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlayer.stop();
                if (mTrackPosition < mTopTracks.size() - 1) {
                    mTrackPosition = mTrackPosition + 1;
                } else {
                    mTrackPosition = 0;
                }
                mMediaPlayer.start();
                setupMedia();
                initView();
                playMedia();
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

    //Fetch album cover image
    private String getAlbumImgUrl() {
        return mTopTracks.get(mTrackPosition).album.images.get(mTopTracks.get(mTrackPosition).album.images.size() - 3).url;
    }

    //Initialise views
    private void initView() {
        ImageView previewAlbumImg = (ImageView) findViewById(R.id.previewAlbumImg);
        TextView previewArtistName = (TextView) findViewById(R.id.previewArtistName);
        TextView previewAlbumName = (TextView) findViewById(R.id.previewAlbumName);
        TextView previewTrackName = (TextView) findViewById(R.id.previewTrackName);
        mBeginTrackTime = (TextView) findViewById(R.id.beginTrackTime);
        mEndTrackTime = (TextView) findViewById(R.id.endTrackTime);


        mPlayPrevButton = (ImageButton) findViewById(R.id.buttonPlayPrev);
        mPlayPauseButton = (ImageButton) findViewById(R.id.buttonPlayPause);
        mPlayNextButton = (ImageButton) findViewById(R.id.buttonPlayNext);

        mSeekBarProgress = (SeekBar) findViewById(R.id.seekBarPreview);
        mSeekBarProgress.setMax(99); // It means 100% .0-99
        mSeekBarProgress.setOnTouchListener(this);


        //Setting the text on Widgets
        previewArtistName.setText(mArtistName);
        previewAlbumName.setText(mAlbumName);
        previewTrackName.setText(mTrackName);
        mEndTrackTime.setText(String.valueOf(timeConversion(mMediaPlayer.getDuration() / 1000)));

        //Setting the preview album cover photo
        Picasso.with(getApplicationContext())
                .load(mPreviewAlbumImgUrl)
                .resize(640, 640)
                .centerInside()
                .into(previewAlbumImg);
    }

    //Update SeekBar primary progress by current song playing position
    private void primarySeekBarProgressUpdater() {
        mMediaFileLengthInMilliseconds = mMediaPlayer.getDuration(); // get the song length in milliseconds from URL
        mSeekBarProgress.setProgress((int) (((float) mMediaPlayer.getCurrentPosition() / mMediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (mMediaPlayer.isPlaying() || mMediaPlayer.getCurrentPosition() < mMediaFileLengthInMilliseconds - 500) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                    if (mMediaPlayer.getCurrentPosition() < mMediaFileLengthInMilliseconds - 500) {
                        mBeginTrackTime.setText(String.valueOf(timeConversion(mMediaPlayer.getCurrentPosition() / 1000)));
                    }
                }
            };
            mHandler.postDelayed(notification, 1000);
        } else {
            //Reset seekbar to the beginning
            mSeekBarProgress.setProgress(0);
            mBeginTrackTime.setText(R.string.begin_track_time);
        }
    }

    private void pauseMedia() {
        mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
        mMediaPlayer.pause();
    }

    private void playMedia() {
        mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        mMediaPlayer.start();
        primarySeekBarProgressUpdater();
    }

    //Set up the media to be played
    private void setupMedia() {
        //mCurrentSongUrl = extras.getString("playback_url");
        mCurrentSongUrl = mTopTracks.get(mTrackPosition).preview_url;
        //mArtistName = extras.getString("artist_name");
        mArtistName = mTopTracks.get(mTrackPosition).artists.get(0).name;

        //mAlbumName = extras.getString("album_name");
        mAlbumName = mTopTracks.get(mTrackPosition).album.name;

        //mPreviewAlbumImgUrl = extras.getString("preview_album_img");
        mPreviewAlbumImgUrl = getAlbumImgUrl();

        //mTrackName = extras.getString("track_name");
        mTrackName = mTopTracks.get(mTrackPosition).name;

        try {
            mMediaPlayer.setDataSource(mCurrentSongUrl); // setup song to mediaplayer data source
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
        if (mMediaPlayer.isPlaying()) {
            mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        /** Method which updates the SeekBar secondary progress by current song loading from URL position*/
        mSeekBarProgress.setSecondaryProgress(percent);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
    }

    private static String timeConversion(int totalSeconds) {
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60;
        return minutes + ":" + seconds;
    }
}
