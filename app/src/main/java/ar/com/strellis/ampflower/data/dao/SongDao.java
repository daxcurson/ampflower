package ar.com.strellis.ampflower.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ar.com.strellis.ampflower.data.model.Song;

@Dao
public interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertSong(Song song);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllSongs(List<Song> songList);
    @Update
    public void updateSong(Song song);
    @Delete
    public void deleteSong(Song song);
    @Query("SELECT * FROM songs")
    public List<Song> listAllSongs();
}
