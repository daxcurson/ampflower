package ar.com.strellis.ampflower2.data.datasource.network;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Objects;

import ar.com.strellis.ampflower2.data.datasource.db.SongsDatabaseInteractor;
import ar.com.strellis.ampflower2.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower2.data.model.Album;
import ar.com.strellis.ampflower2.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower2.data.model.Artist;
import ar.com.strellis.ampflower2.data.model.LoginResponse;
import ar.com.strellis.ampflower2.error.AmpacheSessionExpiredException;
import ar.com.strellis.ampflower2.networkutils.AmpacheService;
import io.reactivex.rxjava3.core.Single;

public class SongsNetworkInteractorAlbums extends SongsNetworkInteractor<AlbumWithSongs>
{
    public SongsNetworkInteractorAlbums(LiveData<LoginResponse> settings, AmpacheService ampacheService, SongsDatabaseInteractor<AlbumWithSongs> databaseInteractor, SongsMemoryInteractor<AlbumWithSongs> memoryInteractor) {
        super(settings, ampacheService, databaseInteractor, memoryInteractor);
    }
    public Single<AlbumWithSongs> getSongs(String album_id)
    {
        Log.d("SongsNetworkInteractorAlbums.getSongsByAlbum","Getting songs from album "+album_id+" from the network, auth: "+ Objects.requireNonNull(settings.getValue()).getAuth());
        return ampacheService.album_songs(Objects.requireNonNull(settings.getValue()).getAuth(),String.valueOf(album_id),0,Integer.MAX_VALUE)
                .map(songs -> {
                    // Songs may be null if there is an error with the server.
                    if(songs.getError()==null) {
                        Log.d("SongsNetworkInteractorAlbums.getSongsByAlbum$1", "Received songs from the network! Album " + album_id);
                        Album album = new Album();
                        album.setId(Integer.parseInt(album_id));
                        Artist artist = new Artist();
                        album.setArtist(artist);
                        AlbumWithSongs s = new AlbumWithSongs();
                        s.setAlbum(album);
                        s.setSongs(songs.getSong());
                        return s;
                    }
                    else
                    {
                        // Throw some kind of exception...?
                        throw new AmpacheSessionExpiredException();
                    }
                })
                .doOnSuccess(databaseInteractor::saveData)
                .doOnSuccess(memoryInteractor::saveData)
                .doOnError(throwable ->
                        Log.d("SongsNetworkInteractorAlbums","Error interacting with the network: "+throwable));
    }
}
