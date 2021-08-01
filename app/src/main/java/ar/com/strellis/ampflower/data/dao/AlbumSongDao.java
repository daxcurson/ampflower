package ar.com.strellis.ampflower.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ar.com.strellis.ampflower.data.model.AlbumSong;

@Dao
public interface AlbumSongDao {
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(AlbumSong album);
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void insertAll(List<AlbumSong> albums);
    @Update
    void update(AlbumSong album);
    @Delete
    void delete(AlbumSong album);
    @Query("DELETE FROM albumsongs")
    void deleteAll();
}
