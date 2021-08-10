package ar.com.strellis.ampflower.data.model;

import java.util.List;

public abstract class EntityWithSongs {
    public abstract String getName();
    public abstract List<Song> getSongs();
    public abstract void setSongs(List<Song> songs);
}
