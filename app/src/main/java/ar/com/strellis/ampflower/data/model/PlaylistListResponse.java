package ar.com.strellis.ampflower.data.model;

import java.util.List;

public class PlaylistListResponse {
    private List<Playlist> playlist;
    private AmpacheError error;
    private int total_count;

    public void setPlaylist(List<Playlist> playlist)
    {
        this.playlist=playlist;
    }
    public List<Playlist> getPlaylist()
    {
        return this.playlist;
    }
    public AmpacheError getError()
    {
        return this.error;
    }
    public void setError(AmpacheError error)
    {
        this.error=error;
    }
    public void setTotal_count(int total_count)
    {
        this.total_count=total_count;
    }
    public int getTotal_count()
    {
        return this.total_count;
    }
}
