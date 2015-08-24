package com.beraaksoy.spotifystreamer;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TopTracksActivity extends Activity {

    private TopTracksAdapter mTopTracksAdapter;
    private List<Track> mTopTracks;
    private ListView mTopTracksListView;
    private String mArtistId;
    //private String mArtistName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toptracks_list);
        Bundle extras = getIntent().getExtras();

        //****** Artist Data ******
        mArtistId = extras.getString("artistId");
        //mArtistName = extras.getString("artistName");

        TopTracksTask topTracksTask = new TopTracksTask();
        topTracksTask.execute(mArtistId);
    }

    public class TopTracksTask extends AsyncTask<String, Void, List<Track>> {
        @Override
        protected void onPostExecute(List<Track> toptracks) {
            super.onPostExecute(toptracks);

            //****** Track Data ******
            mTopTracks = toptracks;
            mTopTracksAdapter = new TopTracksAdapter(getApplicationContext(), mTopTracks);
            mTopTracksListView = (ListView) findViewById(R.id.topTracksListView);
            if (mTopTracksListView != null) {
                mTopTracksListView.setAdapter(mTopTracksAdapter);
            }

//             for (int i = 0; i < mTopTracks.size(); i++) {
//                String playback_url = mTopTracks.get(i).preview_url;
//                Track track = mTopTracks.get(i);
//                Log.i("TRACK_NAME", i + 1 + " " + track.name);
//                Log.i("TRACK_URL_TTACTIVITY", playback_url);
//             }

            mTopTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    List<Track> topTracks = mTopTracks;
                    Intent intent = new Intent(getApplicationContext(), PlaybackActivity.class);
                    intent.putParcelableArrayListExtra("topTracks", (ArrayList<? extends Parcelable>) topTracks);
                    intent.putExtra("track_position", i);
                    startActivity(intent);
                }
            });
        }

//        private String getAlbumImgUrl(int i) {
//            int j = mTopTracks.get(i).album.images.size() - 3;
//            String url = mTopTracks.get(i).album.images.get(j).url;
//            return url;
//        }

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
