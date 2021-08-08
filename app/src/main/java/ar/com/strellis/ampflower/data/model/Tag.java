package ar.com.strellis.ampflower.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@Entity(tableName="tags")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag implements Serializable
{
    @PrimaryKey
    private int id;
    private String name;
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
}
