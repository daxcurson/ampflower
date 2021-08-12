package ar.com.strellis.ampflower.data.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class PlaylistWithSongs extends EntityWithSongs
{
    @Embedded
    private Playlist playlist;
    @Relation(
            parentColumn = "id",
            entity = Song.class,
            entityColumn = "id",
            associateBy = @Junction(
                    value=PlaylistSong.class,
                    parentColumn="playlistId",
                    entityColumn = "songId"
            )
    )
    private List<Song> songs;

    public List<Song> getSongs() {
        return songs;
    }
    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
    public Playlist getPlaylist() {
        return playlist;
    }
    public void setPlaylist(Playlist playlist) {
        this.playlist=playlist;
    }
    public String getName()
    {
        return playlist.getName();
    }
}
