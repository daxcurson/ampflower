package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Objects;

import ar.com.strellis.ampflower.data.datasource.db.SongsDatabaseInteractor;
import ar.com.strellis.ampflower.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.model.PlaylistWithSongs;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Single;

public class SongsNetworkInteractorPlaylists extends SongsNetworkInteractor<PlaylistWithSongs>
{
    public SongsNetworkInteractorPlaylists(LiveData<LoginResponse> settings, AmpacheService ampacheService, SongsDatabaseInteractor<PlaylistWithSongs> databaseInteractor, SongsMemoryInteractor<PlaylistWithSongs> memoryInteractor) {
        super(settings, ampacheService, databaseInteractor, memoryInteractor);
    }

    @Override
    public Single<PlaylistWithSongs> getSongs(String entityId) {
        Log.d("SongsNetworkInteractorPlaylists.getSongs","Getting songs from playlist "+entityId+"from the network, auth: "+ Objects.requireNonNull(settings.getValue()).getAuth());
        return ampacheService.playlist_songs(Objects.requireNonNull(settings.getValue()).getAuth(),entityId,0,Integer.MAX_VALUE)
                .map(songs -> {
                    Log.d("SongsNetworkInteractorPlaylists.getSongs$1","Received songs from the network!");
                    Playlist playlist=new Playlist();
                    playlist.setId(entityId);
                    PlaylistWithSongs s=new PlaylistWithSongs();
                    s.setPlaylist(playlist);
                    s.setSongs(songs);
                    return s;
                })
                .doOnSuccess(databaseInteractor::saveData)
                .doOnSuccess(memoryInteractor::saveData)
                .doOnError(throwable -> Log.d("SongsNetworkInteractorPlaylists","Error interacting with the network: "+throwable));

    }
}
