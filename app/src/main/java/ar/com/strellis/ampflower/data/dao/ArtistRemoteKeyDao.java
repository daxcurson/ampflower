package ar.com.strellis.ampflower.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ar.com.strellis.ampflower.data.model.AlbumRemoteKey;
import ar.com.strellis.ampflower.data.model.ArtistRemoteKey;

@Dao
public interface ArtistRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ArtistRemoteKey key);
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insertAll(List<ArtistRemoteKey> keyList);
    @Query("SELECT * FROM artist_remote_keys where artistId=:artistId")
    ArtistRemoteKey remoteKeyArtist(Integer artistId);
    @Query("SELECT * FROM artist_remote_keys")
    List<ArtistRemoteKey> allRemoteKeyAlbum();
    @Query("DELETE FROM artist_remote_keys")
    void clearRemoteKeys();
}
