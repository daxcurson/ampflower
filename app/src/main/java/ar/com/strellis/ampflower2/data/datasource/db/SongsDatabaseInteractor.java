package ar.com.strellis.ampflower2.data.datasource.db;

import android.util.Log;

import ar.com.strellis.ampflower2.data.AmpacheDatabase;
import ar.com.strellis.ampflower2.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower2.data.model.EntityWithSongs;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class SongsDatabaseInteractor<T extends EntityWithSongs>
{
    protected final AmpacheDatabase appDatabase;
    protected final SongsMemoryInteractor<T> memoryInteractor;

    public SongsDatabaseInteractor(AmpacheDatabase appDatabase,SongsMemoryInteractor<T> memoryInteractor)
    {
        this.appDatabase=appDatabase;
        this.memoryInteractor=memoryInteractor;
    }

    public Maybe<T> getSongs(String album_id)
    {
        Log.d("SongsDatabaseInteractor.getSongs","Getting the songs from the database");
        return getSongsObservable(album_id)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(albumWithSongs -> {
                    Log.d("SongsDatabaseInteractor.getSongs$onSuccess","I have some songs: "+albumWithSongs.getSongs().size()+" songs");
                    memoryInteractor.saveData(albumWithSongs);
                });
    }
    public abstract void saveData(T songs);
    protected abstract Maybe<T> getSongsObservable(String entityId);
}
