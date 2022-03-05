package ar.com.strellis.ampflower.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="playlist_remote_keys")
public class PlaylistRemoteKey {
    @PrimaryKey
    @NonNull
    private String playlistId;
    private Integer prevKey;
    private Integer nextKey;
    public PlaylistRemoteKey(String playlistId,Integer prevKey,Integer nextKey)
    {
        this.playlistId=playlistId;
        this.prevKey=prevKey;
        this.nextKey =nextKey;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public Integer getPrevKey() {
        return prevKey;
    }

    public void setPrevKey(Integer prevKey) {
        this.prevKey = prevKey;
    }

    public Integer getNextKey() {
        return nextKey;
    }

    public void setNextKey(Integer nextKey) {
        this.nextKey = nextKey;
    }
}
