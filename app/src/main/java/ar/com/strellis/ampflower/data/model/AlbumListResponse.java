package ar.com.strellis.ampflower.data.model;

import java.util.List;

public class AlbumListResponse {
    private List<Album> album;

    public List<Album> getAlbum()
    {
        return album;
    }
    public void setAlbum(List<Album> album)
    {
        this.album=album;
    }
}
