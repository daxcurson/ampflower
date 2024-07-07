package ar.com.strellis.ampflower.networkutils;

import java.util.List;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumListResponse;
import ar.com.strellis.ampflower.data.model.ArtistListResponse;
import ar.com.strellis.ampflower.data.model.GoodbyeResponse;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.PingResponse;
import ar.com.strellis.ampflower.data.model.PlaylistListResponse;
import ar.com.strellis.ampflower.data.model.Song;
import ar.com.strellis.ampflower.data.model.SongListResponse;
import ar.com.strellis.ampflower.data.model.UserResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AmpacheService
{
    @GET("server/json.server.php?action=handshake")
    Call<LoginResponse> handshake(
            @Query("auth") String auth,
            @Query("user") String user,
            @Query("timestamp") String timestamp
    );
    @GET("server/json.server.php?action=ping")
    Call<PingResponse> ping(
            @Query("auth") String auth
    );
    @GET("server/json.server.php?action=goodbye")
    Call<GoodbyeResponse> goodbye(
            @Query("auth") String auth
    );
    @GET("server/json.server.php?action=get_indexes&type=song")
    Call<List<Song>> get_indexes_song(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=get_indexes&type=artist")
    Call<ArtistListResponse> get_indexes_artist(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=get_indexes&type=artist")
    Single<ArtistListResponse> get_indexes_artist_rx(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=albums&exact=0")
    Single<AlbumListResponse> albums(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );/*
    @GET("server/json.server.php?action=get_indexes&type=album")
    Single<AlbumListResponse> get_indexes_album_rx(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );*/
    @GET("server/json.server.php?action=get_indexes&type=playlist")
    Call<PlaylistListResponse> get_indexes_playlist(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=get_indexes&type=playlist")
    Single<PlaylistListResponse> get_indexes_playlist_rx(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=album_songs")
    Single<SongListResponse> album_songs(
            @Query("auth") String auth,
            @Query("filter") String album_id,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=playlist_songs")
    Single<SongListResponse> playlist_songs(
            @Query("auth") String auth,
            @Query("filter") String playlist_id,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=artist_songs")
    Single<SongListResponse> artist_songs(
            @Query("auth") String auth,
            @Query("filter") String artist_id,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit,
            @Query("sort") String sort
    );
    @GET("server/json.server.php?action=user")
    Call<UserResponse> user(
            @Query("auth") String auth,
            @Query("username") String username
    );
    @GET("server/json.server.php?action=stats&type=album")
    Single<AlbumListResponse> album_stats(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=stats&type=artist")
    Single<ArtistListResponse> artist_stats(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=stats&type=song")
    Single<SongListResponse> song_stats(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
}
