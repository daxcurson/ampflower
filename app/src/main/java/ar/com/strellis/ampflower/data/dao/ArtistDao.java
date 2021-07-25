package ar.com.strellis.ampflower.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ar.com.strellis.ampflower.data.model.Artist;

@Dao
public interface ArtistDao {
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insertArtist(Artist artist);
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void insertAllArtists(List<Artist> artists);
    @Update
    void updateArtist(Artist artist);
    @Delete
    void deleteArtist(Artist artist);
    @Query("DELETE FROM artists")
    void deleteAll();
    @Query("SELECT * FROM artists")
    List<Artist> listAllArtists();
}
