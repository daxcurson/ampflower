package ar.com.strellis.ampflower.data.datasource.db;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower.data.model.AlbumSong;
import ar.com.strellis.ampflower.data.model.AlbumWithSongs;
import ar.com.strellis.ampflower.data.model.ArtistSong;
import ar.com.strellis.ampflower.data.model.ArtistWithSongs;
import ar.com.strellis.ampflower.data.model.Song;
import io.reactivex.Maybe;

public class SongsDatabaseInteractorArtists extends SongsDatabaseInteractor<ArtistWithSongs>
{
    public SongsDatabaseInteractorArtists(AmpacheDatabase appDatabase, SongsMemoryInteractor<ArtistWithSongs> memoryInteractor) {
        super(appDatabase, memoryInteractor);
    }

    @Override
    public void saveData(ArtistWithSongs songs) {
        Log.d("SongsDatabaseInteractorArtists.saveData","Saving the retrieved data");
        List<ArtistSong> songsToInsert=new LinkedList<>();
        for(Song s:songs.getSongs()) {
            ArtistSong artistSong = new ArtistSong();
            artistSong.setArtistId(songs.getArtist().getId());
            artistSong.setSongId(s.getId());
            Log.d("SongsDatabaseInteractorArtists.saveData", "Storing ArtistSong entity for this song: " + s.getName());
            songsToInsert.add(artistSong);
        }
        appDatabase.artistSongDao().insertAll(songsToInsert);
    }

    @Override
    protected Maybe<ArtistWithSongs> getSongsObservable(String entityId) {
        return appDatabase.artistDao().listArtistSongsObservable(Integer.parseInt(entityId));
    }
}
