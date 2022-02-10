package ar.com.strellis.ampflower.data.datasource.network;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.subjects.ReplaySubject;

public class NetPlaylistsDataSourceFactory extends DataSource.Factory<String, Playlist> {

    private final MutableLiveData<NetPlaylistsPageKeyedDataSource> networkStatus;
    private final NetPlaylistsPageKeyedDataSource playlistsPageKeyedDataSource;
    public NetPlaylistsDataSourceFactory(AmpacheService service, LoginResponse settings) {
        this.networkStatus = new MutableLiveData<>();
        playlistsPageKeyedDataSource = new NetPlaylistsPageKeyedDataSource(service,settings);
    }


    @NonNull
    @Override
    public DataSource<String, Playlist> create() {
        networkStatus.postValue(playlistsPageKeyedDataSource);
        return playlistsPageKeyedDataSource;
    }

    public MutableLiveData<NetPlaylistsPageKeyedDataSource> getNetworkStatus() {
        return networkStatus;
    }

    public ReplaySubject<Playlist> getPlaylists() {
        return playlistsPageKeyedDataSource.getPlaylists();
    }

}
