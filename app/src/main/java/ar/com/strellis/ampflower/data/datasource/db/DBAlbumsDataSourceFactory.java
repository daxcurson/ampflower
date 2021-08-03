package ar.com.strellis.ampflower.data.datasource.db;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import ar.com.strellis.ampflower.data.dao.AlbumDao;
import ar.com.strellis.ampflower.data.model.Album;

public class DBAlbumsDataSourceFactory extends DataSource.Factory<String, Album> {

    private final DBAlbumsPageKeyedDataSource albumsPageKeyedDataSource;
    private LiveData<String> query;
    public DBAlbumsDataSourceFactory(AlbumDao dao) {
        query=new MutableLiveData<>();
        albumsPageKeyedDataSource = new DBAlbumsPageKeyedDataSource(dao,query);
    }

    @NonNull
    @Override
    public DataSource<String,Album> create() {
        return albumsPageKeyedDataSource;
    }
    public void setQuery(LiveData<String> query)
    {
        this.query=query;
        this.albumsPageKeyedDataSource.setQuery(query);
    }
}
