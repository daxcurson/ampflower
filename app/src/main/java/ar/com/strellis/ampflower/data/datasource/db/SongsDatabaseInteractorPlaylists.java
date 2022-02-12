package ar.com.strellis.ampflower.data.datasource.db;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.datasource.memory.SongsMemoryInteractor;
import ar.com.strellis.ampflower.data.model.AlbumSong;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.model.PlaylistSong;
import ar.com.strellis.ampflower.data.model.PlaylistWithSongs;
import ar.com.strellis.ampflower.data.model.Song;
import io.reactivex.rxjava3.core.Maybe;

public class SongsDatabaseInteractorPlaylists extends SongsDatabaseInteractor<PlaylistWithSongs>
{
    public SongsDatabaseInteractorPlaylists(AmpacheDatabase appDatabase, SongsMemoryInteractor<PlaylistWithSongs> memoryInteractor) {
        super(appDatabase, memoryInteractor);
    }

    @Override
    public void saveData(PlaylistWithSongs songs) {
        Log.d("SongsDatabaseInteractorPlaylist.saveData","Saving the retrieved data");
        List<PlaylistSong> songsToInsert=new LinkedList<>();
        for(Song s:songs.getSongs()) {
            PlaylistSong playlistSong = new PlaylistSong();
            playlistSong.setPlaylistId(songs.getPlaylist().getId());
            playlistSong.setSongId(s.getId());
            Log.d("SongsDatabaseInteractorPlaylist.saveData", "Storing PlaylistSong entity for this song: " + s.getName());
            songsToInsert.add(playlistSong);
        }
        appDatabase.playlistSongDao().insertAll(songsToInsert);
    }

    @Override
    protected Maybe<PlaylistWithSongs> getSongsObservable(String entityId) {
        return appDatabase.playlistDao().listPlaylistSongsObservable(entityId);
    }
}
