package ar.com.strellis.ampflower.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.datasource.network.NetPlaylistsDataSourceFactory;
import ar.com.strellis.ampflower.data.datasource.network.PlaylistRemote;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistsRepository {
    private static final String TAG = PlaylistsRepository.class.getSimpleName();
    private static PlaylistsRepository instance;
    private PlaylistRemote network;
    final private AmpacheDatabase database;
    private MediatorLiveData<PagedList<Playlist>> liveDataMerger;
    private AmpacheService ampacheService;
    private LoginResponse loginResponse;

    @SuppressLint("CheckResult")
    public PlaylistsRepository(Context context, AmpacheService ampacheService, AmpacheSettings settings, LoginResponse loginResponse) {
        // find the settings from the application
        // ... with a pretty little detail, if we don't have any network configuration,
        // we need to show only what we find in the database.
        network=null;
        liveDataMerger= new MediatorLiveData<>();
        this.ampacheService=ampacheService;
        this.loginResponse=loginResponse;
        // So, we check here if the ampache details are configured.
        if(settings.getAmpacheUrl()!=null
                && !settings.getAmpacheUrl().equals("")
                && settings.getAmpacheUsername()!=null
                && !settings.getAmpacheUsername().equals(""))
        {
            NetPlaylistsDataSourceFactory dataSourceFactory = new NetPlaylistsDataSourceFactory(ampacheService,loginResponse);

            PagedList.BoundaryCallback<Playlist> boundaryCallback = new PagedList.BoundaryCallback<Playlist>() {
                @Override
                public void onZeroItemsLoaded() {
                    super.onZeroItemsLoaded();
                    liveDataMerger.addSource(database.getPlaylists(), value -> {
                        liveDataMerger.setValue(value);
                        liveDataMerger.removeSource(database.getPlaylists());
                    });
                }
            };
            network = new PlaylistRemote(dataSourceFactory, boundaryCallback);
            database = AmpacheDatabase.getDatabase(context.getApplicationContext());
            // Albums retrieved from the network will be stored in the database.
            liveDataMerger = new MediatorLiveData<>();
            liveDataMerger.addSource(network.getPlaylistsPaged(), value -> {
                liveDataMerger.setValue(value);
                Log.d(TAG, value.toString());
            });

            // save the movies into db
            dataSourceFactory.getPlaylists().
                    observeOn(Schedulers.io()).
                    subscribe(playlist -> database.playlistDao().insertPlaylist(playlist));
        }
        else
        {
            // Load only what we have in the database
            database=AmpacheDatabase.getDatabase(context.getApplicationContext());
            liveDataMerger.addSource(database.getPlaylists(), value -> {
                liveDataMerger.setValue(value);
                liveDataMerger.removeSource(database.getPlaylists());
            });
        }
        // We should add a listener here, for when the server is configured or comes back
        // online again?
    }
    public static PlaylistsRepository getInstance(Context context,AmpacheService ampacheService,AmpacheSettings settings,LoginResponse loginResponse){
        if(instance == null){
            instance = new PlaylistsRepository(context,ampacheService,settings,loginResponse);
        }
        else
        {
            if(!instance.loginResponse.equals(loginResponse))
                instance = new PlaylistsRepository(context,ampacheService,settings,loginResponse);
        }
        return instance;
    }

    public LiveData<PagedList<Playlist>> getPlaylists() {
        return liveDataMerger;
    }

    public LiveData<NetworkState> getNetworkState() {
        if(network==null)
            return new MutableLiveData<>();
        return network.getNetworkState();
    }
}
