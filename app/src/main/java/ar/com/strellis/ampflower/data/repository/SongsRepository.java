package ar.com.strellis.ampflower.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.gson.JsonSyntaxException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.datasource.db.SongsDatabaseInteractorAlbums;
import ar.com.strellis.ampflower.data.datasource.db.SongsDatabaseInteractorArtists;
import ar.com.strellis.ampflower.data.datasource.db.SongsDatabaseInteractorPlaylists;
import ar.com.strellis.ampflower.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower.data.datasource.db.SongsDatabaseInteractor;
import ar.com.strellis.ampflower.data.datasource.network.SongsNetworkInteractor;
import ar.com.strellis.ampflower.data.datasource.network.SongsNetworkInteractorAlbums;
import ar.com.strellis.ampflower.data.datasource.network.SongsNetworkInteractorArtists;
import ar.com.strellis.ampflower.data.datasource.network.SongsNetworkInteractorPlaylists;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.ArtistWithSongs;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.PlaylistWithSongs;
import ar.com.strellis.ampflower.data.model.Song;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

public class SongsRepository
{
    private Disposable dataProviderDisposable;
    private final SongsMemoryInteractor<AlbumWithSongs> songsMemoryInteractorAlbums;
    private final SongsDatabaseInteractorAlbums songsDatabaseInteractorAlbums;
    private final SongsNetworkInteractorAlbums songsNetworkInteractorAlbums;
    private final SongsMemoryInteractor<ArtistWithSongs> songsMemoryInteractorArtists;
    private final SongsDatabaseInteractorArtists songsDatabaseInteractorArtists;
    private final SongsNetworkInteractorArtists songsNetworkInteractorArtists;
    private final SongsMemoryInteractor<PlaylistWithSongs> songsMemoryInteractorPlaylists;
    private final SongsDatabaseInteractorPlaylists songsDatabaseInteractorPlaylists;
    private final SongsNetworkInteractorPlaylists songsNetworkInteractorPlaylists;
    public SongsRepository(Context context,AmpacheService ampacheService, LiveData<LoginResponse> settings)
    {
        songsMemoryInteractorAlbums=new SongsMemoryInteractor<>();
        AmpacheDatabase appDatabase=AmpacheDatabase.getDatabase(context);
        songsDatabaseInteractorAlbums=new SongsDatabaseInteractorAlbums(appDatabase,songsMemoryInteractorAlbums);
        songsNetworkInteractorAlbums=new SongsNetworkInteractorAlbums(settings,ampacheService,songsDatabaseInteractorAlbums,songsMemoryInteractorAlbums);
        songsMemoryInteractorArtists=new SongsMemoryInteractor<>();
        songsDatabaseInteractorArtists=new SongsDatabaseInteractorArtists(appDatabase,songsMemoryInteractorArtists);
        songsNetworkInteractorArtists=new SongsNetworkInteractorArtists(settings,ampacheService,songsDatabaseInteractorArtists,songsMemoryInteractorArtists);
        songsMemoryInteractorPlaylists=new SongsMemoryInteractor<>();
        songsDatabaseInteractorPlaylists=new SongsDatabaseInteractorPlaylists(appDatabase,songsMemoryInteractorPlaylists);
        songsNetworkInteractorPlaylists=new SongsNetworkInteractorPlaylists(settings,ampacheService,songsDatabaseInteractorPlaylists,songsMemoryInteractorPlaylists);
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
        Observable<AlbumWithSongs> memoryObservable=songsMemoryInteractorAlbums.getSongs().toObservable();
        Observable<AlbumWithSongs> databaseObservable=songsDatabaseInteractorAlbums.getSongs(String.valueOf(album_id)).toObservable();
        Observable<AlbumWithSongs> networkObservable=songsNetworkInteractorAlbums.getSongs(String.valueOf(album_id)).toObservable();
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
        return songsMemoryInteractorAlbums.getSongsObservable();
    }
    @SuppressLint("CheckResult")
    public Observable<ArtistWithSongs> getSongsByArtist(int artist_id)
    {
        Observable<ArtistWithSongs> memoryObservable=songsMemoryInteractorArtists.getSongs().toObservable();
        Observable<ArtistWithSongs> databaseObservable=songsDatabaseInteractorArtists.getSongs(String.valueOf(artist_id)).toObservable();
        Observable<ArtistWithSongs> networkObservable=songsNetworkInteractorArtists.getSongs(String.valueOf(artist_id)).toObservable();
        if(!isNetworkInProgress())
        {
            Log.d("SongsRepository","Network is not in progress, go ahead and check the observable");
            // I'll request the 3 observables and keep the entry that is returned first.
            dataProviderDisposable=Observable
                    .concat(memoryObservable,databaseObservable,networkObservable)
                    //.firstElement()
                    .subscribe((data)->{
                        Log.d("SongsRepository", "Concatenated the 3 Observables, received data for artist "+data.getArtist().getId());
                        for(Song s:data.getSongs())
                        {
                            Log.d("SongsRepository","Received this song: "+s.getName());
                        }
                    },this::handleNonHttpException);
        }
        return songsMemoryInteractorArtists.getSongsObservable();
    }
    @SuppressLint("CheckResult")
    public Observable<PlaylistWithSongs> getSongsByPlaylist(String playlistId)
    {
        Observable<PlaylistWithSongs> memoryObservable=songsMemoryInteractorPlaylists.getSongs().toObservable();
        Observable<PlaylistWithSongs> databaseObservable=songsDatabaseInteractorPlaylists.getSongs(String.valueOf(playlistId)).toObservable();
        Observable<PlaylistWithSongs> networkObservable=songsNetworkInteractorPlaylists.getSongs(String.valueOf(playlistId)).toObservable();
        if(!isNetworkInProgress())
        {
            Log.d("SongsRepository","Network is not in progress, go ahead and check the observable");
            // I'll request the 3 observables and keep the entry that is returned first.
            dataProviderDisposable=Observable
                    .concat(memoryObservable,databaseObservable,networkObservable)
                    //.firstElement()
                    .subscribe((data)->{
                        Log.d("SongsRepository", "Concatenated the 3 Observables, received data for playlist "+data.getPlaylist().getId());
                        for(Song s:data.getSongs())
                        {
                            Log.d("SongsRepository","Received this song: "+s.getName());
                        }
                    },this::handleNonHttpException);
        }
        return songsMemoryInteractorPlaylists.getSongsObservable();
    }
}
