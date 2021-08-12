package ar.com.strellis.ampflower.data.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class ArtistWithSongs extends EntityWithSongs {
    @Embedded
    private Artist artist;
    @Relation(
            parentColumn = "id",
            entity = Song.class,
            entityColumn = "id",
            associateBy = @Junction(
                    value=ArtistSong.class,
                    parentColumn="artistId",
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
    public Artist getArtist() {
        return artist;
    }
    public void setArtist(Artist artist) {
        this.artist = artist;
    }
    public String getName()
    {
        return artist.getName();
    }
}
