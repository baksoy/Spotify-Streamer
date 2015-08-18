package com.beraaksoy.spotifydemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

public class ArtistSearchActivity extends ActionBarActivity {

    private ArtistSearchAdapter mArtistSearchAdapter;
    public List<Artist> mArtists;
    private ListView mArtistListView;
    private EditText mArtistSearchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artistsearch_list);

        mArtistSearchInput = (EditText) findViewById(R.id.artistSearchInput);
        mArtistSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String searchString = charSequence.toString();

                if (!searchString.isEmpty()) {
                    ArtistSearchTask artistTopTrackTast = new ArtistSearchTask();
                    artistTopTrackTast.execute(searchString);
                } else {
                    //If search bar is empty
                    mArtistSearchAdapter.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ArtistSearchTask extends AsyncTask<String, Void, List<Artist>> {
        @Override
        protected void onPostExecute(List<Artist> artists) {
            super.onPostExecute(artists);

            mArtists = artists;

            mArtistSearchAdapter = new ArtistSearchAdapter(getApplicationContext(), mArtists);
            mArtistListView = (ListView) findViewById(R.id.artistsListView);
            if (mArtistListView != null) {
                mArtistListView.setAdapter(mArtistSearchAdapter);
            }



        // for (int i = 0; i < artists.size(); i++) {
        //     Artist artist = artists.get(i);
        //     Log.i("ARTIST", i + " " + artist.name);
        // }

        mArtistListView.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick (AdapterView < ? > adapterView, View view,int i, long l){
            String artistId = mArtists.get(i).id;
            String artistName = mArtists.get(i).name;
            Intent intent = new Intent(getApplicationContext(), TopTracksActivity.class);
            intent.putExtra("artistId", artistId);
            intent.putExtra("artistName", artistName);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), artistName, Toast.LENGTH_SHORT).show();
        }
        }

        );
    }

    @Override
    protected List<Artist> doInBackground(String... params) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();

        ArtistsPager artistResults = service.searchArtists(params[0]);
        List<Artist> artists = artistResults.artists.items;

        return artists;
    }
}

}


