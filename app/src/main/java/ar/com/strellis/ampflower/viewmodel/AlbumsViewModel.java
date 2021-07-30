package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.repository.AlbumsRepository;

public class AlbumsViewModel extends ViewModel
{
    private AlbumsRepository albumsRepository;
    public AlbumsViewModel()
    {
    }
    public void setAlbumsRepository(AlbumsRepository repository)
    {
        this.albumsRepository=repository;
    }
    public LiveData<PagedList<Album>> getAlbums()
    {
        return albumsRepository.getAlbums();
    }
    public LiveData<NetworkState> getNetworkState()
    {
        return albumsRepository.getNetworkState();
    }
}
