package ar.com.strellis.ampflower.data.model;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "artistsongs",
        primaryKeys = {"artistId","songId"},
        indices = {@Index("songId")})
public class ArtistSong
{
    private int artistId;
    private int songId;

    public int getArtistId()
    {
        return artistId;
    }
    public void setArtistId(int id)
    {
        this.artistId=id;
    }
    public int getSongId()
    {
        return songId;
    }
    public void setSongId(int id)
    {
        this.songId=id;
    }
}
