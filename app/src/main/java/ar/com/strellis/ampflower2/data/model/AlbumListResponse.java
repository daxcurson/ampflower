package ar.com.strellis.ampflower2.data.model;

import java.util.List;

public class AlbumListResponse {
    private List<Album> album;
    private AmpacheError error;
    private int total_count;
    private String md5;

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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
