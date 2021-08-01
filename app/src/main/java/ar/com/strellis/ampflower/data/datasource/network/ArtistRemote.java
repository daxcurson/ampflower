package ar.com.strellis.ampflower.data.datasource.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.NetworkState;

public class ArtistRemote {
    public static final int LOADING_PAGE_SIZE = 20;
    private static final int NUMBERS_OF_THREADS = 4;
    final private LiveData<PagedList<Artist>> artistsPaged;
    final private LiveData<NetworkState> networkState;

    public ArtistRemote(NetArtistsDataSourceFactory dataSourceFactory, PagedList.BoundaryCallback<Artist> boundaryCallback){
        PagedList.Config pagedListConfig = (new PagedList.Config.Builder())
                .setPageSize(LOADING_PAGE_SIZE)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(LOADING_PAGE_SIZE)
                .setPageSize(LOADING_PAGE_SIZE)
                .build();
        networkState = Transformations.switchMap(dataSourceFactory.getNetworkStatus(),
                NetArtistsPageKeyedDataSource::getNetworkState);
        Executor executor = Executors.newFixedThreadPool(NUMBERS_OF_THREADS);
        LivePagedListBuilder<String,Artist> livePagedListBuilder = new LivePagedListBuilder<>(dataSourceFactory, pagedListConfig);
        artistsPaged = livePagedListBuilder.
                setFetchExecutor(executor).
                setBoundaryCallback(boundaryCallback).
                build();

    }
    public LiveData<PagedList<Artist>> getArtistsPaged(){
        return artistsPaged;
    }
    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }
}
