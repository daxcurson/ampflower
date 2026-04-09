package ar.com.strellis.ampflower2.data.model;

import java.util.List;

public class SongListResponse
{
    private List<Song> song;
    private AmpacheError error;
    private int total_count;
    private String md5;

    public void setSong(List<Song> song)
    {
        this.song=song;
    }
    public List<Song> getSong()
    {
        return this.song;
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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
