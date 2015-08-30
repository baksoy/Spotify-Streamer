package com.beraaksoy.spotifystreamer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class PlaybackActivity extends Activity {

    boolean mIsLargeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toptracks_playback_activity);

        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);

        if (savedInstanceState == null) {
            showDialog();
        }
    }

    public void showDialog() {
        //Receive Top Tracks info via Intent
        Bundle extras = getIntent().getExtras();

        FragmentManager fragmentManager = getFragmentManager();
        PlaybackFragment fragment = new PlaybackFragment();

        fragment.setArguments(extras);

        if (mIsLargeLayout) {
            fragment.show(fragmentManager, "dialog");
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(R.id.playback_fragment, fragment).addToBackStack(null).commit();
        }
    }
}
