package ar.com.strellis.ampflower.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "artist_remote_keys")
public class ArtistRemoteKey {
    @PrimaryKey
    private Integer artistId;
    private Integer prevKey;
    private Integer nextKey;
    public ArtistRemoteKey(Integer artistId,Integer prevKey,Integer nextKey)
    {
        this.artistId=artistId;
        this.prevKey=prevKey;
        this.nextKey =nextKey;
    }

    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
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
