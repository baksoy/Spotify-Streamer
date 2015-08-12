package com.beraaksoy.spotifydemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

public class ArtistSearchAdapter extends ArrayAdapter<Artist> {

    public ArtistSearchAdapter(Context context, List<Artist> artists) {
        super(context, R.layout.artist_row, artists);
    }

    private static class ArtistHolder {
        TextView artistName;
        ImageView artistImg;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Get artist from artist array
        Artist artist = getItem(position);

        View row = convertView;
        ArtistHolder holder = null;

        //starting fresh as we do not have a row view yet
        if (row == null) {
            //Making a new view
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.artist_row, parent, false);
            holder = new ArtistHolder();

            //Get reference to different view elements to be updated
            holder.artistName = (TextView) row.findViewById(R.id.artistName);
            holder.artistImg = (ImageView) row.findViewById(R.id.artistImg);

            row.setTag(holder);
        } else {
            //Otherwise you the existing view
            holder = (ArtistHolder) row.getTag();
        }

        String url;
        if (artist.images.isEmpty()) {
            url = "http://i.imgur.com/jx8ihcj.png?1";
        } else {
            int i = artist.images.size() - 1;
            url = artist.images.get(i).url;
        }

        //Set the view to reflect the data to be displayed
        holder.artistName.setText(artist.name);
        Picasso.with(getContext())
                .load(url)
                .resize(200, 200)
                .centerCrop()
                .into(holder.artistImg);

        return row;

    }
}
