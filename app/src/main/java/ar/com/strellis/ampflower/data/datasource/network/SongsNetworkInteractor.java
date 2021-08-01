package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Objects;

import ar.com.strellis.ampflower.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Single;

public class SongsNetworkInteractor
{
    private final AmpacheService ampacheService;
    private final LiveData<LoginResponse> settings;
    private final SongsDatabaseInteractor databaseInteractor;
    private final SongsMemoryInteractor memoryInteractor;

    public SongsNetworkInteractor(LiveData<LoginResponse> settings, AmpacheService ampacheService, SongsDatabaseInteractor databaseInteractor, SongsMemoryInteractor memoryInteractor)
    {
        this.settings=settings;
        this.ampacheService=ampacheService;
        this.databaseInteractor=databaseInteractor;
        this.memoryInteractor=memoryInteractor;
    }
    public Single<AlbumWithSongs> getSongsByAlbum(int album_id)
    {
        Log.d("SongsNetworkInteractor.getSongsByAlbum","Getting songs from album "+album_id+"from the network, auth: "+ Objects.requireNonNull(settings.getValue()).getAuth());
        return ampacheService.album_songs(Objects.requireNonNull(settings.getValue()).getAuth(),String.valueOf(album_id),0,Integer.MAX_VALUE)
                .map(songs -> {
                    Log.d("SongsNetworkInteractor.getSongsByAlbum$1","Received songs from the network!");
                    Album album=new Album();
                    album.setId(album_id);
                    AlbumWithSongs s=new AlbumWithSongs();
                    s.setAlbum(album);
                    s.setSongs(songs);
                    return s;
                })
                .doOnSuccess(databaseInteractor::saveData)
                .doOnSuccess(memoryInteractor::saveData)
                .doOnError(throwable -> Log.d("SongsNetworkInteractor","Error interacting with the network: "+throwable));
    }
}
