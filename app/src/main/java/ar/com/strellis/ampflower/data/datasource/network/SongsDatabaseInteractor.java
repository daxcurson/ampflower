package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower.data.model.AlbumSong;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.Song;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

public class SongsDatabaseInteractor
{
    private final AmpacheDatabase appDatabase;
    private final SongsMemoryInteractor memoryInteractor;

    public SongsDatabaseInteractor(AmpacheDatabase appDatabase,SongsMemoryInteractor memoryInteractor)
    {
        this.appDatabase=appDatabase;
        this.memoryInteractor=memoryInteractor;
    }

    public Maybe<AlbumWithSongs> getSongs(int album_id)
    {
        Log.d("SongsDatabaseInteractor.getSongs","Getting the songs from the database");
        return appDatabase.albumDao().listAlbumSongsObservable(album_id)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(albumWithSongs -> {
                    Log.d("SongsDatabaseInteractor.getSongs$onSuccess","I have some songs: "+albumWithSongs.getSongs().size()+" songs");
                    memoryInteractor.saveData(albumWithSongs);
                });
    }
    public void saveData(AlbumWithSongs songs)
    {
        Log.d("SongsDatabaseInteractor.saveData","Saving the retrieved data");
        List<AlbumSong> songsToInsert=new LinkedList<>();
        for(Song s:songs.getSongs()) {
            AlbumSong albumSong = new AlbumSong();
            albumSong.setAlbumId(songs.getAlbum().getId());
            albumSong.setSongId(s.getId());
            Log.d("SongsDatabaseInteractor.saveData", "Storing AlbumSong entity for this song: " + s.getName());
            songsToInsert.add(albumSong);
        }
        appDatabase.albumSongDao().insertAll(songsToInsert);
    }
}
