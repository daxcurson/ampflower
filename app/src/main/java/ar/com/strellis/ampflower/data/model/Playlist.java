package ar.com.strellis.ampflower.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName="playlists")
public class Playlist implements Searchable, Serializable
{
    private String playlistName;

    public Playlist()
    {
        // Nothing. Needed to instantiate the playlist from Json when connecting to the Ampacheservice
        id=0;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    @NonNull
    @PrimaryKey
    private int id;
    private String name;
    private String owner;
    private int items;
    private String type;
    private String art;
    public int getId() {
        return id;
    }
    public void setId(int id) {
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Playlist)
        {
            Playlist otherPlaylist=(Playlist)obj;
            return otherPlaylist.id==id && otherPlaylist.playlistName.equals(playlistName);
        }
        else
            return false;
    }
}
