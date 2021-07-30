package ar.com.strellis.ampflower.data.datasource.db;

import androidx.paging.DataSource;

import ar.com.strellis.ampflower.data.dao.AlbumDao;
import ar.com.strellis.ampflower.data.model.Album;

public class DBAlbumsDataSourceFactory extends DataSource.Factory<String, Album> {

    private final DBAlbumsPageKeyedDataSource albumsPageKeyedDataSource;
    public DBAlbumsDataSourceFactory(AlbumDao dao) {
        albumsPageKeyedDataSource = new DBAlbumsPageKeyedDataSource(dao);
    }

    @Override
    public DataSource<String,Album> create() {
        return albumsPageKeyedDataSource;
    }

}
