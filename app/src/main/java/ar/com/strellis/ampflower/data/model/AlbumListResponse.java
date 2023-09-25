package ar.com.strellis.ampflower.data.model;

import java.util.List;

public class AlbumListResponse {
    private List<Album> album;
    private AmpacheError error;
    private int total_count;

    public List<Album> getAlbum()
    {
        return album;
    }
    public void setAlbum(List<Album> album)
    {
        this.album=album;
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
