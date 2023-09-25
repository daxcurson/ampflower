package ar.com.strellis.ampflower.data.repository;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.datasource.network.AlbumPagingSourceRx;
import ar.com.strellis.ampflower.data.datasource.network.AlbumRemoteMediator;
import ar.com.strellis.ampflower.data.datasource.network.ArtistPagingSourceRx;
import ar.com.strellis.ampflower.data.datasource.network.ArtistRemoteMediator;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import ar.com.strellis.ampflower.networkutils.AmpacheUtil;
import io.reactivex.rxjava3.core.Flowable;
@ExperimentalPagingApi
public class ArtistsRepositoryRx {
    private static final int PAGE_SIZE=20;
    private static ArtistsRepositoryRx instance;
    private final AmpacheDatabase database;
    private final AmpacheService ampacheService;
    private AmpacheSettings ampacheSettings;
    private final LiveData<LoginResponse> loginResponse;
    private final MutableLiveData<NetworkState> loading;
    private LiveData<String> query;

    private ArtistsRepositoryRx(Context context, AmpacheService ampacheService, AmpacheSettings settings, LiveData<LoginResponse> loginResponse, LiveData<String> query, LifecycleOwner lifecycleOwner)
    {
        database = AmpacheDatabase.getDatabase(context.getApplicationContext());
        this.loginResponse=loginResponse;
        this.ampacheService=ampacheService;
        this.ampacheSettings=settings;
        loading=new MutableLiveData<>();
        this.query=query;
    }
    public static ArtistsRepositoryRx getInstance(Context context, AmpacheService ampacheService, AmpacheSettings settings, LiveData<LoginResponse> loginResponse, LiveData<String> query, LifecycleOwner lifecycleOwner){
        if(instance == null){
            instance = new ArtistsRepositoryRx(context,ampacheService,settings,loginResponse,query,lifecycleOwner);
        }
        return instance;
    }
    public Flowable<PagingData<Artist>> getArtists() {
        ArtistRemoteMediator mediator=new ArtistRemoteMediator(query,ampacheService,database);
        mediator.setLoginResponse(loginResponse);
        Pager<Integer,Artist> pager= new Pager<>(
                new PagingConfig(PAGE_SIZE, 1),
                0,
                mediator,
                ()->new ArtistPagingSourceRx(ampacheService,loginResponse, loading,query)
        );
        return PagingRx.getFlowable(pager);
    }
    public LiveData<NetworkState> getNetworkState()
    {
        return loading;
    }
    public String getImageUrl(int artistId)
    {
        // builds an URL of an image to be used by Picasso
        return AmpacheUtil.getImageUrl(artistId,"artist",ampacheSettings,loginResponse.getValue());
    }
}
