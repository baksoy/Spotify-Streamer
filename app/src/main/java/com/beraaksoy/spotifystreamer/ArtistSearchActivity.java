package com.beraaksoy.spotifystreamer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ArtistSearchActivity extends Activity implements ArtistSearchFragment.Communicator {

    ArtistSearchFragment mFragment;
    private boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artistsearch_activity);

        mFragment = new ArtistSearchFragment();
        mFragment.setCommunicator(this);

        //Detecting whether we are on a tablet or not
        if (findViewById(R.id.fragment2) != null) {
            isTwoPane = true;
        }
    }

    @Override
    public void respond(String artistId, String artistName) {

        Bundle bundle = new Bundle();
        bundle.putString("artistId", artistId);
        bundle.putString("artistName", artistName);

        //if we are in a tablet, send artist information directly to the TopTracksFragment
        if (isTwoPane) {
            TopTracksFragment fragment = new TopTracksFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment2, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, TopTracksActivity.class);
            intent.putExtra("artistId", artistId);
            intent.putExtra("artistName", artistName);
            startActivity(intent);
        }
    }
}


