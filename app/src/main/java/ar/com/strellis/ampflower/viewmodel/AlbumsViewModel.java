package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;
import androidx.paging.PagingData;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.repository.AlbumsRepository;
import ar.com.strellis.ampflower.data.repository.AlbumsRepositoryRx;
import kotlinx.coroutines.flow.Flow;

public class AlbumsViewModel extends ViewModel
{
    //private AlbumsRepository albumsRepository;
    private AlbumsRepositoryRx albumsRepository;
    private Flow<PagingData<Album>> currentSearchResult=null;
    private String currentQueryValue;
    public AlbumsViewModel()
    {
    }
    public void setAlbumsRepository(AlbumsRepositoryRx repository)
    {
        this.albumsRepository=repository;
    }
    /*public LiveData<PagedList<Album>> getAlbums()
    {
        return albumsRepository.getAlbums();
    }
    public LiveData<NetworkState> getNetworkState()
    {
        return albumsRepository.getNetworkState();
    }*/
    public Flow<PagingData<Album>> searchAlbums(String queryString)
    {
        Flow<PagingData<Album>> lastResult=currentSearchResult;
        if(queryString==currentQueryValue && lastResult!=null)
            return lastResult;
        currentQueryValue=queryString;
        Flow<PagingData<Album>> newResult=albumsRepository.getSearchResultStream(queryString);
        currentSearchResult=newResult;
        return newResult;
    }
}
