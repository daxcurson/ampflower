package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.repository.PlaylistsRepository;

public class PlaylistsViewModel  extends ViewModel
{
    private PlaylistsRepository playlistsRepository;
    public PlaylistsViewModel()
    {
    }
    public void setPlaylistsRepository(PlaylistsRepository repository)
    {
        this.playlistsRepository=repository;
    }
    public LiveData<PagedList<Playlist>> getPlaylists()
    {
        if(playlistsRepository==null)
            return new MutableLiveData<>();
        return playlistsRepository.getPlaylists();
    }
    public LiveData<NetworkState> getNetworkState()
    {
        if(playlistsRepository==null)
            return new MutableLiveData<>();
        return playlistsRepository.getNetworkState();
    }
}
