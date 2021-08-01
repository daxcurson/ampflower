package ar.com.strellis.ampflower.ui.home.playlists;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ar.com.strellis.ampflower.R;

public class PlaylistViewHolder  extends RecyclerView.ViewHolder
{
    private final ImageView playlistImage;
    private final TextView playlistName;
    private final TextView songsQuantity;
    public PlaylistViewHolder(@NonNull View itemView) {
        super(itemView);
        songsQuantity= itemView.findViewById(R.id.list_item_playlist_songs);
        playlistName=itemView.findViewById(R.id.list_item_playlist_name);
        playlistImage=itemView.findViewById(R.id.list_item_playlist_image);
    }

    public ImageView getPlaylistImage() {
        return playlistImage;
    }

    public TextView getPlaylistName() {
        return playlistName;
    }

    public TextView getSongsQuantity() {
        return songsQuantity;
    }
}
