package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.repository.ArtistsRepositoryRx;
import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class ArtistsViewModel extends ViewModel
{
    private ArtistsRepositoryRx artistsRepository;
    private final MutableLiveData<String> query;
    public ArtistsViewModel()
    {
        this.query= new MutableLiveData<>();
    }
    public LiveData<String> getQuery()
    {
        return this.query;
    }
    public void setQuery(String query)
    {
        this.query.setValue(query);
    }
    public Flowable<PagingData<Artist>> getArtists()
    {
        Flowable<PagingData<Artist>> newResult=artistsRepository.getArtists();
        CoroutineScope coroutineScope= ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(newResult,coroutineScope);
        return newResult;
    }
    public void setArtistsRepository(ArtistsRepositoryRx artistsRepository)
    {
        this.artistsRepository=artistsRepository;
    }
    public ArtistsRepositoryRx getArtistsRepository()
    {
        return this.artistsRepository;
    }
    public LiveData<NetworkState> getNetworkState() {
        if(this.artistsRepository==null)
            return new MutableLiveData<>();
        return this.artistsRepository.getNetworkState();
    }
}
