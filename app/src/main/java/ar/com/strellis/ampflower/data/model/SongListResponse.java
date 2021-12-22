package ar.com.strellis.ampflower.data.model;

import java.util.List;

public class SongListResponse
{
    private List<Song> song;

    public void setSong(List<Song> song)
    {
        this.song=song;
    }
    public List<Song> getSong()
    {
        return this.song;
    }
}
