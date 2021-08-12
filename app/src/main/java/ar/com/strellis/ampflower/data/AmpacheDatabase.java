package ar.com.strellis.ampflower.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ar.com.strellis.ampflower.data.dao.AlbumDao;
import ar.com.strellis.ampflower.data.dao.AlbumSongDao;
import ar.com.strellis.ampflower.data.dao.ArtistDao;
import ar.com.strellis.ampflower.data.dao.ArtistSongDao;
import ar.com.strellis.ampflower.data.dao.PlaylistDao;
import ar.com.strellis.ampflower.data.dao.PlaylistSongDao;
import ar.com.strellis.ampflower.data.dao.SongDao;
import ar.com.strellis.ampflower.data.datasource.db.DBAlbumsDataSourceFactory;
import ar.com.strellis.ampflower.data.datasource.db.DBArtistsDataSourceFactory;
import ar.com.strellis.ampflower.data.datasource.db.DBPlaylistsDataSourceFactory;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumSong;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.ArtistSong;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.model.PlaylistSong;
import ar.com.strellis.ampflower.data.model.Song;

@Database(entities = {Song.class, Album.class, Artist.class, Playlist.class, AlbumSong.class, ArtistSong.class, PlaylistSong.class}, version = 13,exportSchema = false)
@TypeConverters({AmpacheDataConverters.class})
public abstract class AmpacheDatabase extends RoomDatabase {
    public abstract AlbumDao albumDao();
    public abstract ArtistDao artistDao();
    public abstract PlaylistDao playlistDao();
    public abstract SongDao songDao();
    public abstract AlbumSongDao albumSongDao();
    public abstract ArtistSongDao artistSongDao();
    public abstract PlaylistSongDao playlistSongDao();
    private static final Object sLock = new Object();

    private static volatile AmpacheDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    private LiveData<PagedList<Album>> albumsPaged;
    private LiveData<PagedList<Artist>> artistsPaged;
    private LiveData<PagedList<Playlist>> playlistsPaged;

    public static AmpacheDatabase getDatabase(final Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                synchronized (AmpacheDatabase.class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                AmpacheDatabase.class, "ampache-data")
                                .fallbackToDestructiveMigration()
                                .build();
                        INSTANCE.init();
                    }
                }
            }
            return INSTANCE;
        }
    }
    private void init()
    {
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        PagedList.Config pagedListConfig = (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Integer.MAX_VALUE).setPageSize(Integer.MAX_VALUE).build();
        DBAlbumsDataSourceFactory dataSourceFactoryAlbums = new DBAlbumsDataSourceFactory(albumDao());
        DBArtistsDataSourceFactory dataSourceFactoryArtists=new DBArtistsDataSourceFactory(artistDao());
        DBPlaylistsDataSourceFactory dataSourceFactoryPlaylists=new DBPlaylistsDataSourceFactory(playlistDao());
        LivePagedListBuilder<String,Album> livePagedListBuilderAlbums = new LivePagedListBuilder<>(dataSourceFactoryAlbums, pagedListConfig);
        LivePagedListBuilder<String,Artist> livePagedListBuilderArtists=new LivePagedListBuilder<>(dataSourceFactoryArtists,pagedListConfig);
        LivePagedListBuilder<String,Playlist> livePagedListBuilderPlaylists=new LivePagedListBuilder<>(dataSourceFactoryPlaylists,pagedListConfig);
        albumsPaged = livePagedListBuilderAlbums.setFetchExecutor(executor).build();
        artistsPaged=livePagedListBuilderArtists.setFetchExecutor(executor).build();
        playlistsPaged=livePagedListBuilderPlaylists.setFetchExecutor(executor).build();
    }
    public LiveData<PagedList<Album>> getAlbums()
    {
        return albumsPaged;
    }
    public LiveData<PagedList<Artist>> getArtists()
    {
        return artistsPaged;
    }
    public LiveData<PagedList<Playlist>> getPlaylists()
    {
        return playlistsPaged;
    }
}
