package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.subjects.ReplaySubject;

public class NetArtistsDataSourceFactory extends DataSource.Factory<String, Artist>{

    private final MutableLiveData<NetArtistsPageKeyedDataSource> networkStatus;
    private NetArtistsPageKeyedDataSource artistsPageKeyedDataSource;
    private final LoginResponse loginResponse;

    public NetArtistsDataSourceFactory(AmpacheService service, LoginResponse settings, LiveData<String> query, LifecycleOwner lifecycleOwner) {
        this.networkStatus = new MutableLiveData<>();
        artistsPageKeyedDataSource = new NetArtistsPageKeyedDataSource(service,settings,query);
        this.loginResponse=settings;
        query.observe(lifecycleOwner, newQuery -> {
            Log.d("NetArtistsDataSourceFactory","About to invalidate the artists datasource");
            artistsPageKeyedDataSource.invalidate();
            artistsPageKeyedDataSource = new NetArtistsPageKeyedDataSource(service,loginResponse,query);
        });

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
