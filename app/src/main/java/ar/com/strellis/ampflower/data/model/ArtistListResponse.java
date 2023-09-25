package ar.com.strellis.ampflower.data.model;

import java.util.List;

public class ArtistListResponse
{
    private List<Artist> artist;
    private int total_count;
    private AmpacheError error;

    public void setArtist(List<Artist> artist)
    {
        this.artist=artist;
    }
    public List<Artist> getArtist()
    {
        return this.artist;
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
        return total_count;
    }
}
