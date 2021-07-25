package ar.com.strellis.ampflower.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ar.com.strellis.ampflower.data.dao.AlbumDao;
import ar.com.strellis.ampflower.data.dao.ArtistDao;
import ar.com.strellis.ampflower.data.dao.PlaylistDao;
import ar.com.strellis.ampflower.data.dao.SongDao;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.model.Song;

@Database(entities = {Song.class, Album.class, Artist.class, Playlist.class}, version = 8,exportSchema = false)
@TypeConverters({AmpacheDataConverters.class})
public abstract class AmpacheDatabase extends RoomDatabase {
    public abstract AlbumDao albumDao();
    public abstract ArtistDao artistDao();
    public abstract PlaylistDao playlistDao();
    public abstract SongDao songDao();
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
    }

}
