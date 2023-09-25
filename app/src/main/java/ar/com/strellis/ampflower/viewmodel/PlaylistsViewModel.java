package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import org.jetbrains.annotations.NotNull;

import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.repository.PlaylistsRepositoryRx;
import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;
@ExperimentalPagingApi
public class PlaylistsViewModel  extends ViewModel
{
    private PlaylistsRepositoryRx playlistsRepository;

    private final MutableLiveData<String> query;
    public PlaylistsViewModel()
    {
        this.query= new MutableLiveData<>();
    }
    public void setPlaylistsRepository(@NotNull PlaylistsRepositoryRx repository)
    {
        this.playlistsRepository=repository;
    }

    public LiveData<String> getQuery()
    {
        return this.query;
    }

    public void setQuery(String query)
    {
        this.query.setValue(query);
    }
    public Flowable<PagingData<Playlist>> getPlaylists()
    {
        // here is a problem where the repository is not initialized before
        // trying to obtain the playlist, so getPlaylists in invoked on a null object.
        Flowable<PagingData<Playlist>> newResult=playlistsRepository.getPlaylists();
        CoroutineScope coroutineScope= ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(newResult,coroutineScope);
        return newResult;
    }
    public LiveData<NetworkState> getNetworkState()
    {
        if(playlistsRepository==null)
            return new MutableLiveData<>();
        return playlistsRepository.getNetworkState();
    }
}
