package ar.com.strellis.ampflower.data.model;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@Entity(tableName="albums")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Album extends ModelEntity implements Searchable, Serializable {

    public Album()
    {
        storedArt=false;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @PrimaryKey
    private int id;
    private String name;
    private Artist artist;
    private int year;
    private int tracks;
    private int disk;
    private List<Tag> tag;
    /**
     * URL for the Artwork for the album cover.
     */
    private String art;
    private int flag;
    private String preciserating;
    private String rating;
    private String averagerating;
    private String mbid;
    private boolean storedArt;
    private String localFile;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Artist getArtist() {
        return artist;
    }
    public void setArtist(Artist artist) {
        this.artist = artist;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getTracks() {
        return tracks;
    }
    public void setTracks(int tracks) {
        this.tracks = tracks;
    }
    public int getDisk() {
        return disk;
    }
    public void setDisk(int disk) {
        this.disk = disk;
    }
    public List<Tag> getTag() {
        return tag;
    }
    public void setTag(List<Tag> tag) {
        this.tag = tag;
    }
    public String getArt() {
        return art;
    }
    public void setArt(String art) {
        this.art = art;
    }
    public int getFlag() {
        return flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }
    public String getPreciserating() {
        return preciserating;
    }
    public void setPreciserating(String preciserating) {
        this.preciserating = preciserating;
    }
    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public String getAveragerating() {
        return averagerating;
    }
    public void setAveragerating(String averagerating) {
        this.averagerating = averagerating;
    }
    public String getMbid() {
        return mbid;
    }
    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public boolean isStoredArt() {
        return storedArt;
    }

    public void setStoredArt(boolean storedArt) {
        this.storedArt = storedArt;
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Album)
        {
            Album other=(Album)obj;
            return this.getId() == other.getId()
                    && this.getName().equals(other.getName());
        }
        else
            return false;
    }
}