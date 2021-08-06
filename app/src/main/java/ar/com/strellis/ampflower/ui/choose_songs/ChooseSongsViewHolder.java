package ar.com.strellis.ampflower.ui.choose_songs;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ar.com.strellis.ampflower.R;

public class ChooseSongsViewHolder extends RecyclerView.ViewHolder
{
    private final ImageView albumImage;
    private final TextView songTitle;
    private final TextView artistName;
    private final CheckBox isChecked;
    public ChooseSongsViewHolder(@NonNull View itemView) {
        super(itemView);
        albumImage=itemView.findViewById(R.id.list_item_song_album_art);
        songTitle=itemView.findViewById(R.id.list_item_song_title);
        artistName=itemView.findViewById(R.id.list_item_song_album_artist);
        isChecked=itemView.findViewById(R.id.list_item_song_is_selected_checkbox);
    }

    public CheckBox getIsChecked()
    {
        return isChecked;
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
