package ar.com.strellis.ampflower.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ar.com.strellis.ampflower.data.model.AlbumSong;
import ar.com.strellis.ampflower.data.model.ArtistSong;

@Dao
public interface ArtistSongDao {
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(ArtistSong artist);
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void insertAll(List<ArtistSong> artistSongs);
    @Update
    void update(ArtistSong artistSong);
    @Delete
    void delete(ArtistSong artist);
    @Query("DELETE FROM artistsongs")
    void deleteAll();
}
