package ar.com.strellis.ampflower.data.model;

import java.util.List;

public class ArtistListResponse
{
    private List<Artist> artist;

    public void setArtist(List<Artist> artist)
    {
        this.artist=artist;
    }
    public List<Artist> getArtist()
    {
        return this.artist;
    }
}
