package ar.com.strellis.ampflower.data.datasource.db;

import androidx.paging.DataSource;

import org.jetbrains.annotations.NotNull;

import ar.com.strellis.ampflower.data.dao.PlaylistDao;
import ar.com.strellis.ampflower.data.model.Playlist;

public class DBPlaylistsDataSourceFactory extends DataSource.Factory<String, Playlist> {

    private final DBPlaylistsPageKeyedDataSource playlistsPageKeyedDataSource;
    public DBPlaylistsDataSourceFactory(PlaylistDao dao) {
        playlistsPageKeyedDataSource = new DBPlaylistsPageKeyedDataSource(dao);
    }

    @NotNull
    @Override
    public DataSource<String,Playlist> create() {
        return playlistsPageKeyedDataSource;
    }

}