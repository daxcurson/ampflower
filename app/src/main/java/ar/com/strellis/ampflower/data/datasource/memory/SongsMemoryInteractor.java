package ar.com.strellis.ampflower.data.datasource.memory;

import android.util.Log;

import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.Song;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class SongsMemoryInteractor
{
    private final BehaviorSubject<AlbumWithSongs> observable;
    private AlbumWithSongs songs;

    public SongsMemoryInteractor()
    {
        observable=BehaviorSubject.create();
    }
    public void saveData(AlbumWithSongs songs)
    {
        Log.d("SongsMemoryInteractor","Saving received songs: "+songs.getSongs().size()+" songs");
        for(Song songInAlbum:songs.getSongs())
            Log.d("SongsMemoryInteractor","Saving this song in memory: "+songInAlbum.getName());
        this.songs=songs;
        observable.onNext(songs);
    }
    public Maybe<AlbumWithSongs> getSongs()
    {
        Log.d("SongsMemoryInteractor","Getting songs from memory");
        return songs==null?Maybe.empty() : Maybe.just(songs);
    }
    public Observable<AlbumWithSongs> getSongsObservable()
    {
        return observable;
    }
}
