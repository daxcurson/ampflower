package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import ar.com.strellis.ampflower.data.datasource.network.AlbumStatsPagingSourceRx;
import ar.com.strellis.ampflower.data.datasource.network.ArtistsPagingSourceRx;
import ar.com.strellis.ampflower.data.datasource.network.SongsPagingSourceRx;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.SearchType;
import ar.com.strellis.ampflower.data.model.Song;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class FavoritesViewModel extends ViewModel
{
    public Flowable<PagingData<Album>> pagedRandomAlbums;
    public MutableLiveData<Boolean> loadingRandomAlbums;
    public Flowable<PagingData<Artist>> pagedTopRatedArtists;
    public MutableLiveData<Boolean> loadingTopRatedArtists;
    public Flowable<PagingData<Album>> pagedTrendingAlbums;
    public MutableLiveData<Boolean> loadingTrendingAlbums;
    public Flowable<PagingData<Song>> pagedRecentlyPlayedSongs;

    public FavoritesViewModel()
    {
        loadingRandomAlbums=new MutableLiveData<>();
        loadingTopRatedArtists=new MutableLiveData<>();
        loadingTrendingAlbums=new MutableLiveData<>();
    }
    public void init(AmpacheService service, LoginResponse loginResponse)
    {
        AlbumStatsPagingSourceRx randomAlbumsPagingSource=new AlbumStatsPagingSourceRx(service,loginResponse, SearchType.RANDOM,this.loadingRandomAlbums);
        Pager<Integer,Album> pager=new Pager<>(
                new PagingConfig(AlbumStatsPagingSourceRx.PAGE_SIZE,
                        AlbumStatsPagingSourceRx.PAGE_SIZE
                        ),
                ()->randomAlbumsPagingSource
        );
        pagedRandomAlbums= PagingRx.getFlowable(pager);
        CoroutineScope coroutineScope= ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(pagedRandomAlbums,coroutineScope);
        loadingRandomAlbums.setValue(false);

        ArtistsPagingSourceRx topRatedArtistsPagingSource=new ArtistsPagingSourceRx(service,loginResponse,SearchType.HIGHEST,this.loadingTopRatedArtists);
        Pager<Integer,Artist> pagerArtists=new Pager<>(
                new PagingConfig(ArtistsPagingSourceRx.PAGE_SIZE,
                        ArtistsPagingSourceRx.PAGE_SIZE
                ),
                ()->topRatedArtistsPagingSource
        );
        pagedTopRatedArtists=PagingRx.getFlowable(pagerArtists);
        CoroutineScope coroutineScopeArtists=ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(pagedTopRatedArtists,coroutineScopeArtists);
        loadingTopRatedArtists.setValue(false);

        AlbumStatsPagingSourceRx trendingAlbumsPagingSource=new AlbumStatsPagingSourceRx(service,loginResponse,SearchType.FREQUENT,this.loadingTrendingAlbums);
        Pager<Integer,Album> pagerTrendingAlbums=new Pager<>(
                new PagingConfig(AlbumStatsPagingSourceRx.PAGE_SIZE,
                        AlbumStatsPagingSourceRx.PAGE_SIZE
                ),
                ()->trendingAlbumsPagingSource
        );
        pagedTrendingAlbums=PagingRx.getFlowable(pagerTrendingAlbums);
        CoroutineScope coroutineScopeTrendingAlbums=ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(pagedTrendingAlbums,coroutineScopeTrendingAlbums);

        SongsPagingSourceRx recentlyPlayedSongsPagingSource=new SongsPagingSourceRx(service,loginResponse,SearchType.RECENT);
        Pager<Integer, Song> pagerRecentlyPlayedSongs=new Pager<>(
                new PagingConfig(SongsPagingSourceRx.PAGE_SIZE,
                        SongsPagingSourceRx.PAGE_SIZE
                ),
                ()->recentlyPlayedSongsPagingSource
        );
        pagedRecentlyPlayedSongs=PagingRx.getFlowable(pagerRecentlyPlayedSongs);
        CoroutineScope coroutineScopeRecentlyPlayedSongs=ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(pagedRecentlyPlayedSongs,coroutineScopeRecentlyPlayedSongs);
    }
}
