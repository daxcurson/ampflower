package ar.com.strellis.ampflower.data.datasource.db;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import java.util.List;

import ar.com.strellis.ampflower.data.dao.PlaylistDao;
import ar.com.strellis.ampflower.data.model.Playlist;

public class DBPlaylistsPageKeyedDataSource extends PageKeyedDataSource<String, Playlist> {
    public static final String TAG = DBPlaylistsPageKeyedDataSource.class.getSimpleName();
    private final PlaylistDao playlistDao;

    public DBPlaylistsPageKeyedDataSource(PlaylistDao playlistDao) {
        this.playlistDao=playlistDao;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull LoadInitialCallback<String, Playlist> callback) {
        Log.i(TAG, "Loading Initial Range, Count " + params.requestedLoadSize);
        List<Playlist> playlists = playlistDao.listAllPlaylists();
        if(playlists.size() != 0) {
            callback.onResult(playlists, "0", "1");
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, Playlist> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, Playlist> callback) {

    }
}
