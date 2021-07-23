package ar.com.strellis.ampflower.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse
{
    private String auth;
    private String api;
    private Date session_expire;
    private Date update;
    private Date add;
    private Date clean;
    private int songs;
    private int albums;
    private int artists;
    private int playlists;
    private int videos;
    private int catalogs;
    private AmpacheError error;
    public String getAuth() {
        return auth;
    }
    public void setAuth(String auth) {
        this.auth = auth;
    }
    public String getApi() {
        return api;
    }
    public void setApi(String api) {
        this.api = api;
    }
    public Date getSession_expire() {
        return session_expire;
    }
    public void setSession_expire(Date session_expire) {
        this.session_expire = session_expire;
    }
    public Date getUpdate() {
        return update;
    }
    public void setUpdate(Date update) {
        this.update = update;
    }
    public Date getAdd() {
        return add;
    }
    public void setAdd(Date add) {
        this.add = add;
    }
    public Date getClean() {
        return clean;
    }
    public void setClean(Date clean) {
        this.clean = clean;
    }
    public int getSongs() {
        return songs;
    }
    public void setSongs(int songs) {
        this.songs = songs;
    }
    public int getAlbums() {
        return albums;
    }
    public void setAlbums(int albums) {
        this.albums = albums;
    }
    public int getArtists() {
        return artists;
    }
    public void setArtists(int artists) {
        this.artists = artists;
    }
    public int getPlaylists() {
        return playlists;
    }
    public void setPlaylists(int playlists) {
        this.playlists = playlists;
    }
    public int getVideos() {
        return videos;
    }
    public void setVideos(int videos) {
        this.videos = videos;
    }
    public int getCatalogs() {
        return catalogs;
    }
    public void setCatalogs(int catalogs) {
        this.catalogs = catalogs;
    }
    public AmpacheError getError() {
        return error;
    }
    public void setError(AmpacheError error) {
        this.error = error;
    }
}
