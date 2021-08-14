package ar.com.strellis.ampflower.ui.view_playlist;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ar.com.strellis.ampflower.R;

public class ViewPlaylistViewHolder extends RecyclerView.ViewHolder
{
    private final ImageView albumImage;
    private final TextView songTitle;
    private final TextView artistName;
    private final ImageButton draggable;
    public ViewPlaylistViewHolder(@NonNull View itemView) {
        super(itemView);
        albumImage=itemView.findViewById(R.id.list_item_song_playlist_album_art);
        songTitle=itemView.findViewById(R.id.list_item_song_playlist_title);
        artistName=itemView.findViewById(R.id.list_item_song_playlist_album_artist);
        draggable=itemView.findViewById(R.id.list_item_song_playlist_draggable);
    }

    public ImageButton getDraggable()
    {
        return draggable;
    }
    public ImageView getAlbumImage() {
        return albumImage;
    }

    public TextView getSongTitle() {
        return songTitle;
    }

    public TextView getArtistName() {
        return artistName;
    }
}
