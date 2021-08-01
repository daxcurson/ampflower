package ar.com.strellis.ampflower.data.datasource.db;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import java.util.List;

import ar.com.strellis.ampflower.data.dao.ArtistDao;
import ar.com.strellis.ampflower.data.model.Artist;

public class DBArtistsPageKeyedDataSource extends PageKeyedDataSource<String, Artist> {
    public static final String TAG = DBArtistsPageKeyedDataSource.class.getSimpleName();
    private final ArtistDao artistDao;

    public DBArtistsPageKeyedDataSource(ArtistDao artistDao) {
        this.artistDao=artistDao;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull LoadInitialCallback<String, Artist> callback) {
        Log.i(TAG, "Loading Initial Range, Count " + params.requestedLoadSize);
        List<Artist> artists = artistDao.listAllArtists();
        if(artists.size() != 0) {
            callback.onResult(artists, "0", "1");
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, Artist> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, Artist> callback) {

    }
}
