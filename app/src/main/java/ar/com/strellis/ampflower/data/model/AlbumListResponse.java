package ar.com.strellis.ampflower.data.model;

import java.util.List;

public class AlbumListResponse {
    private List<Album> album;
    private AmpacheError error;

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
}
