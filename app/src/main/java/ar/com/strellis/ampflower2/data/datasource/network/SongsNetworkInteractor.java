package ar.com.strellis.ampflower2.data.datasource.network;

import androidx.lifecycle.LiveData;

import ar.com.strellis.ampflower2.data.datasource.db.SongsDatabaseInteractor;
import ar.com.strellis.ampflower2.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower2.data.model.EntityWithSongs;
import ar.com.strellis.ampflower2.data.model.LoginResponse;
import ar.com.strellis.ampflower2.networkutils.AmpacheService;
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
