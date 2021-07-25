package ar.com.strellis.ampflower.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import ar.com.strellis.ampflower.data.model.Album;
import io.reactivex.Maybe;

@Dao
public interface AlbumDao {
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insertAlbum(Album album);
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void insertAllAlbums(List<Album> albums);
    @Update
    void updateAlbum(Album album);
    @Delete
    void deleteAlbum(Album albums);
    @Query("DELETE FROM albums")
    void deleteAll();
    @Query("SELECT * FROM albums")
    List<Album> listAllAlbums();
}
