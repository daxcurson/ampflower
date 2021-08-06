package ar.com.strellis.ampflower.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.gson.JsonSyntaxException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower.data.datasource.db.SongsDatabaseInteractor;
import ar.com.strellis.ampflower.data.datasource.network.SongsNetworkInteractor;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.Song;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

public class SongsRepository
{
    private Disposable dataProviderDisposable;
    private final SongsMemoryInteractor songsMemoryInteractor;
    private final SongsDatabaseInteractor songsDatabaseInteractor;
    private final SongsNetworkInteractor songsNetworkInteractor;
    public SongsRepository(Context context,AmpacheService ampacheService, LiveData<LoginResponse> settings)
    {
        songsMemoryInteractor=new SongsMemoryInteractor();
        AmpacheDatabase appDatabase=AmpacheDatabase.getDatabase(context);
        songsDatabaseInteractor=new SongsDatabaseInteractor(appDatabase,songsMemoryInteractor);
        songsNetworkInteractor=new SongsNetworkInteractor(settings,ampacheService,songsDatabaseInteractor,songsMemoryInteractor);
    }

    /**
     * If there is a Disposable that we are waiting on, and is still active, then the network operation to
     * retrieve the songs is still in progress.
     * @return true if the songs are currently being retrieved from the network
     */
    private boolean isNetworkInProgress()
    {
        return dataProviderDisposable != null && !dataProviderDisposable.isDisposed();
    }
    protected void handleNonHttpException(Throwable throwable) {
        // if not an HttpException throw further
        Log.d("SongsRepository","Error, error, error!!!");
        if (throwable instanceof HttpException) {
            Log.d("SongsRepository","HttpException: "+throwable);
        } else if (throwable instanceof JsonSyntaxException) {
            Log.d("SongsRepository","HttpException: "+throwable);

        } else if (throwable instanceof SocketTimeoutException) {
            Log.d("SongsRepository","SocketTimeoutException: "+throwable);

        } else if (throwable instanceof ConnectException) {
            Log.d("SongsRepository","ConnectException: "+throwable);

        } else {
            Log.d("SongsRepository","New exception of some kind: "+throwable);
            throw new RuntimeException(throwable);
        }
    }

    /**
     * Sends a request to all three storages at the same time. When the operations are completed, the result
     * is cascaded back to the application from the network, then the database, then the memory.
     * Ultimately, the object is retrieved from the memory; not the actual object, but an Observable which will
     * contain the required object once it is known.
     * @param album_id the id of the album required
     * @return An Observable, that will return the album and the songs it contains.
     */
    @SuppressLint("CheckResult")
    public Observable<AlbumWithSongs> getSongsByAlbum(int album_id)
    {
        Observable<AlbumWithSongs> memoryObservable=songsMemoryInteractor.getSongs().toObservable();
        Observable<AlbumWithSongs> databaseObservable=songsDatabaseInteractor.getSongs(album_id).toObservable();
        Observable<AlbumWithSongs> networkObservable=songsNetworkInteractor.getSongsByAlbum(album_id).toObservable();
        if(!isNetworkInProgress())
        {
            Log.d("SongsRepository","Network is not in progress, go ahead and check the observable");
            // I'll request the 3 observables and keep the entry that is returned first.
            dataProviderDisposable=Observable
                    .concat(memoryObservable,databaseObservable,networkObservable)
                    //.firstElement()
                    .subscribe((data)->{
                        Log.d("SongsRepository", "Concatenated the 3 Observables, received data for album "+data.getAlbum().getId());
                        for(Song s:data.getSongs())
                        {
                            Log.d("SongsRepository","Received this song: "+s.getName());
                        }
                    },this::handleNonHttpException);
        }
        return songsMemoryInteractor.getSongsObservable();
        /*
        Observable<List<Song>> songs=ampacheService.album_songs(settings.getAuth(),String.valueOf(album_id),0,Integer.MAX_VALUE);
        songs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                songsObserved -> {
                    if(!songsObserved.isEmpty())
                    {
                        Log.d("SongsRepository.getSongsByAlbum","I received songs from the network: "+songsObserved.size()+" songs");
                        appDatabase.songDao().insertAllSongs(songsObserved);
                        // Now for each song, create a relationship and store it
                        // in the table.
                        List<AlbumSong> songsToInsert=new LinkedList<>();
                        for(Song s:songsObserved)
                        {
                            AlbumSong albumSong=new AlbumSong();
                            albumSong.setAlbumId(album_id);
                            albumSong.setSongId(s.getId());
                            Log.d("SongsRepository.getSongsByAlbum","Storing AlbumSong entity for this song: "+s.getName());
                            songsToInsert.add(albumSong);
                        }
                        appDatabase.albumSongDao().insertAll(songsToInsert);
                    }
                    else
                    {
                        Log.d("SongsRepository","No songs retrieved for the album "+album_id);
                    }
                }, throwable -> Log.i("SongsRepository", Objects.requireNonNull(throwable.getMessage()))
        );
        Log.d("SongsRepository.getSongsByAlbum","Returning songs from the database for the album_id "+album_id);
        return appDatabase.albumDao().listAlbumSongs(album_id);
         */
    }
}
