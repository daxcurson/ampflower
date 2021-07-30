package ar.com.strellis.ampflower.data.dao;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import ar.com.strellis.ampflower.data.model.AlbumRemoteKey;
import io.reactivex.Single;

public interface AlbumRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplace(AlbumRemoteKey remoteKey);

    @Query("SELECT * FROM album_remote_keys WHERE label = :query")
    Single<AlbumRemoteKey> remoteKeyByQuerySingle(String query);

    @Query("DELETE FROM album_remote_keys WHERE label = :query")
    void deleteByQuery(String query);
}
