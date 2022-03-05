package ar.com.strellis.ampflower.data.model;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "artists")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Artist extends ModelEntity implements Searchable<Integer>, Serializable
{

    public Artist()
    {
        // Nothing. Needed to instantiate the artist from Json when connecting to the Ampacheservice
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PrimaryKey
    private Integer id;
    private String name;
    private int songcount;
    private List<Tag> tag;
    private String art;
    private int flag;
    private String preciserating;
    private String averagerating;
    private String mbid;
    private String summary;
    private int yearformed;
    private String placeformed;
    @Ignore
    private int page;
    public Integer getId() {
        return id;
    }

    @Override
    public int getPage() {
        return this.page;
    }

    @Override
    public void setPage(int page) {
        this.page=page;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setSongcount(int songcount)
    {
        this.songcount=songcount;
    }
    public int getSongcount()
    {
        return this.songcount;
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
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public int getYearformed() {
        return yearformed;
    }
    public void setYearformed(int yearformed) {
        this.yearformed = yearformed;
    }
    public String getPlaceformed() {
        return placeformed;
    }
    public void setPlaceformed(String placeformed) {
        this.placeformed = placeformed;
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Artist)
        {
            Artist other=(Artist)obj;
            return this.getId().equals(other.getId())
                    && this.getName().equals(other.getName());
        }
        else
            return false;
    }
}
