package ar.com.strellis.ampflower.data.datasource.network;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.subjects.ReplaySubject;

public class NetArtistsDataSourceFactory extends DataSource.Factory<String, Artist>{
    private final MutableLiveData<NetArtistsPageKeyedDataSource> networkStatus;
    private final NetArtistsPageKeyedDataSource artistsPageKeyedDataSource;
    public NetArtistsDataSourceFactory(AmpacheService service, LoginResponse settings) {
        this.networkStatus = new MutableLiveData<>();
        artistsPageKeyedDataSource = new NetArtistsPageKeyedDataSource(service,settings);
    }


    @NonNull
    @Override
    public DataSource<String, Artist> create() {
        networkStatus.postValue(artistsPageKeyedDataSource);
        return artistsPageKeyedDataSource;
    }

    public MutableLiveData<NetArtistsPageKeyedDataSource> getNetworkStatus() {
        return networkStatus;
    }

    public ReplaySubject<Artist> getArtists() {
        return artistsPageKeyedDataSource.getArtists();
    }
}
