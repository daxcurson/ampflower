package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Objects;

import ar.com.strellis.ampflower.data.datasource.db.SongsDatabaseInteractor;
import ar.com.strellis.ampflower.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.EntityWithSongs;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.core.Single;

public abstract class SongsNetworkInteractor<T extends EntityWithSongs>
{
    protected final AmpacheService ampacheService;
    protected final LiveData<LoginResponse> settings;
    protected final SongsDatabaseInteractor<T> databaseInteractor;
    protected final SongsMemoryInteractor<T> memoryInteractor;

    public SongsNetworkInteractor(LiveData<LoginResponse> settings, AmpacheService ampacheService, SongsDatabaseInteractor<T> databaseInteractor, SongsMemoryInteractor<T> memoryInteractor)
    {
        this.settings=settings;
        this.ampacheService=ampacheService;
        this.databaseInteractor=databaseInteractor;
        this.memoryInteractor=memoryInteractor;
    }
    public abstract Single<T> getSongs(String entityId);
}
