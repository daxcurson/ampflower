package ar.com.strellis.ampflower.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.datasource.network.ArtistRemote;
import ar.com.strellis.ampflower.data.datasource.network.NetArtistsDataSourceFactory;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import ar.com.strellis.ampflower.networkutils.AmpacheUtil;
import io.reactivex.schedulers.Schedulers;

public class ArtistsRepository {
    private static final String TAG = ArtistsRepository.class.getSimpleName();
    private static ArtistsRepository instance;
    private ArtistRemote network;
    final private AmpacheDatabase database;
    private MediatorLiveData<PagedList<Artist>> liveDataMerger;
    private LoginResponse loginResponse;
    private final AmpacheSettings ampacheSettings;

    @SuppressLint("CheckResult")
    public ArtistsRepository(Context context, AmpacheService ampacheService, AmpacheSettings settings, LoginResponse loginResponse, LiveData<String> query, LifecycleOwner lifecycleOwner)
    {
        // find the settings from the application
        // ... with a pretty little detail, if we don't have any network configuration,
        // we need to show only what we find in the database.
        network=null;
        liveDataMerger=null;
        this.ampacheSettings=settings;
        this.loginResponse=loginResponse;
        // So, we check here if the ampache details are configured.
        if(settings.getAmpacheUrl()!=null
                && !settings.getAmpacheUrl().equals("")
                && settings.getAmpacheUsername()!=null
                && !settings.getAmpacheUsername().equals(""))
        {
            NetArtistsDataSourceFactory dataSourceFactory = new NetArtistsDataSourceFactory(ampacheService,loginResponse,query,lifecycleOwner);

            PagedList.BoundaryCallback<Artist> boundaryCallback = new PagedList.BoundaryCallback<Artist>() {
                @Override
                public void onZeroItemsLoaded() {
                    super.onZeroItemsLoaded();
                    liveDataMerger.addSource(database.getArtists(), value -> {
                        liveDataMerger.setValue(value);
                        liveDataMerger.removeSource(database.getArtists());
                    });
                }
            };
            network = new ArtistRemote(dataSourceFactory, boundaryCallback);
            database = AmpacheDatabase.getDatabase(context.getApplicationContext());
            // Artists retrieved from the network will be stored in the database.
            liveDataMerger = new MediatorLiveData<>();
            liveDataMerger.addSource(network.getArtistsPaged(), value -> {
                liveDataMerger.setValue(value);
                Log.d(TAG, value.toString());
            });

            // save the movies into db
            dataSourceFactory.getArtists().
                    observeOn(Schedulers.io()).
                    subscribe(artist -> database.artistDao().insertArtist(artist));
        }
        else
        {
            // Load only what we have in the database
            database=AmpacheDatabase.getDatabase(context.getApplicationContext());
            liveDataMerger.addSource(database.getArtists(), value -> {
                liveDataMerger.setValue(value);
                liveDataMerger.removeSource(database.getArtists());
            });
        }
        // We should add a listener here, for when the server is configured or comes back
        // online again?
    }
    public static ArtistsRepository getInstance(Context context,AmpacheService ampacheService,AmpacheSettings settings,LoginResponse loginResponse, LiveData<String> query, LifecycleOwner lifecycleOwner){
        if(instance == null){
            instance = new ArtistsRepository(context,ampacheService,settings,loginResponse,query,lifecycleOwner);
        }
        else
        {
            // If they are passing a login response that is different than the one the instance has, I'll destroy that instance and create a new one.
            if(!instance.loginResponse.equals(loginResponse))
                instance=new ArtistsRepository(context,ampacheService,settings,loginResponse,query,lifecycleOwner);
        }
        return instance;
    }

    public LiveData<PagedList<Artist>> getArtists() {
        return liveDataMerger;
    }

    public LiveData<NetworkState> getNetworkState() {
        if(network==null)
            return new MutableLiveData<>();
        return network.getNetworkState();
    }

    public void setLoginResponse(LoginResponse loginResponse)
    {
        this.loginResponse=loginResponse;
    }
    public String getImageUrl(int artistId)
    {
        // builds an URL of an image to be used by Picasso
        return AmpacheUtil.getImageUrl(artistId,"artist",ampacheSettings,loginResponse);
    }
}
