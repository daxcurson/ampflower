package ar.com.strellis.ampflower.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "album_remote_keys")
public class AlbumRemoteKey
{
    @PrimaryKey
    private Integer albumId;
    private Integer prevKey;
    private Integer nextKey;
    public AlbumRemoteKey(Integer albumId,Integer prevKey,Integer nextKey)
    {
        this.albumId=albumId;
        this.prevKey=prevKey;
        this.nextKey =nextKey;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
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
