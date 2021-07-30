package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import ar.com.strellis.ampflower.networkutils.AmpacheUtil;
import io.reactivex.subjects.ReplaySubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetAlbumsPageKeyedDataSource extends PageKeyedDataSource<String, Album> {

    private static final String TAG = NetAlbumsPageKeyedDataSource.class.getSimpleName();
    private final AmpacheService ampacheService;
    private final MutableLiveData<NetworkState> networkState;
    private final ReplaySubject<Album> albumsObservable;
    private LoginResponse loginResponse;


    public NetAlbumsPageKeyedDataSource(AmpacheService service, LoginResponse login) {
        ampacheService = service;
        networkState = new MutableLiveData<>();
        albumsObservable = ReplaySubject.create();
        this.loginResponse=login;
    }

    public void setLoginResponse(LoginResponse login)
    {
        this.loginResponse=login;
    }
    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public ReplaySubject<Album> getAlbums() {
        return albumsObservable;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull final LoadInitialCallback<String, Album> callback) {
        Log.i(TAG, "Loading Initial Range, Count " + params.requestedLoadSize);

        int limit=params.requestedLoadSize;
        networkState.postValue(NetworkState.LOADING);
        int offset=0;
        Log.d(TAG,"Attempting to connect, auth: "+loginResponse.getAuth());
        Call<List<Album>> callBack = ampacheService.get_indexes_album(loginResponse.getAuth(),"",offset,limit);
        callBack.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(@NonNull Call<List<Album>> call, @NonNull Response<List<Album>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG,"We have a successful answer!");
                    assert response.body() != null;
                    callback.onResult(response.body(), null, "1");
                    networkState.postValue(NetworkState.LOADED);
                    List<Album> results=response.body();
                    Log.d(TAG,"There are "+results.size()+" albums retrieved");
                    results.forEach(albumsObservable::onNext);
                    Log.d(TAG,"Done loading albums");
                } else {
                    Log.e("API CALL", response.message());
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Album>> call, @NonNull Throwable t) {
                String errorMessage;
                if (t.getMessage() == null) {
                    errorMessage = "unknown error";
                } else {
                    errorMessage = t.getMessage();
                }
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                callback.onResult(new ArrayList<>(), null, "1");
            }
        });
    }



    @Override
    public void loadAfter(@NonNull LoadParams<String> params, final @NonNull LoadCallback<String, Album> callback) {
        Log.i(TAG, "Loading page " + params.key );
        networkState.postValue(NetworkState.LOADING);
        final AtomicInteger page = new AtomicInteger(0);
        try {
            page.set(Integer.parseInt(params.key));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        int limit=params.requestedLoadSize;
        int offset=(page.get())*params.requestedLoadSize;
        Log.d(TAG,"Page="+page.get()+", offset="+offset+", limit="+limit);
        Log.d(TAG,"Attempting to connect, auth: "+loginResponse.getAuth());
        Call<List<Album>> callBack = ampacheService.get_indexes_album(loginResponse.getAuth(),"",offset,limit);
        callBack.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(@NonNull Call<List<Album>> call, @NonNull Response<List<Album>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    callback.onResult(response.body(),Integer.toString(page.get()+1));
                    networkState.postValue(NetworkState.LOADED);
                    response.body().forEach(albumsObservable::onNext);
                } else {
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                    Log.e("API CALL", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Album>> call, @NonNull Throwable t) {
                String errorMessage;
                if (t.getMessage() == null) {
                    errorMessage = "unknown error";
                } else {
                    errorMessage = t.getMessage();
                }
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                callback.onResult(new ArrayList<>(),Integer.toString(page.get()));
            }
        });
    }


    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, Album> callback) {

    }
}
