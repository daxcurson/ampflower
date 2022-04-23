package ar.com.strellis.ampflower.data.datasource.db;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower.data.model.AlbumSong;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.Song;
import io.reactivex.rxjava3.core.Maybe;

public class SongsDatabaseInteractorAlbums extends SongsDatabaseInteractor<AlbumWithSongs>
{
    public SongsDatabaseInteractorAlbums(AmpacheDatabase appDatabase, SongsMemoryInteractor<AlbumWithSongs> memoryInteractor) {
        super(appDatabase, memoryInteractor);
    }

    @Override
    protected Maybe<AlbumWithSongs> getSongsObservable(String entityId) {
        return appDatabase.albumDao().listAlbumSongsObservable(Integer.parseInt(entityId));
    }
    public void saveData(AlbumWithSongs songs)
    {
        Log.d("SongsDatabaseInteractorAlbums.saveData","Saving the retrieved data");
        List<AlbumSong> songsToInsert=new LinkedList<>();
        if(songs.getSongs()!=null) {
            for (Song s : songs.getSongs()) {
                AlbumSong albumSong = new AlbumSong();
                albumSong.setAlbumId(songs.getAlbum().getId());
                albumSong.setSongId(s.getId());
                Log.d("SongsDatabaseInteractorAlbums.saveData", "Storing AlbumSong entity for this song: " + s.getName());
                songsToInsert.add(albumSong);
            }
            appDatabase.albumSongDao().insertAll(songsToInsert);
        }
        else
        {
            Log.d("SongsDatabaseInteractorAlbums.saveData","An error occurred when saving the list of songs for an album");
        }
    }
}
