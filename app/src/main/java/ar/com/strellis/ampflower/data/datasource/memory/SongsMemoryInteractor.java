package ar.com.strellis.ampflower.data.datasource.memory;

import android.util.Log;

import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.EntityWithSongs;
import ar.com.strellis.ampflower.data.model.Song;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class SongsMemoryInteractor<T extends EntityWithSongs>
{
    private final BehaviorSubject<T> observable;
    private T songs;

    public SongsMemoryInteractor()
    {
        observable=BehaviorSubject.create();
    }
    public void saveData(T songs)
    {
        if(songs.getSongs()!=null) {
            Log.d("SongsMemoryInteractor", "Saving received songs: " + songs.getSongs().size() + " songs");
            for (Song songInAlbum : songs.getSongs())
                Log.d("SongsMemoryInteractor", "Saving this song in memory: " + songInAlbum.getName());
            this.songs = songs;
        }
        else
        {
            Log.d("SongsMemoryInteractor","The list of songs received is empty");
        }
        observable.onNext(songs);
    }
    public Maybe<T> getSongs()
    {
        Log.d("SongsMemoryInteractor","Getting songs from memory");
        return songs==null?Maybe.empty() : Maybe.just(songs);
    }
    public Observable<T> getSongsObservable()
    {
        return observable;
    }
}
