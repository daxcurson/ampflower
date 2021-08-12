package ar.com.strellis.ampflower.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName="playlists")
public class Playlist extends ModelEntity implements Searchable<String>,Serializable
{
    private String playlistName;

    public Playlist()
    {
        // Nothing. Needed to instantiate the playlist from Json when connecting to the Ampacheservice
        id="";
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    @NonNull
    @PrimaryKey
    private String id;
    private String name;
    private String owner;
    private int items;
    private String type;
    private String art;
    private String preciserating;
    private String rating;
    private String averagerating;
    private Integer flag;
    @NonNull
    public String getId() {
        return id;
    }
    public void setId(@NonNull String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public int getItems() {
        return items;
    }
    public void setItems(int items) {
        this.items = items;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getArt()
    {
        return art;
    }
    public void setArt(String art)
    {
        this.art=art;
    }
    public String getPreciserating()
    {
        return preciserating;
    }
    public void setPreciserating(String preciserating)
    {
        this.preciserating=preciserating;
    }
    public String getRating()
    {
        return rating;
    }
    public void setRating(String rating)
    {
        this.rating=rating;
    }
    public String getAveragerating()
    {
        return this.averagerating;
    }
    public void setAveragerating(String averagerating)
    {
        this.averagerating=averagerating;
    }
    public Integer getFlag()
    {
        return flag;
    }
    public void setFlag(Integer flag)
    {
        this.flag=flag;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Playlist)
        {
            Playlist otherPlaylist=(Playlist)obj;
            return otherPlaylist.id.equals(id) && otherPlaylist.playlistName.equals(playlistName);
        }
        else
            return false;
    }
}
