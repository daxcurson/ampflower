package ar.com.strellis.ampflower.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ar.com.strellis.ampflower.data.model.AlbumSong;
import ar.com.strellis.ampflower.data.model.PlaylistSong;

@Dao
public interface PlaylistSongDao {
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(PlaylistSong playlistSong);
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void insertAll(List<PlaylistSong> playlists);
    @Update
    void update(PlaylistSong playlist);
    @Delete
    void delete(PlaylistSong playlistSong);
    @Query("DELETE FROM playlistsongs")
    void deleteAll();
}
