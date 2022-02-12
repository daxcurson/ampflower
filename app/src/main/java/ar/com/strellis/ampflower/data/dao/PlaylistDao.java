package ar.com.strellis.ampflower.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import ar.com.strellis.ampflower.data.model.ArtistWithSongs;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.model.PlaylistWithSongs;
import io.reactivex.rxjava3.core.Maybe;

@Dao
public interface PlaylistDao {
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insertPlaylist(Playlist playlist);
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void insertAllPlaylists(List<Playlist> playlists);
    @Update
    void updatePlaylist(Playlist playlist);
    @Delete
    void deletePlaylist(Playlist playlist);
    @Query("DELETE FROM playlists")
    void deleteAll();
    @Query("SELECT * FROM playlists")
    List<Playlist> listAllPlaylists();
    @Transaction
    @Query("select * from playlists where id=:playlistId")
    Maybe<PlaylistWithSongs> listPlaylistSongsObservable(String playlistId);
}
