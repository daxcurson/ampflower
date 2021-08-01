package ar.com.strellis.ampflower.ui.home.artists;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ar.com.strellis.ampflower.R;

public class ArtistViewHolder extends RecyclerView.ViewHolder
{
    private final ImageView artistFace;
    private final TextView artistName;
    private final TextView songsQuantity;
    public ArtistViewHolder(@NonNull View itemView) {
        super(itemView);
        songsQuantity= itemView.findViewById(R.id.list_item_artist_songs);
        artistName=itemView.findViewById(R.id.list_item_artist_name);
        artistFace=itemView.findViewById(R.id.list_item_artist_face);
    }

    public ImageView getArtistFace() {
        return artistFace;
    }

    public TextView getArtistName() {
        return artistName;
    }

    public TextView getSongsQuantity() {
        return songsQuantity;
    }
}
