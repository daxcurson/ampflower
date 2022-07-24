package ar.com.strellis.ampflower;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.IOException;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.dao.AlbumDao;
import ar.com.strellis.ampflower.data.dao.AlbumSongDao;
import ar.com.strellis.ampflower.data.dao.SongDao;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumSong;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.Song;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private AmpacheDatabase db;
    private AlbumDao albumDao;
    private AlbumSongDao albumSongDao;
    private SongDao songDao;
    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        //db = Room.inMemoryDatabaseBuilder(context, AmpacheDatabase.class).build();
        db = Room.databaseBuilder(context.getApplicationContext(),
                AmpacheDatabase.class, "ampache-data")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        albumDao=db.albumDao();
        albumSongDao=db.albumSongDao();
        songDao=db.songDao();
    }
    @After
    public void closeDb() throws IOException {
        db.close();
    }
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("ar.com.strellis.ampflower", appContext.getPackageName());
    }/*
    @Test
    public void saveAlbumSong()
    {
        // Let's write an AlbumSong and then try to read it.
        Album album=new Album();
        album.setId(3036);
        Song song=new Song();
        song.setAlbum(album);
        song.setName("Groovejet");
        AlbumSong albumSong = new AlbumSong();
        albumSong.setAlbumId(album.getId());
        albumSong.setSongId(song.getId());
        // Now, save.
        albumDao.insertAlbum(album);
        songDao.insertSong(song);
        albumSongDao.insert(albumSong);
        // And now... try to retrieve an AlbumWithSongs
        AlbumWithSongs songs=albumDao.listAlbumSongs(3036);
        // Assert that the list of songs is not empty.
        assertNotEquals(true,songs.getSongs().isEmpty());
    }*/
    @Test
    public void readAlbumSong()
    {
        AlbumWithSongs songs=albumDao.listAlbumSongs(3036);
        assertNotEquals(true,songs.getSongs().isEmpty());
    }
}