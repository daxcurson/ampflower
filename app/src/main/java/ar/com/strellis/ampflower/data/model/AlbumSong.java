package ar.com.strellis.ampflower.data.model;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "albumsongs",
        primaryKeys = {"albumId","songId"},
        indices = {@Index("songId")})
public class AlbumSong {
    private int albumId;
    private int songId;

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }
}
