package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.repository.PlaylistsRepositoryRx;
import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class PlaylistsViewModel  extends ViewModel
{
    private PlaylistsRepositoryRx playlistsRepository;
    public PlaylistsViewModel()
    {
    }
    public void setPlaylistsRepository(PlaylistsRepositoryRx repository)
    {
        this.playlistsRepository=repository;
    }
    public Flowable<PagingData<Playlist>> getPlaylists()
    {
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
