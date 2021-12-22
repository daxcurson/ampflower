package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.model.PlaylistListResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.subjects.ReplaySubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetPlaylistsPageKeyedDataSource extends PageKeyedDataSource<String, Playlist> {

    private static final String TAG = NetPlaylistsPageKeyedDataSource.class.getSimpleName();
    private final AmpacheService ampacheService;
    private final MutableLiveData<NetworkState> networkState;
    private final ReplaySubject<Playlist> playlistsObservable;
    private final LoginResponse loginResponse;

    public NetPlaylistsPageKeyedDataSource(AmpacheService service, LoginResponse login) {
        ampacheService = service;
        networkState = new MutableLiveData<>();
        playlistsObservable = ReplaySubject.create();
        this.loginResponse=login;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public ReplaySubject<Playlist> getPlaylists() {
        return playlistsObservable;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull final LoadInitialCallback<String, Playlist> callback) {
        Log.i(TAG, "Loading Initial Range of playlists, Count " + params.requestedLoadSize);

        int limit=params.requestedLoadSize;
        networkState.postValue(NetworkState.LOADING);
        int offset=0;
        Log.d(TAG,"Attempting to connect, auth: "+loginResponse.getAuth());
        Call<PlaylistListResponse> callBack = ampacheService.get_indexes_playlist(loginResponse.getAuth(),"",offset,limit);
        callBack.enqueue(new Callback<PlaylistListResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlaylistListResponse> call, @NonNull Response<PlaylistListResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG,"We have a successful answer!");
                    assert response.body() != null;
                    callback.onResult(response.body().getPlaylist(), null, "1");
                    networkState.postValue(NetworkState.LOADED);
                    List<Playlist> results=response.body().getPlaylist();
                    Log.d(TAG,"There are "+results.size()+" playlists retrieved");
                    results.forEach(playlistsObservable::onNext);
                    Log.d(TAG,"Done loading playlists");
                } else {
                    Log.e(TAG, "The response is not successful: "+response.message());
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaylistListResponse> call, @NonNull Throwable t) {
                String errorMessage;
                if (t.getMessage() == null) {
                    errorMessage = "unknown error";
                } else {
                    errorMessage = t.getMessage();
                }
                Log.e(TAG,"Error while reading playlists: "+errorMessage);
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                callback.onResult(new ArrayList<>(), null, "1");
            }
        });
    }



    @Override
    public void loadAfter(@NonNull LoadParams<String> params, final @NonNull LoadCallback<String, Playlist> callback) {
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
        Call<PlaylistListResponse> callBack = ampacheService.get_indexes_playlist(loginResponse.getAuth(),"",offset,limit);
        callBack.enqueue(new Callback<PlaylistListResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlaylistListResponse> call, @NonNull Response<PlaylistListResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    callback.onResult(response.body().getPlaylist(),Integer.toString(page.get()+1));
                    networkState.postValue(NetworkState.LOADED);
                    response.body().getPlaylist().forEach(playlistsObservable::onNext);
                } else {
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                    Log.e("API CALL", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaylistListResponse> call, @NonNull Throwable t) {
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
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, Playlist> callback) {

    }
}
