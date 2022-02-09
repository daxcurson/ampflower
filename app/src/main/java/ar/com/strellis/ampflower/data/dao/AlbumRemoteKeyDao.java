package ar.com.strellis.ampflower.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ar.com.strellis.ampflower.data.model.AlbumRemoteKey;

@Dao
public interface AlbumRemoteKeyDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AlbumRemoteKey key);
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insertAll(List<AlbumRemoteKey> keyList);
    @Query("SELECT * FROM album_remote_keys where albumId=:albumId")
    AlbumRemoteKey remoteKeyAlbum(Integer albumId);
    @Query("SELECT * FROM album_remote_keys")
    List<AlbumRemoteKey> allRemoteKeyAlbum();
    @Query("DELETE FROM album_remote_keys")
    void clearRemoteKeys();
}
