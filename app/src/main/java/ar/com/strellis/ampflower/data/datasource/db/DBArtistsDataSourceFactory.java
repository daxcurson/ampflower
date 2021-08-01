package ar.com.strellis.ampflower.data.datasource.db;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

import ar.com.strellis.ampflower.data.dao.ArtistDao;
import ar.com.strellis.ampflower.data.model.Artist;

public class DBArtistsDataSourceFactory extends DataSource.Factory<String, Artist> {
    private final DBArtistsPageKeyedDataSource artistsPageKeyedDataSource;
    public DBArtistsDataSourceFactory(ArtistDao artistDao) {
        artistsPageKeyedDataSource=new DBArtistsPageKeyedDataSource(artistDao);
    }

    @NonNull
    @Override
    public DataSource<String, Artist> create() {
        return artistsPageKeyedDataSource;
    }
}
