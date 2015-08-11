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

import kaaes.spotify.webapi.android.models.Track;

public class TopTracksAdapter extends ArrayAdapter<Track> {

    public TopTracksAdapter(Context context, List<Track> tracks) {
        super(context, R.layout.toptracks_row, tracks);
    }

    private static class TopTracksHolder {
        TextView trackName;
        TextView albumName;
        ImageView albumImg;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Get Track from track Array
        Track track = getItem(position);

        View row = convertView;
        TopTracksHolder holder = null;

        //starting fresh as we do not have a row view yet
        if (row == null) {
            //Making a new view
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.toptracks_row, parent, false);
            holder = new TopTracksHolder();

            //Get reference to different view elements to be updated

            holder.trackName = (TextView) row.findViewById(R.id.trackName);
            holder.albumName = (TextView) row.findViewById(R.id.albumName);
            holder.albumImg = (ImageView) row.findViewById(R.id.albumImg);

            row.setTag(holder);
        } else {
            //Otherwise get the existing view
            holder = (TopTracksHolder) row.getTag();
        }

        String url;

        if (track.album.images.isEmpty()) {
            url = "http://i.imgur.com/jx8ihcj.png?1";
        } else {
            int i = track.album.images.size() - 1;
            url = track.album.images.get(i).url;
        }


        //Set the view to reflect the data to be displayed
        holder.trackName.setText(track.name);
        holder.albumName.setText(track.album.name);
        Picasso.with(getContext())
                .load(url)
                .resize(200, 200)
                .centerCrop()
                .into(holder.albumImg);

        return row;
    }
}
