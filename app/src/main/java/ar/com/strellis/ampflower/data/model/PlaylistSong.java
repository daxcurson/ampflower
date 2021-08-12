package ar.com.strellis.ampflower.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "playlistsongs",
        primaryKeys = {"playlistId","songId"},
        indices = {@Index("songId")})
public class PlaylistSong
{
    @NonNull
    private String playlistId;
    private int songId;

    public void setPlaylistId(String playlistId)
    {
        this.playlistId=playlistId;
    }
    public String getPlaylistId()
    {
        return this.playlistId;
    }
    public void setSongId(int songId)
    {
        this.songId=songId;
    }
    public int getSongId()
    {
        return this.songId;
    }
}
