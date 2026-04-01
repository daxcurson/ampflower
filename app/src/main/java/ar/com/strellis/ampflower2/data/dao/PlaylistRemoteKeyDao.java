package ar.com.strellis.ampflower2.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ar.com.strellis.ampflower2.data.model.PlaylistRemoteKey;

@Dao
public interface PlaylistRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlaylistRemoteKey key);
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insertAll(List<PlaylistRemoteKey> keyList);
    @Query("SELECT * FROM playlist_remote_keys where playlistId=:playlistId")
    PlaylistRemoteKey remoteKeyPlaylist(String playlistId);
    @Query("SELECT * FROM playlist_remote_keys")
    List<PlaylistRemoteKey> allRemoteKeyAlbum();
    @Query("DELETE FROM playlist_remote_keys")
    void clearRemoteKeys();
}
