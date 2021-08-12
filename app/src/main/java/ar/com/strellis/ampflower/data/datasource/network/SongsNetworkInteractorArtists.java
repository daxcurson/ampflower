package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Objects;

import ar.com.strellis.ampflower.data.datasource.db.SongsDatabaseInteractor;
import ar.com.strellis.ampflower.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.ArtistWithSongs;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Single;

public class SongsNetworkInteractorArtists extends SongsNetworkInteractor<ArtistWithSongs>
{
    public SongsNetworkInteractorArtists(LiveData<LoginResponse> settings, AmpacheService ampacheService, SongsDatabaseInteractor<ArtistWithSongs> databaseInteractor, SongsMemoryInteractor<ArtistWithSongs> memoryInteractor) {
        super(settings, ampacheService, databaseInteractor, memoryInteractor);
    }

    @Override
    public Single<ArtistWithSongs> getSongs(String entityId) {
        Log.d("SongsNetworkInteractorArtists.getSongs","Getting songs from artist "+entityId+"from the network, auth: "+ Objects.requireNonNull(settings.getValue()).getAuth());
        return ampacheService.artist_songs(Objects.requireNonNull(settings.getValue()).getAuth(),String.valueOf(entityId),0,Integer.MAX_VALUE)
                .map(songs -> {
                    Log.d("SongsNetworkInteractorArtists.getSongs$1","Received songs from the network!");
                    Artist artist=new Artist();
                    artist.setId(Integer.parseInt(entityId));
                    ArtistWithSongs s=new ArtistWithSongs();
                    s.setArtist(artist);
                    s.setSongs(songs);
                    return s;
                })
                .doOnSuccess(databaseInteractor::saveData)
                .doOnSuccess(memoryInteractor::saveData)
                .doOnError(throwable -> Log.d("SongsNetworkInteractorArtists","Error interacting with the network: "+throwable));
    }
}
