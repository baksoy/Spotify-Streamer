package com.beraaksoy.spotifydemo;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TopTracksActivity extends ActionBarActivity {

    private TopTracksAdapter mTopTracksAdapter;
    private List<Track> mTopTracks;
    private ListView mTopTracksListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toptracks_list);
        Bundle extras = getIntent().getExtras();

        String artistId = extras.getString("artistId");
        String artistName = extras.getString("artistName");

        TopTracksTask topTracksTask = new TopTracksTask();
        topTracksTask.execute(artistId);
    }

    public class TopTracksTask extends AsyncTask<String, Void, List<Track>> {
        @Override
        protected void onPostExecute(List<Track> tracks) {
            super.onPostExecute(tracks);

            mTopTracks = tracks;

            mTopTracksAdapter = new TopTracksAdapter(getApplicationContext(), mTopTracks);
            mTopTracksListView = (ListView) findViewById(R.id.topTracksListView);
            if (mTopTracksListView != null) {
                mTopTracksListView.setAdapter(mTopTracksAdapter);
            }

            // for (int i = 0; i < tracks.size(); i++) {
            //    Track track = tracks.get(i);
            //    Log.i("TRACK_NAME", i+1 + " " + track.name);
            // }
        }

        @Override
        protected List<Track> doInBackground(String... artistId) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();

            Tracks trackResults = service.getArtistTopTrack(artistId[0], "US");
            List<Track> tracks = trackResults.tracks;

            return tracks;
        }
    }
}
