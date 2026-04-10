package ar.com.strellis.ampflower2.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ar.com.strellis.ampflower2.data.dao.AlbumDao;
import ar.com.strellis.ampflower2.data.dao.AlbumRemoteKeyDao;
import ar.com.strellis.ampflower2.data.dao.AlbumSongDao;
import ar.com.strellis.ampflower2.data.dao.ArtistDao;
import ar.com.strellis.ampflower2.data.dao.ArtistRemoteKeyDao;
import ar.com.strellis.ampflower2.data.dao.ArtistSongDao;
import ar.com.strellis.ampflower2.data.dao.PlaylistDao;
import ar.com.strellis.ampflower2.data.dao.PlaylistRemoteKeyDao;
import ar.com.strellis.ampflower2.data.dao.PlaylistSongDao;
import ar.com.strellis.ampflower2.data.dao.SongDao;
import ar.com.strellis.ampflower2.data.model.Album;
import ar.com.strellis.ampflower2.data.model.AlbumRemoteKey;
import ar.com.strellis.ampflower2.data.model.AlbumSong;
import ar.com.strellis.ampflower2.data.model.Artist;
import ar.com.strellis.ampflower2.data.model.ArtistRemoteKey;
import ar.com.strellis.ampflower2.data.model.ArtistSong;
import ar.com.strellis.ampflower2.data.model.Playlist;
import ar.com.strellis.ampflower2.data.model.PlaylistRemoteKey;
import ar.com.strellis.ampflower2.data.model.PlaylistSong;
import ar.com.strellis.ampflower2.data.model.Song;

@Database(entities = {Song.class, Album.class, AlbumRemoteKey.class, Artist.class, ArtistRemoteKey.class, Playlist.class, PlaylistRemoteKey.class, AlbumSong.class, ArtistSong.class,
        PlaylistSong.class}, version = 24,exportSchema = false)
@TypeConverters({AmpacheDataConverters.class})
public abstract class AmpacheDatabase extends RoomDatabase {
    public abstract AlbumDao albumDao();
    public abstract ArtistDao artistDao();
    public abstract PlaylistDao playlistDao();
    public abstract SongDao songDao();
    public abstract AlbumSongDao albumSongDao();
    public abstract ArtistSongDao artistSongDao();
    public abstract PlaylistSongDao playlistSongDao();
    public abstract AlbumRemoteKeyDao albumRemoteKeyDao();
    public abstract ArtistRemoteKeyDao artistRemoteKeyDao();
    public abstract PlaylistRemoteKeyDao playlistRemoteKeyDao();
    private static final Object sLock = new Object();

    private static volatile AmpacheDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public static AmpacheDatabase getDatabase(final Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                synchronized (AmpacheDatabase.class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                AmpacheDatabase.class, "ampache-data")
                                .allowMainThreadQueries()
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
    }
}
