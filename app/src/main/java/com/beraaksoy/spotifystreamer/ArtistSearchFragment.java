package com.beraaksoy.spotifystreamer;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

public class ArtistSearchFragment extends Fragment {

    private ArtistSearchAdapter mArtistSearchAdapter;
    public List<Artist> mArtists;
    private ListView mArtistListView;
    private String mArtistSearchString;
    private EditText mArtistSearchInput;
    private Communicator communicator;

    public ArtistSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artistsearch_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        communicator = (Communicator) getActivity();

        mArtistSearchInput = (EditText) getActivity().findViewById(R.id.artistSearchInput);
        mArtistSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mArtistSearchString = charSequence.toString();

                if (!mArtistSearchString.isEmpty()) {
                    ArtistSearchTask artistTopTrackTast = new ArtistSearchTask();
                    artistTopTrackTast.execute(mArtistSearchString);
                } else {
                    //If search bar is empty and adapter is not null
                    if (mArtistSearchAdapter != null) {
                        mArtistSearchAdapter.clear();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    public class ArtistSearchTask extends AsyncTask<String, Void, List<Artist>> {

        @Override
        protected void onPostExecute(List<Artist> artists) {
            super.onPostExecute(artists);

            //Network error feedback if artist list is null
            if (artists == null) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.network_error_message, Toast.LENGTH_SHORT).show();
                return;
            }

            //If artist search returns nothing, ask user to refine search
            if (artists.size() == 0 && mArtistSearchInput.getText().toString().length() > 0) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.artist_not_found_message, Toast.LENGTH_SHORT).show();
            }

            mArtists = artists;

            mArtistSearchAdapter = new ArtistSearchAdapter(getActivity().getApplicationContext(), mArtists);
            mArtistListView = (ListView) getActivity().findViewById(R.id.artistsListView);
            if (mArtistListView != null) {
                mArtistListView.setAdapter(mArtistSearchAdapter);
            }

            mArtistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                       @Override
                                                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                           String artistId = mArtists.get(i).id;
                                                           String artistName = mArtists.get(i).name;
                                                           communicator.respond(artistId, artistName);
                                                           Toast.makeText(getActivity().getApplicationContext(), artistName, Toast.LENGTH_SHORT).show();
                                                       }
                                                   }

            );
        }

        @Override
        protected List<Artist> doInBackground(String... params) {
            String queryString = params[0];

            //Changing the search into a like-type search
            queryString = "*" + queryString + "*";

            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService service = api.getService();
                ArtistsPager artistResults = service.searchArtists(queryString);
                List<Artist> artists = artistResults.artists.items;
                return artists;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }

    public interface Communicator {
        public void respond(String artistId, String artistName);
    }

}
