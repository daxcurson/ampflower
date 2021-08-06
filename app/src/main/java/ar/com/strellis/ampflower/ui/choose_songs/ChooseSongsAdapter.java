package ar.com.strellis.ampflower.ui.choose_songs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.SelectableSong;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;

public class ChooseSongsAdapter extends RecyclerView.Adapter<ChooseSongsViewHolder>
{
    private final SongsViewModel songsViewModel;
    public ChooseSongsAdapter(SongsViewModel songsViewModel)
    {
        this.songsViewModel=songsViewModel;
    }
    @NonNull
    @Override
    public ChooseSongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View songItemView=inflater.inflate(R.layout.list_item_song,parent,false);
        return new ChooseSongsViewHolder(songItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseSongsViewHolder holder, int position) {
        SelectableSong song= Objects.requireNonNull(songsViewModel.getSongsInView().getValue()).get(position);
        holder.getSongTitle().setText(song.getSong().getName());
        holder.getArtistName().setText(song.getSong().getArtist().getName());
        holder.getIsChecked().setChecked(song.isSelected());
    }

    public List<SelectableSong> getSongs()
    {
        return this.songsViewModel.getSongsInView().getValue();
    }

    public void submitList(AlbumWithSongs songs) {
        List<SelectableSong> songsToAdd = songs.getSongs().stream().map(song -> new SelectableSong(song, false)).collect(Collectors.toCollection(LinkedList::new));
        Objects.requireNonNull(this.songsViewModel.getSongsInView().getValue()).addAll(songsToAdd);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return Objects.requireNonNull(songsViewModel.getSongsInView().getValue()).size();
    }
}
