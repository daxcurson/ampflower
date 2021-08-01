package ar.com.strellis.ampflower.data.datasource.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.model.Playlist;

public class PlaylistRemote {
    public static final int LOADING_PAGE_SIZE = 20;
    private static final int NUMBERS_OF_THREADS = 4;
    final private LiveData<PagedList<Playlist>> playlistsPaged;
    final private LiveData<NetworkState> networkState;

    public PlaylistRemote(NetPlaylistsDataSourceFactory dataSourceFactory, PagedList.BoundaryCallback<Playlist> boundaryCallback){
        PagedList.Config pagedListConfig = (new PagedList.Config.Builder())
                .setPageSize(LOADING_PAGE_SIZE)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(LOADING_PAGE_SIZE)
                .setPageSize(LOADING_PAGE_SIZE)
                .build();
        networkState = Transformations.switchMap(dataSourceFactory.getNetworkStatus(),
                NetPlaylistsPageKeyedDataSource::getNetworkState);
        Executor executor = Executors.newFixedThreadPool(NUMBERS_OF_THREADS);
        LivePagedListBuilder<String,Playlist> livePagedListBuilder = new LivePagedListBuilder<String,Playlist>(dataSourceFactory, pagedListConfig);
        playlistsPaged = livePagedListBuilder.
                setFetchExecutor(executor).
                setBoundaryCallback(boundaryCallback).
                build();

    }
    public LiveData<PagedList<Playlist>> getPlaylistsPaged(){
        return playlistsPaged;
    }
    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }
}
