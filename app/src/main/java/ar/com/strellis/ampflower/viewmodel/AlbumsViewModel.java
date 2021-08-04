package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.repository.AlbumsRepository;

public class AlbumsViewModel extends ViewModel
{
    private AlbumsRepository albumsRepository;
    private final MutableLiveData<String> query;
    public AlbumsViewModel()
    {
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
    public void setAlbumsRepository(AlbumsRepository repository)
    {
        this.albumsRepository=repository;
    }
    public LiveData<PagedList<Album>> getAlbums()
    {
        if(albumsRepository==null)
            return new MutableLiveData<>();
        return albumsRepository.getAlbums();
    }
    public LiveData<NetworkState> getNetworkState()
    {
        if(albumsRepository==null)
            return new MutableLiveData<>();
        return albumsRepository.getNetworkState();
    }
}
