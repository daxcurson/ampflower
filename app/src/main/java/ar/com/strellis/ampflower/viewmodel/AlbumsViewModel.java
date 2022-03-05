package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.repository.AlbumsRepositoryRx;
import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class AlbumsViewModel extends ViewModel
{
    private AlbumsRepositoryRx albumsRepository;
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
    public void setAlbumsRepository(AlbumsRepositoryRx repository)
    {
        this.albumsRepository=repository;
    }
    public Flowable<PagingData<Album>> getAlbums()
    {
        Flowable<PagingData<Album>> newResult=albumsRepository.getAlbums();
        CoroutineScope coroutineScope= ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(newResult,coroutineScope);
        return newResult;
    }
    public LiveData<NetworkState> getNetworkState()
    {
        if(albumsRepository==null)
            return new MutableLiveData<>();
        return albumsRepository.getNetworkState();
    }
}
