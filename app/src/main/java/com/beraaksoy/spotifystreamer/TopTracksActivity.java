package com.beraaksoy.spotifystreamer;


import android.app.Activity;
import android.os.Bundle;

public class TopTracksActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toptracks_activity);
        Bundle extras = getIntent().getExtras();

        TopTracksFragment fragment = new TopTracksFragment();
        fragment.setArguments(extras);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment2, fragment)
                .commit();
    }
}
