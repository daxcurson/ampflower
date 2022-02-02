package ar.com.strellis.ampflower.data.model;

import java.io.Serializable;

public class SelectableSong implements Serializable
{
    public SelectableSong(Song s,boolean selected)
    {
        this.song=s;
        this.selected=selected;
    }
    private Song song;
    private boolean selected;

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
