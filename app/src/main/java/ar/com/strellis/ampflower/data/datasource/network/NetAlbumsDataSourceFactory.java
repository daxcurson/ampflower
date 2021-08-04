package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.subjects.ReplaySubject;

public class NetAlbumsDataSourceFactory extends DataSource.Factory<String, Album> {

    private final MutableLiveData<NetAlbumsPageKeyedDataSource> networkStatus;
    private NetAlbumsPageKeyedDataSource albumsPageKeyedDataSource;
    private LoginResponse loginResponse;

    public NetAlbumsDataSourceFactory(AmpacheService service, LoginResponse settings, LiveData<String> query, LifecycleOwner lifecycleOwner) {
        this.networkStatus = new MutableLiveData<>();
        albumsPageKeyedDataSource = new NetAlbumsPageKeyedDataSource(service,settings,query);
        this.loginResponse=settings;
        query.observe(lifecycleOwner, newQuery -> {
            Log.d("NetAlbumsDataSourceFactory","About to invalidate the albums datasource");
            albumsPageKeyedDataSource.invalidate();
            albumsPageKeyedDataSource = new NetAlbumsPageKeyedDataSource(service,loginResponse,query);
        });

    }


    @NonNull
    @Override
    public DataSource<String,Album> create() {
        networkStatus.postValue(albumsPageKeyedDataSource);
        return albumsPageKeyedDataSource;
    }

    public MutableLiveData<NetAlbumsPageKeyedDataSource> getNetworkStatus() {
        return networkStatus;
    }

    public ReplaySubject<Album> getAlbums() {
        return albumsPageKeyedDataSource.getAlbums();
    }

    public void setLoginResponse(LoginResponse loginResponse) {
        this.loginResponse=loginResponse;
    }
}