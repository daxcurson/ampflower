package ar.com.strellis.ampflower.network;

import java.util.List;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.GoodbyeResponse;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.PingResponse;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.model.Song;
import ar.com.strellis.ampflower.data.model.UserResponse;
import io.reactivex.Single;
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
    Call<List<Artist>> get_indexes_artist(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=get_indexes&type=album")
    Call<List<Album>> get_indexes_album(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=get_indexes&type=playlist")
    Call<List<Playlist>> get_indexes_playlist(
            @Query("auth") String auth,
            @Query("filter") String filter,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=album_songs")
    Single<List<Song>> album_songs(
            @Query("auth") String auth,
            @Query("filter") String album_id,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=playlist_songs")
    Call<List<Song>> playlist_songs(
            @Query("auth") String auth,
            @Query("filter") String playlist_id,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=artist_songs")
    Call<List<Song>> artist_songs(
            @Query("auth") String auth,
            @Query("filter") String artist_id,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );
    @GET("server/json.server.php?action=user")
    Call<UserResponse> user(
            @Query("auth") String auth,
            @Query("username") String username
    );
}
