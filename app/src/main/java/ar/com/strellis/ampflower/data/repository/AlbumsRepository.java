package ar.com.strellis.ampflower.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.datasource.network.AlbumRemote;
import ar.com.strellis.ampflower.data.datasource.network.NetAlbumsDataSourceFactory;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.schedulers.Schedulers;

public class AlbumsRepository {
    private static final String TAG = AlbumsRepository.class.getSimpleName();
    private static AlbumsRepository instance;
    private AlbumRemote network;
    final private AmpacheDatabase database;
    private MediatorLiveData<PagedList<Album>> liveDataMerger;
    private AmpacheService ampacheService;
    private LoginResponse loginResponse;

    @SuppressLint("CheckResult")
    private AlbumsRepository(Context context, AmpacheService ampacheService, AmpacheSettings settings, LoginResponse loginResponse) {

        // find the settings from the application
        // ... with a pretty little detail, if we don't have any network configuration,
        // we need to show only what we find in the database.
        network=null;
        liveDataMerger=null;
        this.ampacheService=ampacheService;
        this.loginResponse=loginResponse;
        // So, we check here if the ampache details are configured.
        if(settings.getAmpacheUrl()!=null
                && !settings.getAmpacheUrl().equals("")
                && settings.getAmpacheUsername()!=null
                && !settings.getAmpacheUsername().equals(""))
        {
            NetAlbumsDataSourceFactory dataSourceFactory = new NetAlbumsDataSourceFactory(ampacheService,loginResponse);

            PagedList.BoundaryCallback<Album> boundaryCallback = new PagedList.BoundaryCallback<Album>() {
                @Override
                public void onZeroItemsLoaded() {
                    super.onZeroItemsLoaded();
                    liveDataMerger.addSource(database.getAlbums(), value -> {
                        liveDataMerger.setValue(value);
                        liveDataMerger.removeSource(database.getAlbums());
                    });
                }
            };
            network = new AlbumRemote(dataSourceFactory, boundaryCallback);
            database = AmpacheDatabase.getDatabase(context.getApplicationContext());
            // Albums retrieved from the network will be stored in the database.
            liveDataMerger = new MediatorLiveData<>();
            liveDataMerger.addSource(network.getAlbumsPaged(), value -> {
                liveDataMerger.setValue(value);
                Log.d(TAG, value.toString());
            });

            // save the movies into db
            dataSourceFactory.getAlbums().
                    observeOn(Schedulers.io()).
                    subscribe(album -> database.albumDao().insertAlbum(album));
        }
        else
        {
            // Load only what we have in the database
            database=AmpacheDatabase.getDatabase(context.getApplicationContext());
            liveDataMerger = new MediatorLiveData<>();
            liveDataMerger.addSource(database.getAlbums(), value -> {
                liveDataMerger.setValue(value);
                liveDataMerger.removeSource(database.getAlbums());
            });
        }
        // We should add a listener here, for when the server is configured or comes back
        // online again?
    }

    public static AlbumsRepository getInstance(Context context,AmpacheService ampacheService,AmpacheSettings settings,LoginResponse loginResponse){
        if(instance == null){
            instance = new AlbumsRepository(context,ampacheService,settings,loginResponse);
        }
        return instance;
    }

    public LiveData<PagedList<Album>> getAlbums(){
        return  liveDataMerger;
    }

    public LiveData<NetworkState> getNetworkState() {
        // If we don't have any user settings for the server, it will be impossible to obtain any network
        // state, in fact, it will be null. I'll return an empty LiveData object.
        if(network==null)
            return new MutableLiveData<>();
        return network.getNetworkState();
    }
    public void setLoginResponse(LoginResponse loginResponse)
    {
        this.loginResponse=loginResponse;
    }
}
