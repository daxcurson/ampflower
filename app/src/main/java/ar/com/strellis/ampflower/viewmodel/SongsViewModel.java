package ar.com.strellis.ampflower.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ar.com.strellis.ampflower.data.model.Searchable;
import ar.com.strellis.ampflower.data.model.SelectableSong;
import ar.com.strellis.ampflower.data.model.Song;
import ar.com.strellis.ampflower.data.repository.SongsRepository;

public class SongsViewModel extends ViewModel
{
    private SongsRepository songsRepository;
    private final MutableLiveData<Searchable> searchableItem;
    private final MutableLiveData<List<SelectableSong>> songsInView;
    private final MutableLiveData<List<MediaItem>> currentPlaylist;
    private final MutableLiveData<String> query;
    public SongsViewModel()
    {
        super();
        searchableItem =new MutableLiveData<>();
        currentPlaylist=new MutableLiveData<>();
        songsInView =new MutableLiveData<>();
        query=new MutableLiveData<>();
    }
    public LiveData<String> getQuery()
    {
        return query;
    }
    public void setQuery(String query)
    {
        this.query.setValue(query);
    }
    public void setSongsRepository(SongsRepository s)
    {
        this.songsRepository=s;
    }
    public SongsRepository getSongsRepository()
    {
        return this.songsRepository;
    }
    public void setSearchableItem(Searchable songs)
    {
        Log.d("SongsViewModel","This is setSearchableItem");
        searchableItem.setValue(songs);
    }
    public LiveData<Searchable> getSearchableItem()
    {
        Log.d("SongsViewModel","This is getSearchableItem");
        return searchableItem;
    }/*
    public LiveData<List<AlbumWithSongs>> getSongsByAlbum()
    {
        // Obtain the id from selectedSongs, that's the entity
        // that contains the songs that I'm asked to display
        int album_id= Objects.requireNonNull(searchableItem.getValue()).getId();
        Log.d("SongsViewModel","Getting songs from the Repo!");
        return songsRepository.getSongsByAlbum(album_id);
    }*/
    public LiveData<List<MediaItem>> getCurrentPlaylist()
    {
        return currentPlaylist;
    }
    public MutableLiveData<List<SelectableSong>> getSongsInView()
    {
        return songsInView;
    }
    public void setSongsInView(List<SelectableSong> songs)
    {
        this.songsInView.setValue(songs);
    }
    public void setCurrentPlaylist(List<MediaItem> newPlaylist)
    {
        this.currentPlaylist.setValue(newPlaylist);
    }
    public List<MediaItem> convertSongsToMedia(List<Song> songs)
    {
        // The ExoPlayer accepts MediaItems, and I have songs. I'll create a map() to convert between formats.
        return songs.stream().map(song ->
                {
                    MediaMetadata metadata=new MediaMetadata.Builder()
                            .setTitle(song.getTitle())
                            .setArtist(song.getArtist().getName())
                            .build();
                    return new MediaItem.Builder().setUri(song.getUrl())
                            .setMediaMetadata(metadata)
                            .build();
                }
        ).collect(Collectors.toList());
    }
    public List<Song> getSelectedSongs() {
        return Objects.requireNonNull(songsInView.getValue()).stream()
                .filter(SelectableSong::isSelected)
                .map(SelectableSong::getSong)
                .collect(Collectors.toList());
    }
}
