package ar.com.strellis.ampflower.ui.view_playlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.EntityWithSongs;
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
        View songItemView=inflater.inflate(R.layout.list_item_song_in_playlist,parent,false);
        return new ViewPlaylistViewHolder(songItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPlaylistViewHolder holder, int position) {
        List<SelectableSong> songs=songsViewModel.getSongsInView().getValue();
        if(songs!=null)
        {
            SelectableSong song = Objects.requireNonNull(songsViewModel.getCurrentPlaylist().getValue()).get(position);
            holder.getSongTitle().setText(position+" "+song.getSong().getName());
            holder.getArtistName().setText(song.getSong().getArtist().getName());
            Picasso.get().load(song.getSong().getArt()).into(holder.getAlbumImage());
            // Now, if the item is being played, the item must be in bold.
            /*if(songsViewModel.getCurrentItemInPlaylist().getValue()!=null &&
                    songsViewModel.getCurrentItemInPlaylist().getValue()>=0 &&
                    songsViewModel.getCurrentItemInPlaylist().getValue()==position)
            {
                Log.d("ViewPlaylistAdapter","The song at position "+position+" needs to be bolded");
                holder.getSongTitle().setTypeface(holder.getSongTitle().getTypeface(), Typeface.BOLD_ITALIC);
            }
            else
            {
                // Plain state it is.
                holder.getSongTitle().setTypeface(holder.getSongTitle().getTypeface(), Typeface.NORMAL);
            }*/
        }
    }

    public List<SelectableSong> getSongs()
    {
        return this.songsViewModel.getSongsInView().getValue();
    }

    /**
     * Receives songs to display in the Choose Songs view. Clears the currently-displayed list
     * and adds the items received.
     * @param songs The entity, e.g. the album, and its list of songs.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void submitList(EntityWithSongs songs) {
        List<SelectableSong> songsToAdd = songs.getSongs().stream()
                .map(song -> new SelectableSong(song, false))
                .collect(Collectors.toCollection(LinkedList::new));
        // When we receive new songs, we'll delete the ones that are already present.
        Objects.requireNonNull(this.songsViewModel.getSongsInView().getValue()).clear();
        Objects.requireNonNull(this.songsViewModel.getSongsInView().getValue()).addAll(songsToAdd);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        List<SelectableSong> songs=songsViewModel.getSongsInView().getValue();
        if(songs!=null)
            return Objects.requireNonNull(songs).size();
        else
            return 0;
    }

    public static DiffUtil.ItemCallback<SelectableSong> callback_diff = new DiffUtil.ItemCallback<SelectableSong>() {
        @Override
        public boolean areItemsTheSame(@NonNull SelectableSong oldItem, @NonNull SelectableSong newItem) {
            Log.d("DEBUG", "Callback comparing item " + oldItem.getSong().getId() + " with item " + newItem.getSong().getId());
            return oldItem.getSong().getId()==newItem.getSong().getId() && oldItem.getSong().getName().equals(newItem.getSong().getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull SelectableSong oldItem, @NonNull SelectableSong newItem) {
            Log.d("DEBUG", "Callback comparing content of item " + oldItem.getSong().getId() + " with item " + newItem.getSong().getId());
            return oldItem.getSong().equals(newItem.getSong());
        }

    };
}
