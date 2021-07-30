package ar.com.strellis.ampflower.ui.home.albums;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ar.com.strellis.ampflower.R;

public class AlbumViewHolder extends RecyclerView.ViewHolder
{
    private final ImageView albumArt;
    private final TextView albumTitle;
    private final TextView artistName;
    public AlbumViewHolder(@NonNull View itemView) {
        super(itemView);
        albumTitle= itemView.findViewById(R.id.list_item_album_title);
        artistName=itemView.findViewById(R.id.list_item_album_artist);
        albumArt=itemView.findViewById(R.id.list_item_album_art);
    }

    public ImageView getAlbumArt() {
        return albumArt;
    }

    public TextView getAlbumTitle() {
        return albumTitle;
    }

    public TextView getArtistName() {
        return artistName;
    }
}
