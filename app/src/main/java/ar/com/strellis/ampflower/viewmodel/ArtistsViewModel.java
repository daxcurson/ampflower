package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.repository.ArtistsRepository;

public class ArtistsViewModel extends ViewModel
{
    private ArtistsRepository artistsRepository;
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
    public LiveData<PagedList<Artist>> getArtists()
    {
        if(this.artistsRepository==null)
            return new MutableLiveData<>();
        return this.artistsRepository.getArtists();
    }
    public void setArtistsRepository(ArtistsRepository artistsRepository)
    {
        this.artistsRepository=artistsRepository;
    }
    public LiveData<NetworkState> getNetworkState() {
        if(this.artistsRepository==null)
            return new MutableLiveData<>();
        return this.artistsRepository.getNetworkState();
    }
}
