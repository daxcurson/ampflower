package ar.com.strellis.ampflower.data.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
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
    @Query("DELETE FROM albums where name like :query")
    void deleteByQuery(String query);
    @Query("select * from albums where id=:albumId")
    LiveData<List<Album>> listAlbums(int albumId);
    @Transaction
    @Query("select * from albums where id=:albumId")
    Maybe<AlbumWithSongs> listAlbumSongsObservable(int albumId);
    @Query("SELECT * FROM albums")
    List<Album> listAllAlbums();
    @Query("select * from albums where name like :query")
    List<Album> listAlbumsByname(String query);
    @Query("select * from albums where name like :query")
    PagingSource<Integer, Album> pagingSource(String query);
    @Query("select * from albums where name like :s")
    PagingSource<Integer, Album> listAlbumsByNameRx(String s);
}
