package com.beraaksoy.spotifystreamer;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

public class ArtistSearchActivity extends Activity implements ArtistSearchFragment.Communicator {

    ArtistSearchFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artistsearch_activity);

        FragmentManager fm = getFragmentManager();
        mFragment = new ArtistSearchFragment();

        fm.beginTransaction().add(R.id.fragment1, mFragment).commit();
        mFragment.setCommunicator(this);

    }

    @Override
    public void respond(String artistId, String artistName) {
        Intent intent = new Intent(this, TopTracksActivity.class);
        intent.putExtra("artistId", artistId);
        intent.putExtra("artistName", artistName);
        startActivity(intent);
    }
}


