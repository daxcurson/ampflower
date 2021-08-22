package ar.com.strellis.ampflower.ui.view_playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.SelectableSong;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;

public class ViewPlaylistAdapter extends RecyclerView.Adapter<ViewPlaylistViewHolder>
{
    private final SongsViewModel songsViewModel;
    public ViewPlaylistAdapter(SongsViewModel songsViewModel)
    {
        this.songsViewModel=songsViewModel;
    }
    @NonNull
    @Override
    public ViewPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        if(viewType==R.layout.list_item_song_in_playlist)
        {
            View songItemView = inflater.inflate(R.layout.list_item_song_in_playlist, parent, false);
            return new ViewPlaylistViewHolder(songItemView);
        }
        if(viewType==R.layout.list_item_selected_song_in_playlist)
        {
            View songItemView2=inflater.inflate(R.layout.list_item_selected_song_in_playlist,parent,false);
            return new ViewPlaylistViewHolder(songItemView2);
        }
        View songItemView = inflater.inflate(R.layout.list_item_song_in_playlist, parent, false);
        return new ViewPlaylistViewHolder(songItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPlaylistViewHolder holder, int position) {
        List<SelectableSong> songs=songsViewModel.getSongsInView().getValue();
        if(songs!=null)
        {
            SelectableSong song = Objects.requireNonNull(songsViewModel.getCurrentPlaylist().getValue()).get(position);
            holder.getSongTitle().setText(song.getSong().getName());
            holder.getArtistName().setText(song.getSong().getArtist().getName());
            Picasso.get().load(song.getSong().getArt()).into(holder.getAlbumImage());
        }
    }

    /**
     * Chooses which kind of item row to show, according to the current item being played.
     * @param position which position in the list we are going to show
     * @return a layout with the bolded text, if the item being played matches the position.
     */
    @Override
    public int getItemViewType(int position) {
        int currentSelectedItem=Objects.requireNonNull(songsViewModel.getCurrentItemInPlaylist().getValue());
        if(currentSelectedItem==position)
            return R.layout.list_item_selected_song_in_playlist;
        return R.layout.list_item_song_in_playlist;
    }

    public List<SelectableSong> getSongs()
    {
        return this.songsViewModel.getCurrentPlaylist().getValue();
    }

    @Override
    public int getItemCount() {
        List<SelectableSong> songs=songsViewModel.getCurrentPlaylist().getValue();
        if(songs!=null)
            return Objects.requireNonNull(songs).size();
        else
            return 0;
    }
}
