package ar.com.strellis.ampflower.data.model;

import androidx.room.Entity;

@Entity(tableName = "album_remote_keys")
public class AlbumRemoteKey
{
    private String label;
    private String nextKey;
    public AlbumRemoteKey(String label,String nextkey)
    {
        this.label=label;
        this.nextKey =nextkey;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNextKey() {
        return nextKey;
    }

    public void setNextKey(String nextKey) {
        this.nextKey = nextKey;
    }
}
