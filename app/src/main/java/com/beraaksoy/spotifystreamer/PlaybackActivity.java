package com.beraaksoy.spotifystreamer;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class PlaybackActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toptracks_playback_activity);

        if (savedInstanceState == null) {
            PlaybackFragment fragment = new PlaybackFragment();

            //Receive Top Tracks info via Intent
            Bundle extras = getIntent().getExtras();

            fragment.setArguments(extras);

            getFragmentManager().beginTransaction()
                    .replace(R.id.toptracks_playback_fragment, fragment)
                    .commit();
        }
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
