package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava2.PagingRx;

import ar.com.strellis.ampflower.data.datasource.network.AlbumsPagingSourceRx;
import ar.com.strellis.ampflower.data.datasource.network.ArtistsPagingSourceRx;
import ar.com.strellis.ampflower.data.datasource.network.SongsPagingSourceRx;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.SearchType;
import ar.com.strellis.ampflower.data.model.Song;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class FavoritesViewModel extends ViewModel
{
    public Flowable<PagingData<Album>> pagedRandomAlbums;
    public Flowable<PagingData<Artist>> pagedTopRatedArtists;
    public Flowable<PagingData<Album>> pagedTrendingAlbums;
    public Flowable<PagingData<Song>> pagedRecentlyPlayedSongs;

    public FavoritesViewModel()
    {
    }
    public void init(AmpacheService service, LoginResponse loginResponse)
    {
        AlbumsPagingSourceRx randomAlbumsPagingSource=new AlbumsPagingSourceRx(service,loginResponse, SearchType.RANDOM);
        Pager<Integer,Album> pager=new Pager<>(
                new PagingConfig(AlbumsPagingSourceRx.PAGE_SIZE,
                        AlbumsPagingSourceRx.PAGE_SIZE
                        ),
                ()->randomAlbumsPagingSource
        );
        pagedRandomAlbums= PagingRx.getFlowable(pager);
        CoroutineScope coroutineScope= ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(pagedRandomAlbums,coroutineScope);

        ArtistsPagingSourceRx topRatedArtistsPagingSource=new ArtistsPagingSourceRx(service,loginResponse,SearchType.HIGHEST);
        Pager<Integer,Artist> pagerArtists=new Pager<>(
                new PagingConfig(ArtistsPagingSourceRx.PAGE_SIZE,
                        ArtistsPagingSourceRx.PAGE_SIZE
                ),
                ()->topRatedArtistsPagingSource
        );
        pagedTopRatedArtists=PagingRx.getFlowable(pagerArtists);
        CoroutineScope coroutineScopeArtists=ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(pagedTopRatedArtists,coroutineScopeArtists);

        AlbumsPagingSourceRx trendingAlbumsPagingSource=new AlbumsPagingSourceRx(service,loginResponse,SearchType.FREQUENT);
        Pager<Integer,Album> pagerTrendingAlbums=new Pager<>(
                new PagingConfig(AlbumsPagingSourceRx.PAGE_SIZE,
                        AlbumsPagingSourceRx.PAGE_SIZE
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
