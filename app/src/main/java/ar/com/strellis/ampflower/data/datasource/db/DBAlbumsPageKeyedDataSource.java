package ar.com.strellis.ampflower.data.datasource.db;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import java.util.List;

import ar.com.strellis.ampflower.data.dao.AlbumDao;
import ar.com.strellis.ampflower.data.model.Album;

public class DBAlbumsPageKeyedDataSource extends PageKeyedDataSource<String, Album> {

    public static final String TAG = DBAlbumsPageKeyedDataSource.class.getSimpleName();
    private final AlbumDao albumDao;
    public DBAlbumsPageKeyedDataSource(AlbumDao dao) {
        albumDao = dao;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull final LoadInitialCallback<String, Album> callback) {
        Log.i(TAG, "Loading Initial Range, Count " + params.requestedLoadSize);
        List<Album> albums = albumDao.listAllAlbums();
        if(albums.size() != 0) {
            callback.onResult(albums, "0", "1");
        }
    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, final @NonNull LoadCallback<String, Album> callback) {
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, Album> callback) {
    }
}
