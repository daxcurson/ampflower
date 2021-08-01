package ar.com.strellis.ampflower.data.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class AlbumWithSongs extends EntityWithSongs {
    @Embedded
    private Album album;
    @Relation(
            parentColumn = "id",
            entity = Song.class,
            entityColumn = "id",
            associateBy = @Junction(
                    value=AlbumSong.class,
                    parentColumn="albumId",
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
    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }
}
