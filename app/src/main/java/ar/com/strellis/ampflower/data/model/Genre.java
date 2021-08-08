package ar.com.strellis.ampflower.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@Entity(tableName = "genres")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Genre implements Serializable
{
    @PrimaryKey
    @NonNull
    private int id;
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
