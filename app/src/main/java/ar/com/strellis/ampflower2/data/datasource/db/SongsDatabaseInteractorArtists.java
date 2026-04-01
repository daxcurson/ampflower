package ar.com.strellis.ampflower2.data.datasource.db;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import ar.com.strellis.ampflower2.data.AmpacheDatabase;
import ar.com.strellis.ampflower2.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower2.data.model.ArtistSong;
import ar.com.strellis.ampflower2.data.model.ArtistWithSongs;
import ar.com.strellis.ampflower2.data.model.Song;
import io.reactivex.rxjava3.core.Maybe;

public class SongsDatabaseInteractorArtists extends SongsDatabaseInteractor<ArtistWithSongs>
{
    public SongsDatabaseInteractorArtists(AmpacheDatabase appDatabase, SongsMemoryInteractor<ArtistWithSongs> memoryInteractor) {
        super(appDatabase, memoryInteractor);
    }

    @Override
    public void saveData(ArtistWithSongs songs) {
        Log.d("SongsDatabaseInteractorArtists.saveData","Saving the retrieved data");
        List<ArtistSong> songsToInsert=new LinkedList<>();
        if(songs!=null) {
            for (Song s : songs.getSongs()) {
                ArtistSong artistSong = new ArtistSong();
                artistSong.setArtistId(songs.getArtist().getId());
                artistSong.setSongId(s.getId());
                Log.d("SongsDatabaseInteractorArtists.saveData", "Storing ArtistSong entity for this song: " + s.getName());
                songsToInsert.add(artistSong);
            }
            appDatabase.artistSongDao().insertAll(songsToInsert);
        }
    }

    @Override
    protected Maybe<ArtistWithSongs> getSongsObservable(String entityId) {
        return appDatabase.artistDao().listArtistSongsObservable(Integer.parseInt(entityId));
    }
}
