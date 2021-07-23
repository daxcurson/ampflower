package ar.com.strellis.ampflower.data.model;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Entity(tableName = "songs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Song {

    public Song()
    {
        // Nothing. Needed to instantiate the song from Json when connecting to the Ampacheservice
    }

    @PrimaryKey
    private int id;
    private String title;
    private String name;
    private Artist artist;
    private Album album;
    private List<Tag> tag;
    private Artist albumartist;
    private String filename;
    private int track;
    private int playlisttrack;
    private int time;
    private int year;
    private int bitrate;
    private String mode;
    private String mime;
    private String url;
    private long size;
    private String mbid;
    private String album_mbid;
    private String artist_mbid;
    private String art;
    private int flag;
    private String preciserating;
    private int playcount;
    private int catalog;
    private String composer;
    private String channels;
    private String comment;
    private String publisher;
    private String language;
    private double replaygain_album_gain;
    private double replaygain_album_peak;
    private double replaygain_track_gain;
    private double replaygain_track_peak;
    private List<Genre> genre;
    private String rate;
    private String albumartist_mbid;
    private String rating;
    private String averagerating;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Artist getArtist() {
        return artist;
    }
    public void setArtist(Artist artist) {
        this.artist = artist;
    }
    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }
    public List<Tag> getTag() {
        return tag;
    }
    public void setTag(List<Tag> tag) {
        this.tag = tag;
    }
    public Artist getAlbumartist() {
        return albumartist;
    }
    public void setAlbumartist(Artist albumartist) {
        this.albumartist = albumartist;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public int getTrack() {
        return track;
    }
    public void setTrack(int track) {
        this.track = track;
    }
    public int getPlaylisttrack() {
        return playlisttrack;
    }
    public void setPlaylisttrack(int playlisttrack) {
        this.playlisttrack = playlisttrack;
    }
    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getBitrate() {
        return bitrate;
    }
    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }
    public String getMode() {
        return mode;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }
    public String getMime() {
        return mime;
    }
    public void setMime(String mime) {
        this.mime = mime;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public String getMbid() {
        return mbid;
    }
    public void setMbid(String mbid) {
        this.mbid = mbid;
    }
    public String getAlbum_mbid() {
        return album_mbid;
    }
    public void setAlbum_mbid(String album_mbid) {
        this.album_mbid = album_mbid;
    }
    public String getArtist_mbid() {
        return artist_mbid;
    }
    public void setArtist_mbid(String artist_mbid) {
        this.artist_mbid = artist_mbid;
    }
    public String getArt() {
        return art;
    }
    public void setArt(String art) {
        this.art = art;
    }
    public int getFlag() {
        return flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }
    public String getPreciserating() {
        return preciserating;
    }
    public void setPreciserating(String preciserating) {
        this.preciserating = preciserating;
    }
    public int getPlaycount() {
        return playcount;
    }
    public void setPlaycount(int playcount) {
        this.playcount = playcount;
    }
    public int getCatalog() {
        return catalog;
    }
    public void setCatalog(int catalog) {
        this.catalog = catalog;
    }
    public String getComposer() {
        return composer;
    }
    public void setComposer(String composer) {
        this.composer = composer;
    }
    public String getChannels() {
        return channels;
    }
    public void setChannels(String channels) {
        this.channels = channels;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public double getReplaygain_album_gain() {
        return replaygain_album_gain;
    }
    public void setReplaygain_album_gain(double replaygain_album_gain) {
        this.replaygain_album_gain = replaygain_album_gain;
    }
    public double getReplaygain_album_peak() {
        return replaygain_album_peak;
    }
    public void setReplaygain_album_peak(double replaygain_album_peak) {
        this.replaygain_album_peak = replaygain_album_peak;
    }
    public double getReplaygain_track_gain() {
        return replaygain_track_gain;
    }
    public void setReplaygain_track_gain(double replaygain_track_gain) {
        this.replaygain_track_gain = replaygain_track_gain;
    }
    public double getReplaygain_track_peak() {
        return replaygain_track_peak;
    }
    public void setReplaygain_track_peak(double replaygain_track_peak) {
        this.replaygain_track_peak = replaygain_track_peak;
    }
    public List<Genre> getGenre() {
        return genre;
    }
    public void setGenre(List<Genre> genre) {
        this.genre = genre;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAlbumartist_mbid() {
        return albumartist_mbid;
    }

    public void setAlbumartist_mbid(String albumartist_mbid) {
        this.albumartist_mbid = albumartist_mbid;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAveragerating() {
        return averagerating;
    }

    public void setAveragerating(String averagerating) {
        this.averagerating = averagerating;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Song)
        {
            Song otherSong=(Song)obj;
            return otherSong.getId()==this.getId() && otherSong.getName().equals(this.getName());
        }
        return false;
    }
}
