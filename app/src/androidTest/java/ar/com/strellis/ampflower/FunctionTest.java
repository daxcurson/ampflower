package ar.com.strellis.ampflower;

import static org.junit.Assert.fail;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumListResponse;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.ArtistListResponse;
import ar.com.strellis.ampflower.data.model.GoodbyeResponse;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.PingResponse;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.model.PlaylistListResponse;
import ar.com.strellis.ampflower.data.model.Song;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class FunctionTest {
    private AmpacheService ampache;
    private LoginResponse loginResponse;

    @Before
    public void init() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        String url = "http://192.168.1.5/ampache/";
        //.addConverterFactory(new ErrorConverterFactory())
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                //.addConverterFactory(new ErrorConverterFactory())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        ampache = retrofit.create(AmpacheService.class);
        loginResponse=getLoginResponse();
    }/*
    @Test
    public void test01Ping()
    {
        Call<PingResponse> p=ampache.ping(loginResponse.getAuth());
        try {
            Response<PingResponse> r=p.execute();
            if(r.isSuccessful())
            {
                PingResponse pp=r.body();
                assert pp != null;
                System.out.println("PingResponse: "+pp.getSession_expire());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    @Test
    public void test02Goodbye()
    {
        Call<GoodbyeResponse> p=ampache.goodbye(loginResponse.getAuth());
        try
        {
            Response<GoodbyeResponse> r=p.execute();
            if(r.isSuccessful())
            {
                GoodbyeResponse g=r.body();
                assert g != null;
                System.out.println("Response: "+g.getSuccess());
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    @Test
    public void test03ListAlbums()
    {
        Call<AlbumListResponse> callForAlbums=ampache.get_indexes_album(loginResponse.getAuth(),"",null,null);
        try
        {
            Response<AlbumListResponse> albumsResponse=callForAlbums.execute();
            if(albumsResponse.isSuccessful())
            {
                assert albumsResponse.body() != null;
                List<Album> albums=albumsResponse.body().getAlbum();
                if (albums == null) throw new AssertionError();
                for (Album album : albums) {
                    System.out.println("Album retrieved: " + album.getName());
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    public void test04ListSongs()
    {
        Call<List<Song>> callForAlbums=ampache.get_indexes_song(loginResponse.getAuth(),"",null,null);
        try
        {
            Response<List<Song>> songsResponse=callForAlbums.execute();
            if(songsResponse.isSuccessful())
            {
                List<Song> songs=songsResponse.body();
                if (songs == null) throw new AssertionError();
                for (Song song : songs) {
                    System.out.println("Song retrieved: " + song.getName());
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    public void test05ListArtists()
    {
        Call<ArtistListResponse> callForArtists=ampache.get_indexes_artist(loginResponse.getAuth(),"",null,null);
        try
        {
            Response<ArtistListResponse> artistsResponse=callForArtists.execute();
            if(artistsResponse.isSuccessful())
            {
                assert artistsResponse.body() != null;
                List<Artist> artists=artistsResponse.body().getArtist();
                if (artists == null) throw new AssertionError();
                for (Artist artist : artists) {
                    System.out.println("Artist retrieved: " + artist.getName());
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    public void test06ListPlaylists()
    {
        Call<PlaylistListResponse> callForPlaylists=ampache.get_indexes_playlist(loginResponse.getAuth(),"",null,null);
        try
        {
            Response<PlaylistListResponse> playlistsResponse=callForPlaylists.execute();
            if(playlistsResponse.isSuccessful())
            {
                assert playlistsResponse.body() != null;
                List<Playlist> playlists=playlistsResponse.body().getPlaylist();
                if (playlists == null) throw new AssertionError();
                for (Playlist playlist : playlists) {
                    System.out.println("Playlist retrieved: " + playlist.getName());
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }
    private LoginResponse getLoginResponse()
    {
        try {
            String user = "testuser";
            String password="MyTestUser5%";
            String timestamp = String.valueOf(System.currentTimeMillis()/1000);
            MessageDigest crypt = MessageDigest.getInstance("SHA-256");
            crypt.reset();
            crypt.update(password.getBytes());
            String key=byteToHex(crypt.digest());
            String auth =  timestamp+key;
            crypt.reset();
            crypt.update(auth.getBytes());
            String authToSend=byteToHex(crypt.digest());
            Call<LoginResponse> call= ampache.handshake(authToSend, user, timestamp);
            if(call==null)
            {
                throw new RuntimeException("Error al autenticar");
            }
            else
            {
                Response<LoginResponse> response=call.execute();
                if(response.isSuccessful())
                {
                    LoginResponse r= response.body();
                    assert r != null;
                    if(r.getError()==null)
                        return r;
                }
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            fail();
        }
        return null;
    }
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
