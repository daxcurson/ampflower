package ar.com.strellis.ampflower.data.repository;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.Pager;
import androidx.paging.rxjava3.PagingRx;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.datasource.network.AlbumPagingSourceRx;
import ar.com.strellis.ampflower.data.datasource.network.AlbumRemoteMediator;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.core.Flowable;

public class AlbumsRepositoryRx {
    private static final String TAG = AlbumsRepositoryRx.class.getSimpleName();
    private static final int PAGE_SIZE=20;
    private static AlbumsRepositoryRx instance;
    private final AmpacheDatabase database;
    private final AmpacheService ampacheService;
    private AmpacheSettings ampacheSettings;
    private final LoginResponse loginResponse;
    private final MutableLiveData<NetworkState> loading;

    private AlbumsRepositoryRx(Context context,AmpacheService ampacheService,AmpacheSettings settings,LoginResponse loginResponse,LiveData<String> query,LifecycleOwner lifecycleOwner)
    {
        database = AmpacheDatabase.getDatabase(context.getApplicationContext());
        this.loginResponse=loginResponse;
        this.ampacheService=ampacheService;
        this.ampacheSettings=settings;
        loading=new MutableLiveData<>();
    }
    public static AlbumsRepositoryRx getInstance(Context context, AmpacheService ampacheService, AmpacheSettings settings, LoginResponse loginResponse, LiveData<String> query, LifecycleOwner lifecycleOwner){
        if(instance == null){
            instance = new AlbumsRepositoryRx(context,ampacheService,settings,loginResponse,query,lifecycleOwner);
        }
        return instance;
    }
    public Flowable<PagingData<Album>> getAlbums(String query) {
        AlbumRemoteMediator mediator=new AlbumRemoteMediator(query,ampacheService,database);
        mediator.setLoginResponse(loginResponse);
        Pager<Integer,Album> pager= new Pager<>(
                new PagingConfig(PAGE_SIZE, 1),
                1,
                ()->new AlbumPagingSourceRx(ampacheService,loginResponse, loading)
        );
        return PagingRx.getFlowable(pager);
    }
    public LiveData<NetworkState> getNetworkState()
    {
        return loading;
    }
}