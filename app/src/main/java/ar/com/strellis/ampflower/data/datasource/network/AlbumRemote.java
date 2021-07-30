package ar.com.strellis.ampflower.data.datasource.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.NetworkState;

public class AlbumRemote
{
    public static final int LOADING_PAGE_SIZE = 20;
    private static final int NUMBERS_OF_THREADS = 4;
    final private LiveData<PagedList<Album>> albumsPaged;
    final private LiveData<NetworkState> networkState;

    public AlbumRemote(NetAlbumsDataSourceFactory dataSourceFactory, PagedList.BoundaryCallback<Album> boundaryCallback){
        PagedList.Config pagedListConfig = (new PagedList.Config.Builder())
                .setPageSize(LOADING_PAGE_SIZE)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(LOADING_PAGE_SIZE)
                .setPageSize(LOADING_PAGE_SIZE)
                .build();
        networkState = Transformations.switchMap(dataSourceFactory.getNetworkStatus(),
                NetAlbumsPageKeyedDataSource::getNetworkState);
        Executor executor = Executors.newFixedThreadPool(NUMBERS_OF_THREADS);
        LivePagedListBuilder<String,Album> livePagedListBuilder = new LivePagedListBuilder<>(dataSourceFactory, pagedListConfig);
        albumsPaged = livePagedListBuilder.
                setFetchExecutor(executor).
                setBoundaryCallback(boundaryCallback).
                build();

    }
    public LiveData<PagedList<Album>> getAlbumsPaged(){
        return albumsPaged;
    }
    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }
}
