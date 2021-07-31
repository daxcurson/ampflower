package ar.com.strellis.ampflower.data.repository;

import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.datasource.network.AlbumRemoteMediator;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import kotlinx.coroutines.flow.Flow;

public class AlbumsRepositoryRx {
    private AmpacheService networkService;
    private AmpacheDatabase database;
    private final int pageSize=20;

    public AlbumsRepositoryRx(AmpacheService service,AmpacheDatabase database)
    {
        this.networkService=service;
        this.database=database;
    }
    public Flow<PagingData<Album>> getSearchResultStream(String query)
    {
        Pager<Integer, Album> pager = new Pager(
                new PagingConfig(/* pageSize = */ pageSize),
                null, // initialKey,
                new AlbumRemoteMediator(networkService,database),
                () -> database.albumDao().pagingSource(query));
        return pager.getFlow();
    }
}
