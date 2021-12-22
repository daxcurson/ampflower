package ar.com.strellis.ampflower.data.model;

import java.util.List;

public class PlaylistListResponse {
    private List<Playlist> playlist;

    public void setPlaylist(List<Playlist> playlist)
    {
        this.playlist=playlist;
    }
    public List<Playlist> getPlaylist()
    {
        return this.playlist;
    }
}
