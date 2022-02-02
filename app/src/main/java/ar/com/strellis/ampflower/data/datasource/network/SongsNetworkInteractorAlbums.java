package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Objects;

import ar.com.strellis.ampflower.data.datasource.db.SongsDatabaseInteractor;
import ar.com.strellis.ampflower.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Single;

public class SongsNetworkInteractorAlbums extends SongsNetworkInteractor<AlbumWithSongs>
{
    public SongsNetworkInteractorAlbums(LiveData<LoginResponse> settings, AmpacheService ampacheService, SongsDatabaseInteractor<AlbumWithSongs> databaseInteractor, SongsMemoryInteractor<AlbumWithSongs> memoryInteractor) {
        super(settings, ampacheService, databaseInteractor, memoryInteractor);
    }
    public Single<AlbumWithSongs> getSongs(String album_id)
    {
        Log.d("SongsNetworkInteractor.getSongsByAlbum","Getting songs from album "+album_id+" from the network, auth: "+ Objects.requireNonNull(settings.getValue()).getAuth());
        return ampacheService.album_songs(Objects.requireNonNull(settings.getValue()).getAuth(),String.valueOf(album_id),0,Integer.MAX_VALUE)
                .map(songs -> {
                    Log.d("SongsNetworkInteractor.getSongsByAlbum$1","Received songs from the network!");
                    Album album=new Album();
                    album.setId(Integer.parseInt(album_id));
                    AlbumWithSongs s=new AlbumWithSongs();
                    s.setAlbum(album);
                    s.setSongs(songs.getSong());
                    return s;
                })
                .doOnSuccess(databaseInteractor::saveData)
                .doOnSuccess(memoryInteractor::saveData)
                .doOnError(throwable ->
                        Log.d("SongsNetworkInteractor","Error interacting with the network: "+throwable));
    }
}
