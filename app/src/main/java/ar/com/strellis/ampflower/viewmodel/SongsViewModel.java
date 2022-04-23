package ar.com.strellis.ampflower.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.Searchable;
import ar.com.strellis.ampflower.data.model.SelectableSong;
import ar.com.strellis.ampflower.data.model.Song;
import ar.com.strellis.ampflower.data.repository.SongsRepository;

public class SongsViewModel extends ViewModel
{
    private SongsRepository songsRepository;
    private final MutableLiveData<Searchable<?>> searchableItem;
    private final MutableLiveData<List<SelectableSong>> songsInView;
    private final MutableLiveData<List<SelectableSong>> currentPlaylist;
    private int oldItemInPlaylist;
    private final MutableLiveData<Integer> currentItemInPlaylist;
    private final MutableLiveData<String> query;
    public SongsViewModel()
    {
        super();
        searchableItem =new MutableLiveData<>();
        currentPlaylist=new MutableLiveData<>();
        currentItemInPlaylist=new MutableLiveData<>();
        songsInView =new MutableLiveData<>();
        query=new MutableLiveData<>();
        oldItemInPlaylist=0;
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
    public void setSearchableItem(Searchable<?> songs)
    {
        Log.d("SongsViewModel","This is setSearchableItem");
        searchableItem.setValue(songs);
    }
    public LiveData<Searchable<?>> getSearchableItem()
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
    public LiveData<List<SelectableSong>> getCurrentPlaylist()
    {
        return currentPlaylist;
    }
    public LiveData<Integer> getCurrentItemInPlaylist()
    {
        return currentItemInPlaylist;
    }
    public void setCurrentItemInPlaylist(int item)
    {
        if(currentItemInPlaylist.getValue()!=null)
        {
            oldItemInPlaylist = currentItemInPlaylist.getValue();
            Log.d("SongsViewModel","Old value "+oldItemInPlaylist);
        }
        Log.d("SongsViewModel","New value: "+item);
        currentItemInPlaylist.setValue(item);
    }
    public void setOldItemInPlaylist(int oldItemInPlaylist)
    {
        this.oldItemInPlaylist=oldItemInPlaylist;
    }
    public int getOldItemInPlaylist()
    {
        return oldItemInPlaylist;
    }
    public MutableLiveData<List<SelectableSong>> getSongsInView()
    {
        return songsInView;
    }
    public void setSongsInView(List<SelectableSong> songs)
    {
        this.songsInView.setValue(songs);
    }
    public void setCurrentPlaylist(List<SelectableSong> newPlaylist)
    {
        this.currentPlaylist.setValue(newPlaylist);
    }
    public List<SelectableSong> getSelectedSongsIntoPlaylist()
    {
        return Objects.requireNonNull(songsInView.getValue()).stream()
                .filter(SelectableSong::isSelected)
                .collect(Collectors.toList());
    }
    public List<Song> getSelectedSongs() {
        return Objects.requireNonNull(songsInView.getValue()).stream()
                .filter(SelectableSong::isSelected)
                .map(SelectableSong::getSong)
                .collect(Collectors.toList());
    }
    public LoginResponse getLoginResponse()
    {
        return songsRepository.getLoginResponse();
    }

}
