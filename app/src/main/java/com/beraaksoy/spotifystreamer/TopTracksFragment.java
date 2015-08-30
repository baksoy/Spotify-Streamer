package com.beraaksoy.spotifystreamer;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TopTracksFragment extends Fragment {

    private TopTracksAdapter mTopTracksAdapter;
    private List<Track> mTopTracks;
    private ListView mTopTracksListView;
    private String mArtistId;


    public TopTracksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null && extras.containsKey("artistId")) {
            mArtistId = extras.getString("artistId");
            TopTracksTask topTracksTask = new TopTracksTask();
            topTracksTask.execute(mArtistId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.toptracks_fragment, container, false);
        return view;
    }

    public class TopTracksTask extends AsyncTask<String, Void, List<Track>> {
        @Override
        protected void onPostExecute(List<Track> toptracks) {
            super.onPostExecute(toptracks);

            //****** Track Data ******
            mTopTracks = toptracks;
            mTopTracksAdapter = new TopTracksAdapter(getActivity().getApplicationContext(), mTopTracks);
            mTopTracksListView = (ListView) getActivity().findViewById(R.id.topTracksListView);
            if (mTopTracksListView != null) {
                mTopTracksListView.setAdapter(mTopTracksAdapter);
            }

            mTopTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    List<Track> topTracks = mTopTracks;
                    Intent intent = new Intent(getActivity().getApplicationContext(), PlaybackActivity.class);
                    intent.putParcelableArrayListExtra("topTracks", (ArrayList<? extends Parcelable>) topTracks);
                    intent.putExtra("track_position", i);
                    startActivity(intent);
                }
            });
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
